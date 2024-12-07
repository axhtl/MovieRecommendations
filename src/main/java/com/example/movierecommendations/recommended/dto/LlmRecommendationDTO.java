package com.example.movierecommendations.recommended.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LlmRecommendationDTO {

    private Long memberId;  // 회원 ID
    private List<String> llmRecommendations;  // 추천 영화 리스트
}
