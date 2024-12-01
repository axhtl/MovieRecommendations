package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/movie")
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/search-movies")
    public String searchMovies(
            @RequestParam String movieName,
            @RequestParam(required = false, defaultValue = "10") int itemPerPage
    ) {
        return movieService.searchMoviesByName(movieName, itemPerPage);
    }

    // 영화 상세 정보 조회 API
    @GetMapping("/search-details")
    public String searchMovieDetails(
            @RequestParam String movieCd, // 영화코드
            @RequestParam(defaultValue = "json") String responseType // 응답 형식 (json 또는 xml)
    ) {
        return movieService.searchMovieDetails(movieCd, responseType);
    }
}
