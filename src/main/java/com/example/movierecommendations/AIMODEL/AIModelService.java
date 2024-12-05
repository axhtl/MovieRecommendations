package com.example.movierecommendations.AIMODEL;

import com.example.movierecommendations.recommended.service.RecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.MemberRepository;
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

    private final RecommendationService recommendationService;
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
        List<String> recommendedMovies = parseRecommendationResult(result.toString());

        // 추천 결과를 Recommendation 테이블에 저장
        saveRecommendationResult(memberId, recommendedMovies);

        return CompletableFuture.completedFuture(recommendedMovies);
    }

    // 비동기적으로 Python AI 모델을 호출하여 추천 결과를 가져오는 메서드
    @Async
    public CompletableFuture<List<String>> callLLMModel(Map<String, Object> inputData, Long memberId) throws IOException {
        // user_prompt를 inputData에서 추출
        String userPrompt = (String) inputData.get("user_prompt");
        if (userPrompt == null || userPrompt.isEmpty()) {
            throw new IllegalArgumentException("user_prompt는 필수입니다.");
        }

        // ProcessBuilder를 사용하여 Python 스크립트를 실행
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "recomsystem/llm.py", userPrompt);
        processBuilder.redirectErrorStream(true);

        // 프로세스를 시작하고, 결과를 읽기 위한 스트림 준비
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;

        // Python 스크립트에서 출력된 결과를 읽기
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

        // 결과를 추천 영화 리스트로 파싱
        List<String> recommendedMovies = parseRecommendationResult(result.toString());

        // 추천 결과를 Recommendation 테이블에 저장
        saveRecommendationResult(memberId, recommendedMovies);

        return CompletableFuture.completedFuture(recommendedMovies);
    }


    // Python AI 모델에서 반환된 결과를 List<String> 형식으로 변환
    private List<String> parseRecommendationResult(String result) {
        // 결과를 적절한 형식으로 파싱하여 추천 영화 리스트 반환
        // 이 부분은 실제 결과 형식에 맞게 파싱 작업을 해야 합니다.
        // 예시에서는 간단히 콤마로 구분된 문자열을 리스트로 변환하는 형태로 가정
        return List.of(result.split(","));
    }

    // 추천 결과를 Recommendation 테이블에 저장하는 메서드
    private void saveRecommendationResult(Long memberId, List<String> recommendedMovies) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 추천 리스트를 Recommendation 테이블에 저장
        recommendationService.saveRecommendationResult(memberId, recommendedMovies);
    }
}
