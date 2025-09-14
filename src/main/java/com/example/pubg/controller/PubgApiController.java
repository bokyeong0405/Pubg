package com.example.pubg.controller;

import com.example.pubg.dto.MatchDetailResponse;
import com.example.pubg.service.PubgService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pubg")
@CrossOrigin(origins = "http://localhost:3000")
public class PubgApiController {

    private final PubgService pubgService;

    public PubgApiController(PubgService pubgService) {
        this.pubgService = pubgService;
    }

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

    @Getter
    @AllArgsConstructor
    public static class PlayerSearchResponse {
        private String nickname;
        private Page<MatchDetailResponse> matchPage;
    }
}
