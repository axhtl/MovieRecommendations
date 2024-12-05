package com.example.movierecommendations.recommended.repository;

import com.example.movierecommendations.recommended.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    // 회원 ID로 추천 리스트 조회
    Recommendation findByMemberMemberId(Long memberId);
}
