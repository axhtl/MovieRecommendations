package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findByMemberId(Long memberId);
    boolean existsByMemberId(Long memberId);
}
