package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.dto.review.CreateReviewRequestDTO;
import com.example.movierecommendations.member.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 시청 영화(리뷰) 등록, 정보 저장
    @PostMapping("/review/{memberId}")
    public ResponseEntity<SaveResponseDTO> saveReview(@PathVariable Long memberId, @RequestBody CreateReviewRequestDTO request) {
        Long reviewId = reviewService.saveReview(memberId, request);
        return ResponseEntity.ok(new SaveResponseDTO(
                reviewId, HttpStatus.OK.value(), "시청한 영화 기록이 정상적으로 등록되었습니다."
        ));
    }

    // 시청 영화(리뷰) 삭제 - 특정 리뷰 삭제
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok("후기가 성공적으로 삭제되었습니다.");
    }
}
