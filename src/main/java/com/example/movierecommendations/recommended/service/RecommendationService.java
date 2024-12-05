package com.example.movierecommendations.recommended.service;


import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.MemberRepository;
import com.example.movierecommendations.recommended.domain.Recommendation;
import com.example.movierecommendations.recommended.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final MemberRepository memberRepository;

    // 추천된 영화 리스트 저장
    public void saveRecommendationResult(Long memberId, List<String> recommendedMovies) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 추천 리스트를 Recommendation 테이블에 저장
        Recommendation recommendation = Recommendation.builder()
                .member(member)
                .recommendedMovies(recommendedMovies)
                .build();

        recommendationRepository.save(recommendation);
    }


    // 추천 영화 리스트 수정 (전체 교체)
    public void modifyRecommendationList(Long recommendationId, List<String> updatedRecommendations) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        // 기존 추천 리스트를 모두 제거하고 새 리스트로 교체
        recommendation.getRecommendedMovies().clear();
        recommendation.getRecommendedMovies().addAll(updatedRecommendations);
        recommendationRepository.save(recommendation);
    }

    // 추천 영화 리스트에 영화 추가
    public void addMovieToRecommendation(Long recommendationId, String movieInfo) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        // 영화 추천 리스트에 영화 추가
        recommendation.addMovieRecommendation(movieInfo);
        recommendationRepository.save(recommendation);
    }

    // 추천 영화 리스트에서 영화 삭제
    public void removeMovieFromRecommendation(Long recommendationId, String movieInfo) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        // 영화 추천 리스트에서 특정 영화 삭제
        recommendation.removeMovieRecommendation(movieInfo);
        recommendationRepository.save(recommendation);
    }
}
