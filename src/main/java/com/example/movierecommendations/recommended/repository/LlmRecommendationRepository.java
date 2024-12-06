package com.example.movierecommendations.recommended.repository;

import com.example.movierecommendations.recommended.domain.LlmRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LlmRecommendationRepository extends JpaRepository<LlmRecommendation, Long> {
    // memberId를 기반으로 LlmRecommendation을 찾는 메서드
    Optional<LlmRecommendation> findByMember_MemberId(Long memberId);
}
