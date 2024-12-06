package com.example.movierecommendations.recommended.controller;

import com.example.movierecommendations.recommended.domain.HrmRecommendation;
import com.example.movierecommendations.recommended.dto.HrmRecommendationDTO;
import com.example.movierecommendations.recommended.service.HrmRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class HrmRecommendationController {

    private final HrmRecommendationService hrmRecommendationService;

    @Autowired
    public HrmRecommendationController(HrmRecommendationService hrmRecommendationService) {
        this.hrmRecommendationService = hrmRecommendationService;
    }

    // 특정 회원의 추천 리스트를 조회하는 엔드포인트
    @GetMapping(value = "/recommend/{memberId}", produces = "application/json")
    public ResponseEntity<HrmRecommendationDTO> getRecommendationByMemberId(@PathVariable Long memberId) {
        try {
            // 서비스 계층에서 추천 리스트를 조회
            HrmRecommendation hrmRecommendation = hrmRecommendationService.getRecommendationByMemberId(memberId);

            // 조회한 추천 데이터를 DTO로 변환
            HrmRecommendationDTO hrmRecommendationDTO = new HrmRecommendationDTO(
                    hrmRecommendation.getMemberId(),
                    hrmRecommendation.getHybridRecommendations(),
                    hrmRecommendation.getSimilarMovies()
            );

            return ResponseEntity.ok(hrmRecommendationDTO);
        } catch (RuntimeException e) {
            // 예외 처리: 추천 리스트가 없을 경우
            return ResponseEntity.status(404).body(null);
        }
    }

    // 회원의 추천 리스트를 저장하는 엔드포인트
    @PostMapping("/save")
    public ResponseEntity<String> saveRecommendation(@RequestBody HrmRecommendationDTO hrmRecommendationDTO) {
        // HrmRecommendationDTO를 서비스 계층에 전달하여 저장 처리
        hrmRecommendationService.saveRecommendation(hrmRecommendationDTO);

        return ResponseEntity.ok("Recommendation saved successfully.");
    }
}
