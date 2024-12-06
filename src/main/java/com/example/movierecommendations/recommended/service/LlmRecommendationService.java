package com.example.movierecommendations.recommended.service;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.MemberRepository;
import com.example.movierecommendations.member.repository.PreferredActorRepository;
import com.example.movierecommendations.member.repository.PreferredGenreRepository;
import com.example.movierecommendations.member.repository.SurveyRepository;
import com.example.movierecommendations.member.service.MemberService;
import com.example.movierecommendations.recommended.domain.LlmRecommendation;
import com.example.movierecommendations.recommended.dto.LlmRecommendationDTO;
import com.example.movierecommendations.recommended.repository.LlmRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LlmRecommendationService {

    private final LlmRecommendationRepository llmRecommendationRepository;
    private final MemberRepository memberRepository;

    private final SurveyRepository surveyRepository;
    private final PreferredGenreRepository preferredGenreRepository;
    private final PreferredActorRepository preferredActorRepository;
    //private final AIModelService aiModelService;
    private final MemberService memberService;

    public void saveRecommendationForMember(Long memberId, List<String> recommendedMovies) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // LlmRecommendation 엔티티 생성 및 저장
        LlmRecommendation llmRecommendation = LlmRecommendation.builder()
                .member(member)
                .llmRecommendations(recommendedMovies)
                .build();

        llmRecommendationRepository.save(llmRecommendation);
    }

    // 특정 회원의 추천 리스트 조회
    public LlmRecommendation getRecommendationByMemberId(Long memberId) {
        return llmRecommendationRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found for memberId: " + memberId));
    }

    // LlmRecommendationDTO를 기반으로 추천을 저장
    public void saveRecommendation(LlmRecommendationDTO llmRecommendationDTO) {
        // 1. DTO에서 memberId를 사용하여 Member 객체를 찾기
        Member member = memberRepository.findById(llmRecommendationDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + llmRecommendationDTO.getMemberId()));

        // 2. DTO에서 받은 추천 영화 목록을 사용하여 LlmRecommendation 엔티티 생성
        LlmRecommendation llmRecommendation = LlmRecommendation.builder()
                .member(member)  // 찾은 회원을 연결
                .llmRecommendations(llmRecommendationDTO.getLlmRecommendations())  // 추천 영화 리스트 설정
                .build();

        // 3. 엔티티 저장
        llmRecommendationRepository.save(llmRecommendation);
    }
}
