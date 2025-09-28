package com.example.pubg.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PubgService {
    private final WebClient.Builder webClientBuilder;
    private static final String PYTHON_API_URL = "http://127.0.0.1:8000";

    // Job 상태와 결과를 저장할 인메모리 맵
    private final Map<String, String> jobResults = new ConcurrentHashMap<>();

    public String startAnalysisJob(String matchData) {
        String jobId = UUID.randomUUID().toString();
        jobResults.put(jobId, "PENDING"); // 초기 상태를 PENDING으로 설정
        processAnalysis(jobId, matchData);
        return jobId;
    }

    @Async // 이 메소드를 별도의 스레드에서 비동기적으로 실행
    public void processAnalysis(String jobId, String matchData) {
        WebClient webClient = webClientBuilder.baseUrl(PYTHON_API_URL).build();
        try {
            String analysisResult = webClient.post()
                    .uri("/analyze")
                    .bodyValue(Map.of("match_info", matchData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> (String) response.get("analysis"))
                    .block(); // 비동기 호출을 동기적으로 기다림 (주의: @Async 메소드 내에서만 사용)

            jobResults.put(jobId, analysisResult);
        } catch (Exception e) {
            jobResults.put(jobId, "ERROR: " + e.getMessage());
        }
    }

    public String getJobResult(String jobId) {
        return jobResults.getOrDefault(jobId, "NOT_FOUND");
    }

    // 기존 메소드들은 유지 (생략)
}