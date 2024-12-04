package com.example.movierecommendations.member.service;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.domain.PreferredActor;
import com.example.movierecommendations.member.domain.PreferredGenre;
import com.example.movierecommendations.member.domain.Survey;
import com.example.movierecommendations.member.dto.CreateSurveyRequestDTO;
import com.example.movierecommendations.member.dto.survey.SurveyResponseDTO;
import com.example.movierecommendations.member.repository.MemberRepository;
import com.example.movierecommendations.member.repository.PreferredActorRepository;
import com.example.movierecommendations.member.repository.PreferredGenreRepository;
import com.example.movierecommendations.member.repository.SurveyRepository;
import com.example.movierecommendations.member.vo.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // 설문조사 조회
    @Transactional(readOnly = true)
    public SurveyResponseDTO getSurveyByMemberId(Long memberId) {
        // Survey 조회
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 설문조사가 존재하지 않습니다."));

        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 선호 장르 조회
        List<String> preferredGenres = preferredGenreRepository.findByMember_MemberId(memberId)
                .stream()
                .map(PreferredGenre::getGenre)
                .collect(Collectors.toList());

        // 선호 배우 조회
        List<String> preferredActors = preferredActorRepository.findByMember_MemberId(memberId)
                .stream()
                .map(PreferredActor::getActor)
                .collect(Collectors.toList());

        // SurveyResponseDTO 생성 후 반환
        return SurveyResponseDTO.builder()
                .surveyId(survey.getSurveyId())
                .gender(survey.getGender())
                .age(survey.getAge())
                .preferredGenres(preferredGenres)
                .preferredActors(preferredActors)
                .memberId(member.getMemberId()) // 회원 ID
                .membername(member.getMembername()) // 회원 이름
                .nickname(member.getNickname()) // 닉네임
                .build();
    }

    // 성별 수정
    @Transactional
    public void updateGender(Long memberId, Gender gender) {
        // Survey 객체를 조회하고 없으면 예외 처리
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문조사를 찾을 수 없습니다. memberId: " + memberId));

        // Survey 객체의 성별을 업데이트
        survey.updateGender(gender);

        // 업데이트된 Survey 객체를 저장
        surveyRepository.save(survey);
    }

    // 나이 수정
    @Transactional
    public void updateAge(Long memberId, String age) {
        // Survey 객체를 조회하고 없으면 예외 처리
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문조사를 찾을 수 없습니다. memberId: " + memberId));

        // Survey 객체의 나이를 업데이트
        survey.updateAge(age);

        // 업데이트된 Survey 객체를 저장
        surveyRepository.save(survey);
    }

    // 선호 장르 추가
    @Transactional
    public void addPreferredGenre(Long memberId, String genre) {
        // Optional에서 Member 객체를 가져옴. 없으면 예외 처리.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다. memberId: " + memberId));

        PreferredGenre preferredGenre = PreferredGenre.builder()
                .member(member)
                .genre(genre)
                .build();
        preferredGenreRepository.save(preferredGenre);
    }

    // 선호 장르 삭제
    @Transactional
    public void deletePreferredGenre(Long preferredGenreId) {
        PreferredGenre preferredGenre = preferredGenreRepository.findById(preferredGenreId)
                .orElseThrow(() -> new RuntimeException("해당 preferredGenreId에 대한 선호 장르가 존재하지 않습니다."));

        preferredGenreRepository.deleteByPreferredGenreId(preferredGenreId);
    }

    // 선호 배우 추가
    @Transactional
    public void addPreferredActor(Long memberId, String actor) {
        // Optional에서 Member 객체를 가져옴. 없으면 예외 처리.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다. memberId: " + memberId));

        PreferredActor preferredActor = PreferredActor.builder()
                .member(member)
                .actor(actor)
                .build();
        preferredActorRepository.save(preferredActor);
    }

    // 선호 배우 삭제
    @Transactional
    public void deletePreferredActor(Long preferredActorId) {
        PreferredActor preferredActor = preferredActorRepository.findById(preferredActorId)
                .orElseThrow(() -> new RuntimeException("해당 preferredGenreId에 대한 선호 장르가 존재하지 않습니다."));

        preferredActorRepository.deleteByPreferredActorId(preferredActorId);
    }
}

