package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.PreferredActor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferredActorRepository extends JpaRepository<PreferredActor, Long> {
}
