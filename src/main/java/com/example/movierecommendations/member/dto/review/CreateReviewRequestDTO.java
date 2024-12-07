package com.example.movierecommendations.member.dto.review;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReviewRequestDTO {
    private int movieId;
    private String ranked;

    public Review toReview(Member member) {
        return Review.builder()
                .member(member)
                .movieId(movieId)
                .ranked(ranked)
//                .createdAt(LocalDateTime.now())
                .build();
    }
}
