package com.example.movierecommendations.AIMODEL;

import com.example.movierecommendations.recommended.service.HrmRecommendationService;
import com.example.movierecommendations.recommended.service.LlmRecommendationService;
import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AIModelService {

    private static final Logger logger = LoggerFactory.getLogger(AIModelService.class);

    private final LlmRecommendationService llmRecommendationService;
    private final HrmRecommendationService hrmRecommendationService;
    private final MemberRepository memberRepository;

    // 비동기적으로 Python AI 모델을 호출하여 추천 결과를 가져오는 메서드
    @Async
    public CompletableFuture<List<String>> callHRMModel(Map<String, Object> inputData, Long memberId) throws IOException {
        logger.info("Calling Python AI model with input data: {}, for memberId: {}", inputData, memberId);

        String jsonData = new ObjectMapper().writeValueAsString(inputData);
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "recomsystem/hrm.py", jsonData);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python AI model execution failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error while executing Python model: {}", e.getMessage());
            throw new RuntimeException("Error while executing Python model", e);
        }

        // 결과가 정상적으로 오면 추천 결과를 반환
        List<String> recommendedMovies = parseHRMRecommendationResult(result.toString());

        // 추천 결과를 Recommendation 테이블에 저장
        saveHRMRecommendationResult(memberId, recommendedMovies);

        return CompletableFuture.completedFuture(recommendedMovies);
    }

    // 비동기적으로 Python AI 모델을 호출하여 추천 결과를 가져오는 메서드
    @Async
    public CompletableFuture<List<String>> callLLMModel(Map<String, Object> inputData, Long memberId) throws IOException {
        String userPrompt = (String) inputData.get("user_prompt");
        if (userPrompt == null || userPrompt.isEmpty()) {
            throw new IllegalArgumentException("user_prompt는 필수입니다.");
        }

        ProcessBuilder processBuilder = new ProcessBuilder("python3", "recomsystem/llm.py", userPrompt);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python 스크립트 실행 실패, 종료 코드: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Python 스크립트 실행 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Python 스크립트 실행 중 오류 발생", e);
        }

        List<String> recommendedMovies = parseLLMRecommendationResult(result.toString());

        saveLLMRecommendationResult(memberId, recommendedMovies);

        return CompletableFuture.completedFuture(recommendedMovies);
    }

    // HRM 모델의 추천 결과를 파싱하는 메서드
    private List<String> parseHRMRecommendationResult(String result) {
        return List.of(result.split(","));
    }

    // HRM 추천 결과를 저장하는 메서드
    private void saveHRMRecommendationResult(Long memberId, List<String> recommendedMovies) {
        // HRM 추천 결과를 저장하는 로직 (HRMRecommendation 엔티티)
        hrmRecommendationService.saveRecommendationForMember(memberId, recommendedMovies);
    }

    // LLM 모델의 추천 결과를 파싱하는 메서드
    private List<String> parseLLMRecommendationResult(String result) {
        return List.of(result.split(","));
    }

    // LLM 추천 결과를 저장하는 메서드
    private void saveLLMRecommendationResult(Long memberId, List<String> recommendedMovies) {
        // LLM 추천 결과를 저장하는 로직 (LlmRecommendation 엔티티)
        llmRecommendationService.saveRecommendationForMember(memberId, recommendedMovies);
    }
}
