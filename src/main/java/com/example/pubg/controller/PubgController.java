package com.example.pubg.controller;

import com.example.pubg.dto.MatchDetailResponse;
import com.example.pubg.service.PubgService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pubg")
public class PubgController {

    private final PubgService pubgService;

    public PubgController(PubgService pubgService) {
        this.pubgService = pubgService;
    }

    @GetMapping("/")
    public String mainPage() {
        return "index"; // 검색창 메인 화면
    }

    @GetMapping("/player")
    public String getPlayer(@RequestParam String nickname) {
        return pubgService.getPlayerByNickname(nickname);
    }

    @GetMapping("/matches")
    public List<String> getMatches(@RequestParam String accountId) {
        return pubgService.getMatchByAccountId(accountId);
    }

    @GetMapping("/match-stats")
    public List<MatchDetailResponse.Included.Attributes.Stats> getMatchStats(@RequestParam String matchId) {
        return pubgService.getMatchStatsByMatchId(matchId);
    }

    @GetMapping("/match-view")
    public String viewMatchStats(@RequestParam String matchId,
                                 @RequestParam(required = false) String nickname,
                                 Model model) {

        List<MatchDetailResponse.Included.Attributes.Stats> stats = pubgService.getMatchStatsByMatchId(matchId);

        // 1. 팀별로 그룹핑
        Map<Integer, List<MatchDetailResponse.Included.Attributes.Stats>> teamStats = new HashMap<>();

        if (stats != null) {
            teamStats = stats.stream()
                    .collect(Collectors.groupingBy(MatchDetailResponse.Included.Attributes.Stats::getTeamId));
        }

        // 딜량바 용
        double maxDamage = stats.stream()
                .mapToDouble(MatchDetailResponse.Included.Attributes.Stats::getDamageDealt)
                .max()
                .orElse(1.0); // 0으로 나누지 않도록 안전값

        // 2. 각 팀 내부 플레이어를 생존 시간 순으로 정렬
        teamStats.values().forEach(
                list -> list.sort((s1, s2) -> Double.compare(s2.getTimeSurvived(), s1.getTimeSurvived()))
        );

        // 3. 우승팀: winPlace == 1 인 플레이어의 teamId
        int winnerTeamId = stats.stream()
                .filter(s -> s.getWinPlace() == 1)
                .map(MatchDetailResponse.Included.Attributes.Stats::getTeamId)
                .findFirst()
                .orElse(-1);

        // 4. 검색된 플레이어의 팀 ID (닉네임 대소문자 무시)
        int searchedPlayerTeamId = stats.stream()
                .filter(s -> nickname != null && s.getName().equalsIgnoreCase(nickname))
                .map(MatchDetailResponse.Included.Attributes.Stats::getTeamId)
                .findFirst()
                .orElse(-1);

        // 5. model에 전달
        model.addAttribute("matchId", matchId);
        model.addAttribute("nickname", nickname);
        model.addAttribute("teamStats", teamStats);  // ← Thymeleaf에서 entrySet으로 순회할 것
        model.addAttribute("winnerTeamId", winnerTeamId);
        model.addAttribute("searchedPlayerTeamId", searchedPlayerTeamId);
        model.addAttribute("maxDamage", maxDamage);

        return "match_stats";
    }

    @GetMapping("/search")
    public String searchRecentMatches(@RequestParam String nickname,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {

        String accountId = pubgService.getPlayerByNickname(nickname);
        List<String> allMatches = pubgService.getMatchByAccountId(accountId);

        int pageSize = 5;
        int fromIndex = Math.min(page * pageSize, allMatches.size());
        int toIndex = Math.min(fromIndex + pageSize, allMatches.size());
        List<String> pagedMatchIds = allMatches.subList(fromIndex, toIndex);

        List<MatchDetailResponse> matchSummaries = pagedMatchIds.stream()
                .map(id -> pubgService.getMatchSummaryDto(id, nickname))
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, pageSize);
        PageImpl<MatchDetailResponse> matchPage = new PageImpl<>(matchSummaries, pageable, allMatches.size());

        model.addAttribute("nickname", nickname);
        model.addAttribute("matchIds", pagedMatchIds); // 클릭용
        model.addAttribute("matchPage", matchPage);
        return "player_matches";
    }
}