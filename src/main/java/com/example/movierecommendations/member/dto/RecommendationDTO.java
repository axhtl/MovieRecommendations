package com.example.movierecommendations.member.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private Long memberId;  // 회원 ID
    private List<String> movieRecommendations;  // 추천된 영화 목록
}
