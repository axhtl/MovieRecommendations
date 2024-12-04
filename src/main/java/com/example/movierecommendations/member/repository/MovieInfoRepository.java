package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieInfoRepository  extends JpaRepository<MovieInfo, Long> {
    boolean existsByMovieId(int movieId);
}
