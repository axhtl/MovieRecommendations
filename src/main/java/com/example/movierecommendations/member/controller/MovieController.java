package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.survey.SurveyResponseDTO;
import com.example.movierecommendations.member.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/movie")
public class MovieController {

    private final MovieService movieService;

    /**
     * 영화 목록을 검색하는 API
     * @param movieName 검색할 영화 이름
     * @param itemPerPage 한 번에 가져올 영화 목록의 수 (기본값: 10)
     * @return 검색된 영화 목록 (JSON 형태로 반환)
     */
    @GetMapping("/search-movies")
    public String searchMovies(
            @RequestParam String movieName, // 검색할 영화 이름
            @RequestParam(required = false, defaultValue = "10") int itemPerPage // 한 번에 가져올 영화 수
    ) {
        return movieService.searchMoviesByName(movieName, itemPerPage); // 영화 목록 반환
    }

    /**
     * 영화 상세 정보를 조회하는 API
     * @param movieCd 영화 코드
     * @param responseType 응답 형식 (json 또는 xml)
     * @return 영화 상세 정보 (JSON 또는 XML)
     */
    @GetMapping("/search-details")
    public String searchMovieDetails(
            @RequestParam String movieCd, // 영화 코드
            @RequestParam(defaultValue = "json") String responseType // 응답 형식 (json 또는 xml)
    ) {
        return movieService.searchMovieDetails(movieCd, responseType); // 영화 상세 정보 반환
    }

    /**
     * 영화 추천을 요청하는 API (설문 데이터를 Flask 서버로 전달)
     * @param surveyData 설문조사 데이터
     * @param movieName 추천할 영화 이름
     * @return Flask 서버로부터 받은 추천 결과
     */
    @PostMapping("/recommendation")
    public String getMovieRecommendation(
            @RequestBody SurveyResponseDTO surveyData, // 설문조사 데이터
            @RequestParam String movieName // 추천할 영화 이름
    ) {
        // Flask 서버로 설문 데이터를 보내고 추천 결과를 받아옴
        return movieService.getRecommendationFromFlask(surveyData, movieName);
    }
}
