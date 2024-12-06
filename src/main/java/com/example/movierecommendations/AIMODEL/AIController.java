package com.example.movierecommendations.AIMODEL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")  // 엔드포인트 URL
public class AIController {

    private final FastAPIClient fastAPIClient;

    // FastAPIClient 주입
    @Autowired
    public AIController(FastAPIClient fastAPIClient) {
        this.fastAPIClient = fastAPIClient;
    }

    // 기존 HRM 모델을 호출하는 메서드
    @PostMapping("/predict/{memberId}")
    public CompletableFuture<ResponseEntity<List<String>>> HRMpredict(@PathVariable Long memberId, @RequestBody Map<String, Object> inputData) {
        // FastAPI의 /predict 엔드포인트로 요청을 보냄
        return fastAPIClient.callFastAPIModel("/api/ai/predict", inputData)
                .thenApply(ResponseEntity::ok)  // 응답을 OK로 감싸서 반환
                .exceptionally(ex -> {
                    return ResponseEntity.status(500).body(List.of("Error occurred while calling the AI model: " + ex.getMessage()));
                });
    }

    // LLM 모델을 호출하는 메서드
    @PostMapping("/predict2/{memberId}")
    public CompletableFuture<ResponseEntity<List<String>>> LLMpredict(@PathVariable Long memberId, @RequestBody Map<String, Object> inputData) {
        // FastAPI의 /predict2 엔드포인트로 요청을 보냄
        return fastAPIClient.callFastAPIModel("/api/ai/predict/llm", inputData)
                .thenApply(ResponseEntity::ok)  // 응답을 OK로 감싸서 반환
                .exceptionally(ex -> {
                    return ResponseEntity.status(500).body(List.of("Error occurred while calling the LLM model: " + ex.getMessage()));
                });
    }
}
