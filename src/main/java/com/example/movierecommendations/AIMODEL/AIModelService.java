package com.example.movierecommendations.AIMODEL;


import com.example.movierecommendations.member.dto.UserMovieInfoResponse;
import com.example.movierecommendations.recommended.domain.HrmRecommendation;
import com.example.movierecommendations.recommended.domain.LlmRecommendation;
import com.example.movierecommendations.recommended.repository.HrmRecommendationRepository;
import com.example.movierecommendations.recommended.repository.LlmRecommendationRepository;
import com.example.movierecommendations.recommended.service.HrmRecommendationService;
import com.example.movierecommendations.recommended.service.LlmRecommendationService;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIModelService {

    private static final Logger logger = LoggerFactory.getLogger(AIModelService.class);

    private final WebClient webClient;
    private final LlmRecommendationService llmRecommendationService;
    private final HrmRecommendationService hrmRecommendationService;
    private final HrmRecommendationRepository hrmRecommendationRepository;
    private final LlmRecommendationRepository llmRecommendationRepository;



    // 생성자에서 WebClient.Builder를 주입받아 webClient를 초기화
    @Autowired
    public AIModelService(WebClient.Builder webClientBuilder, LlmRecommendationService llmRecommendationService,
                          HrmRecommendationService hrmRecommendationService,
                          HrmRecommendationRepository hrmRecommendationRepository,
                          LlmRecommendationRepository llmRecommendationRepository) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8086").build(); // WebClient 생성
        this.llmRecommendationService = llmRecommendationService;
        this.hrmRecommendationService = hrmRecommendationService;
        this.hrmRecommendationRepository = hrmRecommendationRepository;
        this.llmRecommendationRepository = llmRecommendationRepository;
    }




    // FastAPI에서 추천 결과를 받아오는 메서드
//    @Async
//    public CompletableFuture<List<String>> callHRMModel(String inputData, Long memberId) {
//        logger.info("Calling FastAPI HRM model with input data: {}", inputData);
//
//        return webClient.post()
//                .uri("/recom-hybrid")  // FastAPI에서 받는 경로
//                .bodyValue(inputData)   // 요청 본문에 inputData를 담아 보냄
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
//                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
//                }) // FastAPI로부터 응답받을 타입을 지정 (List<String>)
//                .toFuture()  // 비동기적으로 처리할 수 있게 Future로 변환
//                .thenApply(result -> {
//                    logger.info("Received HRM recommendations: {}", result);
//                    // HRM 추천 결과를 Recommendation 테이블에 저장
//                    saveHRMRecommendationResult(memberId, result);
//                    return result;  // HRM 추천 결과 반환
//                })
//                .exceptionally(ex -> {
//                    if (ex instanceof WebClientResponseException) {
//                        WebClientResponseException webClientEx = (WebClientResponseException) ex;
//                        logger.error("WebClient Error: {} with response: {}", ex.getMessage(), webClientEx.getResponseBodyAsString());
//                    } else {
//                        logger.error("Error calling FastAPI HRM model: {}", ex.getMessage());
//                    }
//                    // 오류 발생 시 기본 에러 메시지를 반환
//                    return List.of("Error occurred while calling HRM model");
//                });
//    }
//

//    @Async
//    public CompletableFuture<List<String>> callHRMModel(String inputData, Long memberId) {
//        logger.info("Calling FastAPI HRM model with input data: {}", inputData);
//
//        return webClient.post()
//                .uri("/recom-hybrid")  // FastAPI에서 받는 경로
//                .bodyValue(inputData)   // 요청 본문에 inputData를 담아 보냄
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
//                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
//                }) // FastAPI로부터 응답받을 타입을 지정 (List<String>)
//                .toFuture()  // 비동기적으로 처리할 수 있게 Future로 변환
//                .thenApply(result -> {
//                    logger.info("Received HRM recommendations: {}", result);
//
//                    // HRM 추천 결과를 MovieRecommendationDTO로 변환
//                    List<MovieRecommendationDTO> movieRecommendationDTOs = result.stream()
//                            .map(MovieRecommendationDTO::new)  // MovieRecommendationDTO 생성
//                            .collect(Collectors.toList());
//
//                    // HRM 추천 결과를 Recommendation 테이블에 저장
//                    saveHRMRecommendationResult(memberId, movieRecommendationDTOs);
//                    return result;  // HRM 추천 결과 반환
//                })
//                .exceptionally(ex -> {
//                    if (ex instanceof WebClientResponseException) {
//                        WebClientResponseException webClientEx = (WebClientResponseException) ex;
//                        logger.error("WebClient Error: {} with response: {}", ex.getMessage(), webClientEx.getResponseBodyAsString());
//                    } else {
//                        logger.error("Error calling FastAPI HRM model: {}", ex.getMessage());
//                    }
//                    // 오류 발생 시 기본 에러 메시지를 반환
//                    return List.of("Error occurred while calling HRM model");
//                });
//    }

