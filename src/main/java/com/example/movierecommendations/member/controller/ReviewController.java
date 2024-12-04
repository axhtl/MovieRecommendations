package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.dto.review.CreateReviewRequestDTO;
import com.example.movierecommendations.member.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 시청한 영화 후기, 정보 저장
    @PostMapping("/review/{memberId}")
    public ResponseEntity<SaveResponseDTO> saveReview(@PathVariable Long memberId, @RequestBody CreateReviewRequestDTO request) {
        Long reviewId = reviewService.saveReview(memberId, request);
        return ResponseEntity.ok(new SaveResponseDTO(
                reviewId, HttpStatus.OK.value(), "시청한 영화 기록이 정상적으로 등록되었습니다."
        ));
    }
}
