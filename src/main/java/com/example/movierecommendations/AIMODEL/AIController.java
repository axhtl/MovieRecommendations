package com.example.movierecommendations.AIMODEL;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIModelService aiModelService;

    @Autowired
    public AIController(AIModelService aiModelService) {
        this.aiModelService = aiModelService;
    }

    // 설문조사 결과를 AI 모델에 보내어 추천 결과를 받음 (기존 HRM 모델)
    @PostMapping("/predict/{memberId}")
    public CompletableFuture<ResponseEntity<List<String>>> predict(@PathVariable Long memberId, @RequestBody Map<String, Object> inputData) throws IOException {
        return aiModelService.callHRMModel(inputData, memberId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).body(List.of("Error occurred while calling the AI model: " + ex.getMessage())));
    }

    // 새로운 LLM 모델을 호출하여 추천 결과를 받음 (새로운 메서드 predict2)
    @PostMapping("/predict2/{memberId}")
    public CompletableFuture<ResponseEntity<List<String>>> predict2(@PathVariable Long memberId, @RequestBody Map<String, Object> inputData) throws IOException {
        return aiModelService.callLLMModel(inputData, memberId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).body(List.of("Error occurred while calling the LLM model: " + ex.getMessage())));
    }
}
