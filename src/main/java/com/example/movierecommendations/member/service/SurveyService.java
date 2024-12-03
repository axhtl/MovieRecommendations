package com.example.movierecommendations.member.service;

import com.example.movierecommendations.member.domain.*;
import com.example.movierecommendations.member.dto.CreateSurveyRequestDTO;
import com.example.movierecommendations.member.dto.survey.SurveyResponseDTO;
import com.example.movierecommendations.member.dto.RecommendationDTO;
import com.example.movierecommendations.member.repository.*;
import com.example.movierecommendations.member.vo.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final PreferredGenreRepository preferredGenreRepository;
    private final PreferredActorRepository preferredActorRepository;
    private final MemberRepository memberRepository;
    private final RecommendationRepository recommendationRepository;

    private final RestTemplate restTemplate;

    // AI 서버 연동을 위한 URL
    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Transactional
    public Long saveSurvey(Long memberId, CreateSurveyRequestDTO request) {
        // 입력값 검증 - NULL 체크
        validateCreateSurveyRequest(request);

        // 이미 설문조사 등록 여부 확인
        if (surveyRepository.existsByMemberId(memberId)) {
            throw new IllegalStateException("이미 해당 회원에 대한 설문조사가 등록되어 있습니다.");
        }

        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 설문조사 저장
        Survey survey = request.toSurvey(memberId);
        surveyRepository.save(survey);

        // 선호 장르 및 배우 저장
        List<PreferredGenre> preferredGenres = request.toPreferredGenres(member);
        preferredGenreRepository.saveAll(preferredGenres);

        List<PreferredActor> preferredActors = request.toPreferredActor(member);
        preferredActorRepository.saveAll(preferredActors);

        // AI 서버에서 추천 영화 받아오기
        List<String> recommendations = getRecommendationsFromAI(memberId, request);

        // 추천 결과 저장
        saveRecommendations(member, recommendations);

        return survey.getSurveyId();
    }

    // AI 서버에서 추천 영화 받아오기
    private List<String> getRecommendationsFromAI(Long memberId, CreateSurveyRequestDTO request) {
        // AI 서버에 보내기 위한 요청 데이터 구성
        String url = aiServerUrl + "/api/ai-recommendation";

        // 요청에 필요한 데이터 구성 (선호 장르와 선호 배우 정보를 AI 서버로 전달)
        String userInput = createUserInput(request);  // 선호 장르 및 선호 배우 정보를 AI 서버에 전달
        String userBehaviorData = createUserBehaviorData();  // 모든 사용자들의 추천 영화 데이터 전달
        String movieName = "사용자 선택 영화 이름";  // 예시로 영화 이름을 하드코딩 혹은 다른 로직으로 처리할 수 있음

        // AI 서버에 요청 보내기
        String payload = "{"
                + "\"user_input\": \"" + userInput + "\","
                + "\"user_behavior_data\": \"" + userBehaviorData + "\","
                + "\"movie_name\": \"" + movieName + "\""
                + "}";

        // 직접 결과를 받을 수 있도록 응답을 List 형태로 처리
        List<String> recommendedMovies = restTemplate.postForObject(url, payload, List.class);

        if (recommendedMovies == null) {
            throw new RuntimeException("AI 서버로부터 추천 결과를 받을 수 없습니다.");
        }

        return recommendedMovies;
    }


    // 사용자 선호 정보를 AI 서버에서 사용할 형식으로 변환
    private String createUserInput(CreateSurveyRequestDTO request) {
        // 선호 장르, 선호 배우, 성별, 나이를 JSON 형식으로 변환
        return "{"
                + "\"gender\": \"" + request.getGender() + "\","
                + "\"age\": \"" + request.getAge() + "\","
                + "\"preferred_genres\": " + request.getPreferredGenres() + ","
                + "\"preferred_actors\": " + request.getPreferredActors()
                + "}";
    }

    // 모든 사용자들의 추천 영화를 생성하여 전달하는 메소드
    private String createUserBehaviorData() {
        // 모든 사용자들의 추천 영화 리스트를 가져오기
        List<Member> allMembers = memberRepository.findAll();  // 모든 회원을 조회
        List<String> userBehaviorDataList = allMembers.stream()
                .map(member -> {
                    List<String> recommendedMovies = recommendationRepository.findByMemberId(member.getMemberId())
                            .map(Recommendation::getMovieRecommendations)
                            .orElse(List.of());  // 추천 영화가 없다면 빈 리스트 반환

                    return "{"
                            + "\"user_id\": \"" + member.getMemberId() + "\","
                            + "\"liked_movies\": " + recommendedMovies
                            + "}";
                })
                .collect(Collectors.toList());

        // 전체 데이터를 JSON 형식으로 변환하여 반환
        return "[" + String.join(",", userBehaviorDataList) + "]";
    }

    // 추천 결과 저장
    private void saveRecommendations(Member member, List<String> recommendations) {
        // Recommendation 엔티티를 통해 AI 추천 결과를 저장
        Recommendation recommendation = Recommendation.builder()
                .member(member)
                .movieRecommendations(recommendations)
                .build();

        recommendationRepository.save(recommendation);
    }

    // 설문조사 조회
    @Transactional(readOnly = true)
    public SurveyResponseDTO getSurveyByMemberId(Long memberId) {
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 설문조사가 존재하지 않습니다."));

        List<String> preferredGenres = preferredGenreRepository.findByMember_MemberId(memberId)
                .stream()
                .map(PreferredGenre::getGenre)
                .collect(Collectors.toList());

        List<String> preferredActors = preferredActorRepository.findByMember_MemberId(memberId)
                .stream()
                .map(PreferredActor::getActor)
                .collect(Collectors.toList());

        return SurveyResponseDTO.builder()
                .surveyId(survey.getSurveyId())
                .gender(survey.getGender())
                .age(survey.getAge())
                .preferredGenres(preferredGenres)
                .preferredActors(preferredActors)
                .build();
    }

    // 설문조사 유효성 검사
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

    // 추천 조회
    @Transactional(readOnly = true)
    public RecommendationDTO getRecommendation(Long memberId) {
        Recommendation recommendation = (Recommendation) recommendationRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 추천 영화가 존재하지 않습니다."));

        return RecommendationDTO.builder()
                .memberId(memberId)
                .movieRecommendations(recommendation.getMovieRecommendations())
                .build();
    }

    // 성별 수정
    @Transactional
    public void updateGender(Long memberId, Gender gender) {
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문조사를 찾을 수 없습니다. memberId: " + memberId));
        survey.updateGender(gender);
        surveyRepository.save(survey);
    }

    // 나이 수정
    @Transactional
    public void updateAge(Long memberId, String age) {
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 설문조사를 찾을 수 없습니다. memberId: " + memberId));
        survey.updateAge(age);
        surveyRepository.save(survey);
    }

    // 선호 장르 추가
    @Transactional
    public void addPreferredGenre(Long memberId, String genre) {
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
                .orElseThrow(() -> new RuntimeException("해당 preferredActorId에 대한 선호 배우가 존재하지 않습니다."));
        preferredActorRepository.deleteByPreferredActorId(preferredActorId);
    }
}
