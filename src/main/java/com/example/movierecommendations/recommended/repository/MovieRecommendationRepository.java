package com.example.movierecommendations.recommended.repository;

import com.example.movierecommendations.recommended.domain.MovieRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRecommendationRepository extends JpaRepository<MovieRecommendation, Long> {
    // 영화 코드로 추천 영화 찾기
    MovieRecommendation findByMovieCd(Long movieCd);
}
