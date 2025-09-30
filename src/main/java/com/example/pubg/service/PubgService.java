package com.example.pubg.service;

import com.example.pubg.dto.MatchDetailResponse;
import com.example.pubg.dto.PlayerDetailResponse;
import com.example.pubg.dto.PlayerListResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubgService {
    private final WebClient.Builder webClientBuilder;
    private static final String PYTHON_API_URL = "http://127.0.0.1:8000";

    public String getAnalysis(String matchData) {
        WebClient webClient = webClientBuilder.baseUrl(PYTHON_API_URL).build();
        try {
            Map<String, Object> responseMap = webClient.post()
                    .uri("/analyze")
                    .bodyValue(Map.of("match_info", matchData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (responseMap != null && responseMap.containsKey("error")) {
                return "PYTHON_ERROR: " + responseMap.get("error");
            } else if (responseMap != null && responseMap.containsKey("analysis")) {
                return (String) responseMap.get("analysis");
            } else {
                return "ERROR: Unknown response from Python server";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }

    // 기존 메소드들은 유지 (생략)
    @Value("${pubg.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getPlayerByNickname(String nickname) {
        String url = "https://api.pubg.com/shards/steam/players?filter[playerNames]=" + nickname;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/vnd.api+json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            PlayerListResponse result = objectMapper.readValue(response.getBody(), PlayerListResponse.class);
            return result.getData().get(0).getId(); // accountId
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse accountId", e);
        }
    }

    public List<String> getMatchByAccountId(String accountId) {
        String url = "https://api.pubg.com/shards/steam/players/" + accountId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/vnd.api+json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            PlayerDetailResponse result = objectMapper.readValue(response.getBody(), PlayerDetailResponse.class);
            return result.getData().getRelationships().getMatches().getData()
                    .stream().map(m -> m.getId()).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse matchId list", e);
        }
    }

    public List<MatchDetailResponse.Included.Attributes.Stats> getMatchStatsByMatchId(String matchId) {
        return extractStatsFromMatch(matchId);
    }

    public Map<Integer, List<MatchDetailResponse.Included.Attributes.Stats>> getGroupedMatchStatsByMatchId(String matchId) {
        List<MatchDetailResponse.Included.Attributes.Stats> stats = extractStatsFromMatch(matchId);

        return stats.stream()
                .sorted(Comparator.comparingInt(MatchDetailResponse.Included.Attributes.Stats::getWinPlace))
                .collect(Collectors.groupingBy(MatchDetailResponse.Included.Attributes.Stats::getTeamId,
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    private List<MatchDetailResponse.Included.Attributes.Stats> extractStatsFromMatch(String matchId) {
        String url = "https://api.pubg.com/shards/steam/matches/" + matchId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/vnd.api+json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode included = root.get("included");

            Map<String, Integer> participantIdToTeamId = new HashMap<>();
            int teamCounter = 1;

            for (JsonNode node : included) {
                if (node.get("type").asText().equals("roster")) {
                    for (JsonNode p : node.get("relationships").get("participants").get("data")) {
                        participantIdToTeamId.put(p.get("id").asText(), teamCounter);
                    }
                    teamCounter++;
                }
            }

            List<MatchDetailResponse.Included.Attributes.Stats> results = new ArrayList<>();
            for (JsonNode node : included) {
                if (node.get("type").asText().equals("participant")) {
                    JsonNode attr = node.get("attributes");
                    JsonNode stats = attr.get("stats");
                    MatchDetailResponse.Included.Attributes.Stats s = new MatchDetailResponse.Included.Attributes.Stats();
                    s.setName(stats.get("name").asText());
                    s.setKills(stats.get("kills").asInt());
                    s.setDamageDealt(stats.get("damageDealt").asDouble());
                    s.setTimeSurvived(stats.get("timeSurvived").asDouble());
                    s.setWinPlace(stats.get("winPlace").asInt());
                    s.setAssists(stats.get("assists").asInt());
                    s.setDBNOs(stats.get("DBNOs").asInt());
                    s.setTeamId(participantIdToTeamId.getOrDefault(node.get("id").asText(), 0));
                    results.add(s);
                }
            }

            return results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse match detail with teams", e);
        }
    }

    public MatchDetailResponse.Included.Attributes.Stats getPlayerStatsFromMatch(String matchId, String nickname) {
        List<MatchDetailResponse.Included.Attributes.Stats> allStats = getMatchStatsByMatchId(matchId);

        return allStats.stream()
                .filter(s -> s.getName().equalsIgnoreCase(nickname))
                .findFirst()
                .orElse(null);
    }

    public MatchDetailResponse getMatchSummaryDto(String matchId, String nickname) {
        String url = "https://api.pubg.com/shards/steam/matches/" + matchId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/vnd.api+json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            MatchDetailResponse matchDetail = objectMapper.readValue(response.getBody(), MatchDetailResponse.class);
            String gameMode = matchDetail.getData().getAttributes().getGameMode();
            MatchDetailResponse.Included.Attributes.Stats stat = matchDetail.getIncluded().stream()
                    .filter(i -> "participant".equals(i.getType()) && nickname.equalsIgnoreCase(i.getAttributes().getStats().getName()))
                    .map(i -> i.getAttributes().getStats())
                    .findFirst()
                    .orElseThrow();

            MatchDetailResponse dto = new MatchDetailResponse();
            dto.setMatchId(matchId);
            dto.setKills(stat.getKills());
            dto.setAssists(stat.getAssists());
            dto.setDBNOs(stat.getDBNOs());
            dto.setDamageDealt(stat.getDamageDealt());
            dto.setTimeSurvived(stat.getTimeSurvived());
            dto.setWinPlace(stat.getWinPlace());
            dto.setGameMode(translateGameMode(gameMode));
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse match summary", e);
        }
    }

    private String translateGameMode(String mode) {
        return switch (mode) {
            case "solo" -> "솔로";
            case "solo-fpp" -> "솔로(FPP)";
            case "duo" -> "듀오";
            case "duo-fpp" -> "듀오(FPP)";
            case "squad" -> "스쿼드";
            case "squad-fpp" -> "스쿼드(FPP)";
            case "war" -> "아케이드";
            case "normal" -> "일반전";
            default -> mode;
        };
    }
}