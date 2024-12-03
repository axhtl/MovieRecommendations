package com.example.movierecommendations.member.service;

import com.example.movierecommendations.member.dto.survey.SurveyResponseDTO;
import com.example.movierecommendations.member.dto.RecommendationDTO;
import com.example.movierecommendations.member.domain.Recommendation;
import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.RecommendationRepository;
import com.example.movierecommendations.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final RestTemplate restTemplate;
    private final RecommendationRepository recommendationRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public MovieService(RestTemplate restTemplate, RecommendationRepository recommendationRepository,
                        MemberRepository memberRepository) {
        this.restTemplate = restTemplate;
        this.recommendationRepository = recommendationRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Flask 서버에서 추천 영화 목록을 가져오는 메소드
     */
    public String getRecommendationFromFlask(SurveyResponseDTO userData, List<String> userBehaviorData, String movieName) {
        String flaskUrl = "http://localhost:5000/api/ai-recommendation";  // Flask 서버 URL

        // HTTP 요청 본문을 설정 (기존 방식 그대로 사용)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문을 객체로 전달하는 부분
        FlaskRequestBody requestBody = new FlaskRequestBody(userData.getGender().toString(), userData.getAge(),
                userData.getPreferredGenres(), userData.getPreferredActors(), userBehaviorData, movieName);

        HttpEntity<FlaskRequestBody> entity = new HttpEntity<>(requestBody, headers);

        // Flask 서버로 데이터 전송
        URI uri = UriComponentsBuilder.fromHttpUrl(flaskUrl)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // Flask 서버 응답 처리
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        return responseEntity.getBody();  // Flask 서버에서 반환한 추천 결과 반환
    }

    /**
     * TMDB API에서 영화 정보를 가져오는 메소드
     */
    public String searchMovies(String query, boolean includeAdult, String language, int page) {
        String API_URL = "https://api.themoviedb.org/3/search/movie";
        String API_KEY = "YOUR_TMDB_API_KEY";  // TMDB API 키

        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s?query=%s&include_adult=%s&language=%s&page=%d",
                API_URL, query, includeAdult, language, page);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();  // TMDB API 응답
            } else {
                throw new RuntimeException("Failed to fetch movie data: " + response.message());
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while calling the TMDb API", e);
        }
    }

    /**
     * 추천 결과 저장 및 반환
     */
    public RecommendationDTO saveRecommendation(Long memberId, SurveyResponseDTO userData, List<String> userBehaviorData, String movieName) {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // userBehaviorData는 RecommendationRepository에서 가져오기
        Optional<Recommendation> existingRecommendation = recommendationRepository.findByMemberId(memberId);
        List<String> movieRecommendations = null;

        if (existingRecommendation.isPresent()) {
            movieRecommendations = existingRecommendation.get().getMovieRecommendations();
        }

        // Flask 서버로부터 추천 영화 목록 받기
        String recommendation = getRecommendationFromFlask(userData, userBehaviorData, movieName);

        // TMDB에서 영화 정보 검색
        String movieRecommendationsInfo = searchMovies(recommendation, false, "ko", 1);

        // 추천 결과 저장
        Recommendation recommendationEntity = new Recommendation();
        recommendationEntity.setMember(member);
        recommendationEntity.setMovieRecommendations(List.of(movieRecommendationsInfo));

        // DB에 추천 결과 저장
        recommendationRepository.save(recommendationEntity);

        // RecommendationDTO로 반환
        return new RecommendationDTO(memberId, List.of(movieRecommendationsInfo));
    }

    // Flask 서버로 보낼 요청 본문
    private static class FlaskRequestBody {
        public final String gender;
        public final String age;
        public final List<String> preferredGenres;
        public final List<String> preferredActors;
        public final List<String> userBehaviorData;
        public final String movieName;

        public FlaskRequestBody(String gender, String age, List<String> preferredGenres, List<String> preferredActors,
                                List<String> userBehaviorData, String movieName) {
            this.gender = gender;
            this.age = age;
            this.preferredGenres = preferredGenres;
            this.preferredActors = preferredActors;
            this.userBehaviorData = userBehaviorData;
            this.movieName = movieName;
        }
    }
}