//    public List<String> callHRMModel(String inputData, Long memberId) {
//        logger.info("Calling FastAPI HRM model with input data: {}", inputData);
//
//        // 동기 방식으로 FastAPI 호출
//        List<String> result = webClient.post()
//                .uri("/recom-hybrid")  // FastAPI에서 받는 경로
//                .bodyValue(inputData)   // 요청 본문에 inputData를 담아 보냄
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
//                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
//                }) // FastAPI로부터 응답받을 타입을 지정 (List<String>)
//                .block();  // 동기적으로 처리하도록 `block()` 호출
//
//        if (result != null) {
//            logger.info("Received HRM recommendations: {}", result);
//
//            // HRM 추천 결과를 MovieRecommendationDTO로 변환
////            List<MovieRecommendationDTO> movieRecommendationDTOs = result.stream()
////                    .map(MovieRecommendationDTO::new)  // MovieRecommendationDTO 생성
////                    .collect(Collectors.toList());
//
//            // HRM 추천 결과를 Recommendation 테이블에 저장
//            //saveHRMRecommendationResult(memberId, movieRecommendationDTOs);
//        } else {
//            logger.error("Failed to receive a valid response from the HRM model.");
//        }
//
//        return result != null ? result : List.of("Error occurred while calling HRM model");
//    }

//    public List<String> callHRMModel(String inputData, Long memberId) {
//        logger.info("Calling FastAPI HRM model with input data: {}", inputData);
//
//        // 동기 방식으로 FastAPI 호출
//        List<String> result = webClient.post()
//                .uri("/test")  // FastAPI에서 받는 경로
//                .bodyValue(inputData)   // 요청 본문에 inputData를 담아 보냄
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
//                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
//                }) // FastAPI로부터 응답받을 타입을 지정 (List<String>)
////                .timeout(Duration.ofSeconds(10));
//                .block();  // 동기적으로 처리하도록 `block()` 호출
//
//        if (result != null) {
//            logger.info("Received HRM recommendations: {}", result);
//        } else {
//            logger.error("Failed to receive a valid response from the HRM model.");
//        }
//
//        return result != null ? result : List.of("Error occurred while calling HRM model");
//    }


//    public List<String> callHRMModel(String inputData, Long memberId) {
//        logger.info("Calling FastAPI HRM model with input data: {}", inputData);
//
//        // WebClient에 30초 타임아웃 설정
//        WebClient webClientWithTimeout = webClient.mutate()
//                .clientConnector(new ReactorClientHttpConnector(
//                        HttpClient.create()
//                                .responseTimeout(Duration.ofSeconds(30))  // 응답 타임아웃을 30초로 설정
//                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)  // 연결 타임아웃도 30초로 설정
//                ))
//                .build();
//
//        // 동기 방식으로 FastAPI 호출
//        List<String> result = webClientWithTimeout.post()
//                .uri("/recom-hybrid")  // FastAPI에서 받는 경로
//                .contentType(MediaType.APPLICATION_JSON)  // Content-Type을 application/json으로 설정
//                .bodyValue(inputData)   // 요청 본문에 JSON 형식의 inputData를 담아 보냄
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
//                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
//                }) // FastAPI로부터 응답받을 타입을 지정 (List<String>)
//                .block();  // 동기적으로 처리하도록 block() 호출
//
//        if (result != null) {
//            logger.info("Received HRM recommendations: {}", result);
//        } else {
//            logger.error("Failed to receive a valid response from the HRM model.");
//        }
//
//        return result != null ? result : List.of("Error occurred while calling HRM model");
//    }

