package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
}
