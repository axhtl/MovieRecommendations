package com.example.movierecommendations.recommended.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieRecommendationDTO {

    private String title;  // 영화 제목
    private String recommendationReason;  // 추천 이유

    public MovieRecommendationDTO(String title, String recommendationReason) {
        this.title = title;
        this.recommendationReason = recommendationReason;
    }
}
