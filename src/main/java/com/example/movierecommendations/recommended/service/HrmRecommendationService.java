package com.example.movierecommendations.recommended.service;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.MemberRepository;
import com.example.movierecommendations.recommended.domain.HrmRecommendation;
import com.example.movierecommendations.recommended.dto.HrmRecommendationDTO;
import com.example.movierecommendations.recommended.repository.HrmRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // 추천 영화 목록을 저장하는 메서드 (기존의 hybrid + similar 영화 목록 통합)
    public void saveRecommendationForMember(Long memberId, List<String> recommendedMovies) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // HRMRecommendation 엔티티 생성 및 저장 (하나의 추천 리스트로 저장)
        HrmRecommendation hrmRecommendation = HrmRecommendation.builder()
                .member(member)
                .recommendations(recommendedMovies)  // 통합된 추천 영화 목록 설정
                .build();

        hrmRecommendationRepository.save(hrmRecommendation);
    }

    // DTO에서 받은 추천 영화 목록을 저장하는 메서드 (기존의 hybrid + similar 영화 목록 통합)
    public void saveRecommendation(HrmRecommendationDTO hrmRecommendationDTO) {
        // 1. DTO에서 memberId를 사용하여 Member 객체를 찾기
        Member member = memberRepository.findById(hrmRecommendationDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + hrmRecommendationDTO.getMemberId()));

        // 2. DTO에서 받은 추천 영화 목록을 사용하여 HrmRecommendation 엔티티 생성
        HrmRecommendation hrmRecommendation = HrmRecommendation.builder()
                .member(member)  // 찾은 회원을 연결
                .recommendations(hrmRecommendationDTO.getRecommendations())  // 통합된 추천 영화 목록 설정
                .build();

        // 3. HrmRecommendation 엔티티를 데이터베이스에 저장
        hrmRecommendationRepository.save(hrmRecommendation);
    }
}



