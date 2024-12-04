package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.MovieDirector;
import com.example.movierecommendations.member.domain.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieDirectorRepository extends JpaRepository<MovieDirector, Long> {
    List<MovieDirector> findByMovieInfo(MovieInfo movieInfo);
}
