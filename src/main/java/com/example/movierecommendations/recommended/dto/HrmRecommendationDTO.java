package com.example.movierecommendations.recommended.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HrmRecommendationDTO {

    private Long memberId;  // 추천 리스트의 회원 ID
    private List<String> hybridRecommendations;  // 하이브리드 추천 영화 목록
    private List<String> similarMovies;  // 유사 영화 목록
}
