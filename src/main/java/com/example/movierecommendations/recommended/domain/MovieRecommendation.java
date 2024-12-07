package com.example.movierecommendations.recommended.domain;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "movie_recommendation")
public class MovieRecommendation {

    @Id
    private Long movieCd;  // 영화 코드 (Primary Key로 사용)

    private String movieNm;  // 영화 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hrm_recommendation_id")  // HrmRecommendation과 연결
    private HrmRecommendation hrmRecommendation;

    @Override
    public String toString() {
        return "MovieRecommendation{" +
                "movieCd=" + movieCd +
                ", movieNm='" + movieNm + '\'' +
                '}';
    }
}
