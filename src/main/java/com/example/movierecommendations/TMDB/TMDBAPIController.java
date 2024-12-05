package com.example.movierecommendations.TMDB;

import com.example.movierecommendations.AIMODEL.AIModelService;
import com.example.movierecommendations.member.domain.PreferredActor;
import com.example.movierecommendations.member.domain.PreferredGenre;
import com.example.movierecommendations.member.domain.Survey;
import com.example.movierecommendations.member.repository.PreferredActorRepository;
import com.example.movierecommendations.member.repository.PreferredGenreRepository;
import com.example.movierecommendations.member.repository.SurveyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import netscape.javascript.JSObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class TMDBAPIController {

    private final TMDBAPIService TMDBMovieService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 추가
    private final SurveyRepository surveyRepository;
    private final PreferredGenreRepository preferredGenreRepository;
    private final PreferredActorRepository preferredActorRepository;
    private final AIModelService aiModelService;

    // 영화 검색
    @GetMapping("/search")
    public ResponseEntity<String> searchMovies(
            @PathVariable("memberId") Long memberId,  // 검색을 했을 때 설문정보와 검색 영화이름을 ai 모델에 전달해야 함
            @RequestParam("query") String query,
            @RequestParam(defaultValue = "false") boolean includeAdult,
            @RequestParam(defaultValue = "ko") String language,
            @RequestParam(defaultValue = "1") int page) {

        // TMDB API로부터 받아온 JSON 응답
        String jsonResponse = TMDBMovieService.searchMovies(query, includeAdult, language, page);

        // 설문조사 정보 조회
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        // 좋아하는 장르 정보 조회
        PreferredGenre preferredGenre = (PreferredGenre) preferredGenreRepository.findByMember_MemberId(memberId);

        // 좋아하는 배우 정보 조회
        PreferredActor preferredActor = (PreferredActor) preferredActorRepository.findByMember_MemberId(memberId);

        // 객체를 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> surveyMap = objectMapper.convertValue(survey, Map.class);
        Map<String, Object> preferGenre = objectMapper.convertValue(preferredGenre, Map.class);
        Map<String, Object> preferActor = objectMapper.convertValue(preferredActor, Map.class);

        // 모든 Map을 하나로 합침
        Map<String, Object> inputData = new HashMap<>();
        inputData.putAll(surveyMap);      // surveyMap을 inputData에 추가
        inputData.putAll(preferGenre);    // preferGenre을 inputData에 추가
        inputData.putAll(preferActor);    // preferActor을 inputData에 추가

        // target_movie 값을 inputData에 추가
        inputData.put("target_movie", query);  // target_movie를 inputData에 추가

        try {
            // JSON을 보기 좋게 포맷팅
            String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));

            // hrmModel 실행후 테이블에 저장
            aiModelService.callHRMModel(inputData, memberId);
            // 포맷팅된 JSON 응답 반환
            return ResponseEntity.ok(formattedJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error formatting JSON response.");
        }
    }

    // 영화 상세정보 검색
    @GetMapping("/detail/{movieId}")
    public ResponseEntity<String> getMovieDetails(
            @PathVariable int movieId,
            @RequestParam(required = false, defaultValue = "ko") String language) {
        // TMDB API로부터 받아온 JSON 응답
        String jsonResponse = TMDBMovieService.getMovieDetails(movieId,language);

        try {
            // JSON을 보기 좋게 포맷팅
            String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));

            // 포맷팅된 JSON 응답 반환
            return ResponseEntity.ok(formattedJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error formatting JSON response.");
        }
    }

    // 영화 크레딧 정보 검색
    @GetMapping("/{movieId}/credits")
    public ResponseEntity<String> getMovieCredits(
            @PathVariable int movieId,
            @RequestParam(defaultValue = "ko") String language) {

        String jsonResponse = TMDBMovieService.getMovieCredits(movieId, language);

        try {
            // JSON 포맷팅
            String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                    objectMapper.readTree(jsonResponse)
            );
            return ResponseEntity.ok(formattedJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("JSON 포맷팅 중 오류 발생.");
        }
    }
}
