package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.MovieGenre;
import com.example.movierecommendations.member.domain.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
    List<MovieGenre> findByMovieInfo(MovieInfo movieInfo);
}
