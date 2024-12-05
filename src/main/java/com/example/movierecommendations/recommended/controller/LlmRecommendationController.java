package com.example.movierecommendations.recommended.controller;

import com.example.movierecommendations.recommended.dto.LlmRecommendationDTO;
import com.example.movierecommendations.recommended.service.LlmRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations/llm")
public class LlmRecommendationController {

    private final LlmRecommendationService llmRecommendationService;

    @Autowired
    public LlmRecommendationController(LlmRecommendationService llmRecommendationService) {
        this.llmRecommendationService = llmRecommendationService;
    }

    // LLM 추천 리스트 저장 엔드포인트
    @PostMapping("/save")
    public ResponseEntity<String> saveRecommendation(@RequestBody LlmRecommendationDTO llmRecommendationDTO) {
        try {
            // 서비스 계층에서 추천 리스트 저장 처리
            llmRecommendationService.saveRecommendation(llmRecommendationDTO);
            return ResponseEntity.ok("LLM Recommendation saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred while saving LLM Recommendation.");
        }
    }
}
