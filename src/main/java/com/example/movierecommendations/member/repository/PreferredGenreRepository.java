package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.PreferredGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferredGenreRepository extends JpaRepository<PreferredGenre, Long> {
}
