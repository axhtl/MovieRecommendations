package com.example.movierecommendations.recommended.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRecommendationDTO {

    private Long movieCd;   // 영화 코드
    private String movieNm; // 영화 이름

    // String을 받는 생성자 추가
    public MovieRecommendationDTO(String movieNm) {
        this.movieNm = movieNm;
    }
}