//    public List<String> callHRMModel(String inputData, Long memberId) {
//        logger.info("Calling FastAPI HRM model with input data: {}", inputData);
//
//        // WebClient에 30초 타임아웃 설정
//        WebClient webClientWithTimeout = webClient.mutate()
//                .clientConnector(new ReactorClientHttpConnector(
//                        HttpClient.create()
//                                .responseTimeout(Duration.ofSeconds(300))  // 응답 타임아웃을 30초로 설정
//                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300000)  // 연결 타임아웃도 30초로 설정
//                ))
//                .build();
//
//        // 동기 방식으로 FastAPI 호출
//        List<String> result = webClientWithTimeout.post()
//                .uri("/recom-hybrid")  // FastAPI에서 받는 경로
//                .contentType(MediaType.APPLICATION_JSON)  // Content-Type을 application/json으로 설정
//                .bodyValue(inputData)   // 요청 본문에 JSON 형식의 inputData를 담아 보냄
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
//                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
//                }) // FastAPI로부터 응답받을 타입을 지정 (List<String>)
//                .block();  // 동기적으로 처리하도록 block() 호출
//
//        if (result != null) {
//            logger.info("Received HRM recommendations: {}", result);
//        } else {
//            logger.error("Failed to receive a valid response from the HRM model.");
//        }
//
//        return result != null ? result : List.of("Error occurred while calling HRM model");
//    }

    public List<Map<String, Object>> callHRMModel(String inputData, Long memberId) {
        logger.info("Calling FastAPI HRM model with input data: {}", inputData);

        // WebClient에 30초 타임아웃 설정
        WebClient webClientWithTimeout = webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(300))  // 응답 타임아웃을 30초로 설정
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300000)  // 연결 타임아웃도 30초로 설정
                ))
                .build();

        // 동기 방식으로 FastAPI 호출
        List<Map<String, Object>> result = webClientWithTimeout.post()
                .uri("/recom-hybrid")  // FastAPI에서 받는 경로
                .contentType(MediaType.APPLICATION_JSON)  // Content-Type을 application/json으로 설정
                .bodyValue(inputData)   // 요청 본문에 JSON 형식의 inputData를 담아 보냄
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                }) // FastAPI로부터 응답받을 타입을 지정 (List<Map<String, Object>>)
                .block();  // 동기적으로 처리하도록 block() 호출

        if (result != null && !result.isEmpty()) {
            logger.info("Received HRM recommendations: {}", result);
        } else {
            logger.error("Failed to receive a valid response from the HRM model.");
        }

        return result != null ? result : List.of(Map.of("error", "Error occurred while calling HRM model"));
    }


//
//    // FastAPI에서 LLM 추천 결과를 받아오는 메서드
//    @Async
//    public void callLLMModel(UserMovieInfoResponse inputData, Long memberId) {
//        logger.info("Calling FastAPI LLM model with input data: {}", inputData);
//
//        webClient.post()
//                .uri("/api/ai/predict/llm")
//                .bodyValue(inputData)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        clientResponse -> Mono.error(new RuntimeException("API error: " + clientResponse.statusCode())))
//                .bodyToMono(new ParameterizedTypeReference<List<String>>() {}) // 타입을 명확히 지정
//                .doOnTerminate(() -> logger.info("Finished LLM recommendation call"))
//                .doOnError(ex -> logger.error("Error calling FastAPI LLM model: {}", ex.getMessage()))
//                .subscribe(result -> {
//                    logger.info("Received LLM recommendations: {}", result);
//                    // LLM 추천 결과를 Recommendation 테이블에 저장
//                    saveLLMRecommendationResult(memberId, result);
//                });
//    }




//    // HRM 추천 결과를 저장하는 메서드
//    private void saveHRMRecommendationResult(Long memberId, List<MovieRecommendationDTO> recommendedMovies) {
//        hrmRecommendationService.saveRecommendationForMember(memberId, recommendedMovies);
//    }

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