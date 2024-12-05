package com.example.movierecommendations.recommended.controller;

import com.example.movierecommendations.recommended.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendationController {

    private final RecommendationService recommendationService;

    // 추천된 영화 리스트 저장
    @PostMapping("/{memberId}")
    public ResponseEntity<String> saveRecommendationList(
            @PathVariable Long memberId,
            @RequestBody List<String> movieRecommendations) {

        recommendationService.saveRecommendationResult(memberId, movieRecommendations);
        return ResponseEntity.ok("추천 영화 리스트가 성공적으로 저장되었습니다.");
    }

    // 추천 영화 리스트 수정 (전체 교체)
    @PutMapping("/{recommendationId}")
    public ResponseEntity<String> modifyRecommendationList(
            @PathVariable Long recommendationId,
            @RequestBody List<String> updatedRecommendations) {

        recommendationService.modifyRecommendationList(recommendationId, updatedRecommendations);
        return ResponseEntity.ok("추천 영화 리스트가 성공적으로 수정되었습니다.");
    }

    // 추천 영화 리스트에 영화 추가
    @PostMapping("/{recommendationId}/add")
    public ResponseEntity<String> addMovieToRecommendation(
            @PathVariable Long recommendationId,
            @RequestBody String movieInfo) {

        recommendationService.addMovieToRecommendation(recommendationId, movieInfo);
        return ResponseEntity.ok("추천 영화가 성공적으로 추가되었습니다.");
    }

    // 추천 영화 리스트에서 영화 삭제
    @DeleteMapping("/{recommendationId}/remove")
    public ResponseEntity<String> removeMovieFromRecommendation(
            @PathVariable Long recommendationId,
            @RequestBody String movieInfo) {

        recommendationService.removeMovieFromRecommendation(recommendationId, movieInfo);
        return ResponseEntity.ok("추천 영화가 성공적으로 삭제되었습니다.");
    }
}
