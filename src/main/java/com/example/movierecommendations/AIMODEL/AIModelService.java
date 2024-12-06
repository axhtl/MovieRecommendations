package com.example.movierecommendations.AIMODEL;


import com.example.movierecommendations.recommended.domain.HrmRecommendation;
import com.example.movierecommendations.recommended.domain.LlmRecommendation;
import com.example.movierecommendations.recommended.repository.HrmRecommendationRepository;
import com.example.movierecommendations.recommended.repository.LlmRecommendationRepository;
import com.example.movierecommendations.recommended.service.HrmRecommendationService;
import com.example.movierecommendations.recommended.service.LlmRecommendationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AIModelService {

    private static final Logger logger = LoggerFactory.getLogger(AIModelService.class);

    private final WebClient webClient;
    private final LlmRecommendationService llmRecommendationService;
    private final HrmRecommendationService hrmRecommendationService;
    private final HrmRecommendationRepository hrmRecommendationRepository;
    private final LlmRecommendationRepository llmRecommendationRepository;



    // 생성자에서 WebClient.Builder를 주입받아 webClient를 초기화
    public AIModelService(WebClient.Builder webClientBuilder, LlmRecommendationService llmRecommendationService,
                          HrmRecommendationService hrmRecommendationService,
                          HrmRecommendationRepository hrmRecommendationRepository,
                          LlmRecommendationRepository llmRecommendationRepository) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8080").build(); // WebClient 생성
        this.llmRecommendationService = llmRecommendationService;
        this.hrmRecommendationService = hrmRecommendationService;
        this.hrmRecommendationRepository = hrmRecommendationRepository;
        this.llmRecommendationRepository = llmRecommendationRepository;
    }

    // FastAPI에서 추천 결과를 받아오는 메서드
    @Async
    public void callHRMModel(Map<String, Object> inputData, Long memberId) {
        logger.info("Calling FastAPI HRM model with input data: {}", inputData);

        webClient.post()
                .uri("/api/ai/predict")
                .bodyValue(inputData)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                }) // 타입을 명확히 지정
                .toFuture()
                .thenApply(result -> {
                    logger.info("Received HRM recommendations: {}", result);
                    // HRM 추천 결과를 Recommendation 테이블에 저장
                    saveHRMRecommendationResult(memberId, result);
                    return result;
                })
                .exceptionally(ex -> {
                    if (ex instanceof WebClientResponseException) {
                        WebClientResponseException webClientEx = (WebClientResponseException) ex;
                        logger.error("WebClient Error: {} with response: {}", ex.getMessage(), webClientEx.getResponseBodyAsString());
                    } else {
                        logger.error("Error calling FastAPI HRM model: {}", ex.getMessage());
                    }
                    throw new RuntimeException("Error calling FastAPI HRM model", ex);
                });
    }



    // FastAPI에서 LLM 추천 결과를 받아오는 메서드
    @Async
    public void callLLMModel(Map<String, Object> inputData, Long memberId) {
        logger.info("Calling FastAPI LLM model with input data: {}", inputData);

        webClient.post()
                .uri("/api/ai/predict/llm")
                .bodyValue(inputData)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {}) // 타입을 명확히 지정
                .doOnTerminate(() -> logger.info("Finished LLM recommendation call"))
                .doOnError(ex -> logger.error("Error calling FastAPI LLM model: {}", ex.getMessage()))
                .subscribe(result -> {
                    logger.info("Received LLM recommendations: {}", result);
                    // LLM 추천 결과를 Recommendation 테이블에 저장
                    saveLLMRecommendationResult(memberId, result);
                });
    }




    // HRM 추천 결과를 저장하는 메서드
    private void saveHRMRecommendationResult(Long memberId, List<String> recommendedMovies) {
        hrmRecommendationService.saveRecommendationForMember(memberId, recommendedMovies);
    }

    // LLM 추천 결과를 저장하는 메서드
    private void saveLLMRecommendationResult(Long memberId, List<String> recommendedMovies) {
        llmRecommendationService.saveRecommendationForMember(memberId, recommendedMovies);
    }

    // memberId를 기준으로 HRM 추천 결과를 반환하는 메서드
    public Optional<HrmRecommendation> getHRMRecommendationByMemberId(Long memberId) {
        return hrmRecommendationRepository.findByMemberMemberId(memberId);
    }

    // memberId를 기준으로 LLM 추천 결과를 반환하는 메서드
    public Optional<LlmRecommendation> getLLMRecommendationByMemberId(Long memberId) {
        return llmRecommendationRepository.findByMember_MemberId(memberId);
    }
}

