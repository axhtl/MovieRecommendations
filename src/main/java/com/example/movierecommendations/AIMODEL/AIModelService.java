package com.example.movierecommendations.AIMODEL;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIModelService {

    private static final Logger logger = LoggerFactory.getLogger(AIModelService.class);
    private final WebClient webClient;

    // 생성자에서 WebClient.Builder를 주입받아 webClient를 초기화
    @Autowired
    public AIModelService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8080").build(); // WebClient 생성
    }


    // FastAPI에서 추천 결과를 받아오는 메서드
    public List<Map<String, Object>> callHRMModel(String inputData, Long memberId) {
        logger.info("Calling FastAPI HRM model with input data: {}", inputData);

        // WebClient에 30초 타임아웃 설정
        WebClient webClientWithTimeout = webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(30))  // 응답 타임아웃을 30초로 설정
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)  // 연결 타임아웃도 30초로 설정
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


    // FastAPI에서 추천 결과를 받아오는 메서드
    public Map<String, Object> callLLMModel(String inputData) {
        logger.info("Calling FastAPI LLM model with input data: {}", inputData);

        // WebClient에 30초 타임아웃 설정
        WebClient webClientWithTimeout = webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(30))  // 응답 타임아웃을 30초로 설정
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)  // 연결 타임아웃도 30초로 설정
                ))
                .build();

        // 요청 본문을 JSON 형태로 변경
        String requestBody = String.format("{\"text\": \"%s\"}", inputData);
        logger.info("Prepared RequestBody to send to ai-server: {}", requestBody);

        // 동기 방식으로 FastAPI 호출
        Map<String, Object> result = null;

        try {
            // FastAPI 서버에 요청 보내기
            logger.debug("Sending POST request to FastAPI server at /llm");
            result = webClientWithTimeout.post()
                    .uri("/llm")  // FastAPI에서 받는 경로
                    .contentType(MediaType.APPLICATION_JSON)  // Content-Type을 application/json으로 설정
                    .bodyValue(requestBody)  // 요청 본문에 JSON 형태로 데이터를 담아 보냄
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                // 상태 코드가 4xx 또는 5xx일 경우 예외 발생
                                logger.error("API error: Status Code = {}", clientResponse.statusCode());
                                return Mono.error(new RuntimeException("API error: " + clientResponse.statusCode()));
                            })
                    .bodyToMono(Map.class)  // 응답을 Map으로 받도록 변경
                    .doOnTerminate(() -> logger.info("FastAPI request completed")) // 요청 종료 시 로그 출력
                    .doOnError(ex -> logger.error("Error occurred while calling FastAPI: {}", ex.getMessage(), ex))  // 에러 처리
                    .block();  // 동기적으로 처리하도록 block() 호출

            // 응답 결과가 비어 있지 않으면, 결과 출력
            if (result != null && !result.isEmpty()) {
                logger.info("Received LLM recommendations: {}", result);
            } else {
                logger.warn("Received empty or invalid response from LLM model.");
            }

        } catch (Exception ex) {
            // 예외 처리 및 에러 로그 출력
            logger.error("Error occurred while calling FastAPI LLM model: {}", ex.getMessage(), ex);
            result = Map.of("error", "Error occurred while calling LLM model");
        }

        return result != null ? result : Map.of("error", "Error occurred while calling LLM model");
    }



    // FastAPI에서 추천 결과를 받아오는 메서드
    public Map<String, Object> callLLMReasonModel(String inputData) {
        logger.info("Calling FastAPI LLM model with input data: {}", inputData);

        // WebClient에서 타임아웃 설정을 제거하고 무제한으로 설정
        WebClient webClientWithTimeout = webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(0))  // 응답 타임아웃을 무제한으로 설정
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 0)  // 연결 타임아웃을 무제한으로 설정
                ))
                .build();

        // 요청 본문을 JSON 형태로 변경
        String requestBody = String.format("{\"text\": \"%s\"}", inputData);
        logger.info("Prepared RequestBody to send to ai-server: {}", requestBody);

        // 동기 방식으로 FastAPI 호출
        Map<String, Object> result = null;

        try {
            // FastAPI 서버에 요청 보내기
            logger.debug("Sending POST request to FastAPI server at /llm-reason");
            result = webClientWithTimeout.post()
                    .uri("/llm-reason")  // FastAPI에서 받는 경로
                    .contentType(MediaType.APPLICATION_JSON)  // Content-Type을 application/json으로 설정
                    .bodyValue(requestBody)  // 요청 본문에 JSON 형태로 데이터를 담아 보냄
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                // 상태 코드가 4xx 또는 5xx일 경우 예외 발생
                                logger.error("API error: Status Code = {}", clientResponse.statusCode());
                                return Mono.error(new RuntimeException("API error: " + clientResponse.statusCode()));
                            })
                    .bodyToMono(Map.class)  // 응답을 Map으로 받도록 수정
                    .doOnTerminate(() -> logger.info("FastAPI call completed")) // 요청 종료 시 로그 출력
                    .doOnError(ex -> logger.error("Error occurred while calling FastAPI: {}", ex.getMessage(), ex))  // 에러 처리
                    .block();  // 동기적으로 처리하도록 block() 호출

            // 응답 결과가 비어 있지 않으면, 결과 출력
            if (result != null && !result.isEmpty()) {
                logger.info("Received LLM-Reason recommendations: {}", result);
            } else {
                logger.warn("Received empty or invalid response from LLM-Reason model.");
            }

        } catch (Exception ex) {
            // 예외 처리 및 에러 로그 출력
            logger.error("Error occurred while calling FastAPI LLM model: {}", ex.getMessage(), ex);
            result = Map.of("error", "Error occurred while calling LLM model");
        }

        return result != null ? result : Map.of("error", "Error occurred while calling LLM model");
    }




}
