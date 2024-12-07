package com.example.movierecommendations.recommended.controller;

import com.example.movierecommendations.recommended.dto.MovieRecommendationDTO;
import com.example.movierecommendations.recommended.domain.MovieRecommendation;
import com.example.movierecommendations.recommended.service.MovieRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies/rec/")
public class MovieRecommendationController {

    private final MovieRecommendationService movieRecommendationService;

    @Autowired
    public MovieRecommendationController(MovieRecommendationService movieRecommendationService) {
        this.movieRecommendationService = movieRecommendationService;
    }

    // 영화 추천 하나 저장
    @PostMapping("/recommend")
    public ResponseEntity<MovieRecommendation> saveMovieRecommendation(@RequestBody MovieRecommendationDTO movieRecommendationDTO) {
        MovieRecommendation savedRecommendation = movieRecommendationService.saveMovieRecommendation(movieRecommendationDTO);
        return new ResponseEntity<>(savedRecommendation, HttpStatus.CREATED);
    }

    // 여러 영화 추천 저장
    @PostMapping("/recommendations")
    public ResponseEntity<List<MovieRecommendation>> saveMovieRecommendations(@RequestBody List<MovieRecommendationDTO> movieRecommendationDTOs) {
        List<MovieRecommendation> savedRecommendations = movieRecommendationService.saveMovieRecommendations(movieRecommendationDTOs);
        return new ResponseEntity<>(savedRecommendations, HttpStatus.CREATED);
    }

    // 영화 코드로 추천 영화 찾기
    @GetMapping("/{movieCd}")
    public ResponseEntity<MovieRecommendation> getMovieRecommendation(@PathVariable Long movieCd) {
        MovieRecommendation movieRecommendation = movieRecommendationService.getMovieRecommendation(movieCd);
        if (movieRecommendation != null) {
            return new ResponseEntity<>(movieRecommendation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 모든 영화 추천 리스트 조회
    @GetMapping("/all")
    public ResponseEntity<List<MovieRecommendation>> getAllMovieRecommendations() {
        List<MovieRecommendation> movieRecommendations = movieRecommendationService.getAllMovieRecommendations();
        return new ResponseEntity<>(movieRecommendations, HttpStatus.OK);
    }
}
