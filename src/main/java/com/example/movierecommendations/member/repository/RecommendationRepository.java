package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    // 기존: 회원 ID로 추천 결과 조회
    Optional<Recommendation> findByMemberId(Long memberId);

}
