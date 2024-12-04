package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.MovieActor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieActorRepository extends JpaRepository<MovieActor, Long> {
}
