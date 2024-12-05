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

    // 추천 예측 API
    @PostMapping("/predict/{memberId}")
    public CompletableFuture<ResponseEntity<List<String>>> predict(@PathVariable Long memberId, @RequestBody Map<String, Object> inputData) throws IOException {
        // AI 모델 호출하여 추천 결과를 받아옴
        return aiModelService.callPythonAIModel(inputData, memberId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).body(List.of("Error occurred while calling the AI model: " + ex.getMessage())));
    }
}
