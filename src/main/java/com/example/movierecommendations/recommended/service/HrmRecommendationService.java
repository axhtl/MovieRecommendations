package com.example.movierecommendations.recommended.service;

import com.example.movierecommendations.recommended.domain.HrmRecommendation;
import com.example.movierecommendations.recommended.dto.HrmRecommendationDTO;
import com.example.movierecommendations.recommended.repository.HrmRecommendationRepository;
import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HrmRecommendationService {

    private final HrmRecommendationRepository hrmRecommendationRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public HrmRecommendationService(HrmRecommendationRepository hrmRecommendationRepository, MemberRepository memberRepository) {
        this.hrmRecommendationRepository = hrmRecommendationRepository;
        this.memberRepository = memberRepository;
    }

    // 특정 회원에 대한 추천 리스트 조회
    public HrmRecommendation getRecommendationByMemberId(Long memberId) {
        return hrmRecommendationRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found for memberId: " + memberId));
    }

    // 추천 리스트 저장
    public void saveRecommendation(HrmRecommendationDTO hrmRecommendationDTO) {
        // memberId로 Member 객체를 찾기
        Member member = memberRepository.findById(hrmRecommendationDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // HrmRecommendation 엔티티 생성
        HrmRecommendation hrmRecommendation = HrmRecommendation.builder()
                .member(member)
                .hybridRecommendations(hrmRecommendationDTO.getHybridRecommendations())  // DTO에서 하이브리드 추천 리스트 가져오기
                .similarMovies(hrmRecommendationDTO.getSimilarMovies())  // DTO에서 유사 영화 리스트 가져오기
                .build();

        // 엔티티를 저장
        hrmRecommendationRepository.save(hrmRecommendation);
    }
}
