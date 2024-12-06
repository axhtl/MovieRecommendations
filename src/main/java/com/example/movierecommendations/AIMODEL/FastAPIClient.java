package com.example.movierecommendations.AIMODEL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.ParameterizedTypeReference;

@Service
public class FastAPIClient {

    private final WebClient webClient;

    // 생성자에서 WebClient를 주입받습니다.
    @Autowired
    public FastAPIClient(WebClient.Builder webClientBuilder) {
        // FastAPI 서버 URL 설정
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080")  // FastAPI 서버 주소
                .build();
    }

    // FastAPI 모델 호출 메서드
    public CompletableFuture<List<String>> callFastAPIModel(String endpoint, Map<String, Object> requestBody) {
        return webClient.post()
                .uri(endpoint)  // FastAPI의 엔드포인트
                .contentType(MediaType.APPLICATION_JSON)  // JSON 형식으로 전송
                .bodyValue(requestBody)  // 요청 본문에 데이터 설정
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {}) // 반환 타입을 정확하게 지정
                .toFuture()
                .exceptionally(ex -> {
                    // 예외 처리: FastAPI 응답 오류 처리
                    if (ex instanceof WebClientResponseException) {
                        WebClientResponseException webClientEx = (WebClientResponseException) ex;
                        System.out.println("Error response: " + webClientEx.getResponseBodyAsString());
                    }
                    throw new RuntimeException("Error calling FastAPI model: " + ex.getMessage());
                });
    }
}
