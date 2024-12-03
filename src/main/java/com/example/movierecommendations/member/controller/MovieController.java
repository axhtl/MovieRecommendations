package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.RecommendationDTO;
import com.example.movierecommendations.member.dto.survey.SurveyResponseDTO;
import com.example.movierecommendations.member.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * user_data와 user_behavior_data, 영화 이름을 바탕으로 추천 결과를 저장하고 반환하는 엔드포인트
     */
    @PostMapping("/save-recommendations/{memberId}")
    public ResponseEntity<RecommendationDTO> saveRecommendations(
            @PathVariable Long memberId,
            @RequestBody SurveyResponseDTO userData,
            @RequestParam String movieName,
            @RequestParam List<String> userBehaviorData) {

        // 추천 결과를 저장하고 반환
        RecommendationDTO recommendationDTO = movieService.saveRecommendation(memberId, userData, userBehaviorData, movieName);

        return ResponseEntity.ok(recommendationDTO);
    }

    // 검색된 영화를 TMDB API로부터 받아오는 엔드포인트 추가
    @GetMapping("/search")
    public ResponseEntity<String> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "false") boolean includeAdult,
            @RequestParam(defaultValue = "ko") String language,
            @RequestParam(defaultValue = "1") int page) {
        // MovieService에서 TMDB API 호출
        String movieInfo = movieService.searchMovies(query, includeAdult, language, page);
        return ResponseEntity.ok(movieInfo);
    }
}
