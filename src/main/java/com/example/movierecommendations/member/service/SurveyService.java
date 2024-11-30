package com.example.movierecommendations.member.service;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.domain.PreferredActor;
import com.example.movierecommendations.member.domain.PreferredGenre;
import com.example.movierecommendations.member.domain.Survey;
import com.example.movierecommendations.member.dto.CreateSurveyRequestDTO;
import com.example.movierecommendations.member.repository.MemberRepository;
import com.example.movierecommendations.member.repository.PreferredActorRepository;
import com.example.movierecommendations.member.repository.PreferredGenreRepository;
import com.example.movierecommendations.member.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final PreferredGenreRepository preferredGenreRepository;
    private final PreferredActorRepository preferredActorRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long saveSurvey(Long memberId, CreateSurveyRequestDTO request) {
        // 입력값 검증 - NULL 체크
        validateCreateSurveyRequest(request);

        // memberId로 이미 설문조사가 등록되었는지 확인
        if (surveyRepository.existsByMemberId(memberId)) {
            throw new IllegalStateException("이미 해당 회원에 대한 설문조사가 등록되어 있습니다.");
        }

        // memberId로 Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 설문조사 저장
        Survey survey = request.toSurvey(memberId);
        surveyRepository.save(survey);

        // 선호 장르 저장
        List<PreferredGenre> preferredGenres = request.toPreferredGenres(member);
        preferredGenreRepository.saveAll(preferredGenres);

        // 선호 배우 저장
        List<PreferredActor> preferredActor = request.toPreferredActor(member);
        preferredActorRepository.saveAll(preferredActor);

        return survey.getSurveyId();
    }

    private void validateCreateSurveyRequest(CreateSurveyRequestDTO request) {
        if (request.getGender() == null) {
            throw new IllegalArgumentException("성별이 입력되지 않았습니다.");
        }
        if (request.getAge() == null || request.getAge().trim().isEmpty()) {
            throw new IllegalArgumentException("나이가 입력되지 않았습니다.");
        }
        if (request.getPreferredGenres() == null || request.getPreferredGenres().isEmpty()) {
            throw new IllegalArgumentException("선호장르가 선택되지 않았습니다.");
        }
        if (request.getPreferredActors() == null || request.getPreferredActors().isEmpty()) {
            throw new IllegalArgumentException("선호배우가 선택되지 않았습니다.");
        }
    }
}

