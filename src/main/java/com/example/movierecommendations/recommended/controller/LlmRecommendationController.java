package com.example.movierecommendations.recommended.controller;


import com.example.movierecommendations.recommended.domain.LlmRecommendation;
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


    // 특정 회원의 추천 리스트를 조회하는 엔드포인트
    @GetMapping(value = "/recommend/{memberId}", produces = "application/json")
    public ResponseEntity<LlmRecommendationDTO> getRecommendationByMemberId(@PathVariable Long memberId) {
        try {
            // 서비스 계층에서 추천 리스트를 조회
            LlmRecommendation llmRecommendation = llmRecommendationService.getRecommendationByMemberId(memberId);

            // 조회한 추천 데이터를 DTO로 변환
            LlmRecommendationDTO llmRecommendationDTO = new LlmRecommendationDTO(
                    llmRecommendation.getMemberId(),
                    llmRecommendation.getLlmRecommendations()
            );

            return ResponseEntity.ok(llmRecommendationDTO);
        } catch (RuntimeException e) {
            // 예외 처리: 추천 리스트가 없을 경우
            return ResponseEntity.status(404).body(null);
        }
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
