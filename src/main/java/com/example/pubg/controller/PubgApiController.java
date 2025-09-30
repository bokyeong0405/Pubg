package com.example.pubg.controller;

import com.example.pubg.dto.MatchDetailResponse;
import com.example.pubg.service.PubgService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pubg")
@CrossOrigin(origins = "http://localhost:3000") // CORS 설정 유지
public class PubgApiController {

    private final PubgService pubgService;

    // 분석 작업 시작 API
    @PostMapping("/analysis-jobs")
    public ResponseEntity<Map<String, String>> createAnalysisJob(@RequestBody String matchData) {
        String jobId = pubgService.startAnalysisJob(matchData);
        return ResponseEntity.accepted().body(Map.of("jobId", jobId));
    }

    // 분석 결과 조회 API
    @GetMapping("/analysis-jobs/{jobId}")
    public ResponseEntity<Map<String, String>> getAnalysisJob(@PathVariable String jobId) {
        String result = pubgService.getJobResult(jobId);
        String status = "PENDING";
        String analysis = null;

        if ("NOT_FOUND".equals(result)) {
            return ResponseEntity.notFound().build();
        } else if (result.startsWith("ERROR:")) {
            status = "ERROR";
            analysis = result;
        } else if (!"PENDING".equals(result)) {
            status = "COMPLETED";
            analysis = result;
        }

        return ResponseEntity.ok(Map.of("status", status, "analysis", analysis == null ? "" : analysis));
    }

    // 기존의 다른 API 엔드포인트들은 유지 (생략)
    @GetMapping("/search")
    public PlayerSearchResponse searchRecentMatches(@RequestParam String nickname,
                                                    @RequestParam(defaultValue = "0") int page) {

        String accountId = pubgService.getPlayerByNickname(nickname);
        List<String> allMatches = pubgService.getMatchByAccountId(accountId);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allMatches.size());
        List<String> pagedMatchIds = allMatches.subList(start, end);

        List<MatchDetailResponse> matchSummaries = pagedMatchIds.stream()
                .map(id -> pubgService.getMatchSummaryDto(id, nickname))
                .collect(Collectors.toList());

        Page<MatchDetailResponse> matchPage = new PageImpl<>(matchSummaries, pageable, allMatches.size());

        return new PlayerSearchResponse(nickname, matchPage);
    }

    @GetMapping("/matches/{matchId}")
    public List<MatchDetailResponse.Included.Attributes.Stats> getMatchStats(@PathVariable String matchId) {
        return pubgService.getMatchStatsByMatchId(matchId);
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class PlayerSearchResponse {
        private String nickname;
        private org.springframework.data.domain.Page<com.example.pubg.dto.MatchDetailResponse> matchPage;
    }
}