package com.example.pubg.controller;

import com.example.pubg.service.PubgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
}