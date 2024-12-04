package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.MovieDirector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieDirectorRepository extends JpaRepository<MovieDirector, Long> {
}
