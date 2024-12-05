package com.example.movierecommendations.recommended.service;

import com.example.movierecommendations.recommended.domain.LlmRecommendation;
import com.example.movierecommendations.recommended.dto.LlmRecommendationDTO;
import com.example.movierecommendations.recommended.repository.LlmRecommendationRepository;
import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LlmRecommendationService {

    private final LlmRecommendationRepository llmRecommendationRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public LlmRecommendationService(LlmRecommendationRepository llmRecommendationRepository, MemberRepository memberRepository) {
        this.llmRecommendationRepository = llmRecommendationRepository;
        this.memberRepository = memberRepository;
    }

    // LLM 추천 리스트 저장
    public void saveRecommendation(LlmRecommendationDTO llmRecommendationDTO) {
        // memberId로 Member 객체 찾기
        Member member = memberRepository.findById(llmRecommendationDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // LLMRecommendation 엔터티 생성
        LlmRecommendation llmRecommendation = LlmRecommendation.builder()
                .member(member)
                .llmRecommendations(llmRecommendationDTO.getLlmRecommendations()) // LLM 추천 영화 리스트 설정
                .build();

        // LLMRecommendation 저장
        llmRecommendationRepository.save(llmRecommendation);
    }

    // 특정 회원의 추천 리스트 조회
    public LlmRecommendation getRecommendationByMemberId(Long memberId) {
        return llmRecommendationRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found for memberId: " + memberId));
    }
}
