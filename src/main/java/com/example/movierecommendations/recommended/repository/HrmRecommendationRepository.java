package com.example.movierecommendations.recommended.repository;

import com.example.movierecommendations.recommended.domain.HrmRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HrmRecommendationRepository extends JpaRepository<HrmRecommendation, Long> {

    // memberId를 기준으로 HrmRecommendation을 조회하는 메서드
    Optional<HrmRecommendation> findByMemberMemberId(Long memberId);
}
