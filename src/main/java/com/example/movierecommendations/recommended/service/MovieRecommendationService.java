package com.example.movierecommendations.recommended.service;

import com.example.movierecommendations.recommended.domain.MovieRecommendation;
import com.example.movierecommendations.recommended.dto.MovieRecommendationDTO;
import com.example.movierecommendations.recommended.repository.MovieRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieRecommendationService {

    private final MovieRecommendationRepository movieRecommendationRepository;

    @Autowired
    public MovieRecommendationService(MovieRecommendationRepository movieRecommendationRepository) {
        this.movieRecommendationRepository = movieRecommendationRepository;
    }

    // 영화 추천을 저장하는 메소드
    public MovieRecommendation saveMovieRecommendation(MovieRecommendationDTO movieRecommendationDTO) {
        MovieRecommendation movieRecommendation = MovieRecommendation.builder()
                .movieCd(movieRecommendationDTO.getMovieCd())
                .movieNm(movieRecommendationDTO.getMovieNm())
                .build();

        return movieRecommendationRepository.save(movieRecommendation);
    }

    // 여러 영화 추천을 저장하는 메소드
    public List<MovieRecommendation> saveMovieRecommendations(List<MovieRecommendationDTO> movieRecommendationDTOs) {
        List<MovieRecommendation> movieRecommendations = movieRecommendationDTOs.stream()
                .map(dto -> MovieRecommendation.builder()
                        .movieCd(dto.getMovieCd())
                        .movieNm(dto.getMovieNm())
                        .build())
                .collect(Collectors.toList());

        return movieRecommendationRepository.saveAll(movieRecommendations);
    }

    // 영화 코드로 추천 영화 찾기
    public MovieRecommendation getMovieRecommendation(Long movieCd) {
        return movieRecommendationRepository.findByMovieCd(movieCd);
    }

    // 모든 영화 추천 리스트 조회
    public List<MovieRecommendation> getAllMovieRecommendations() {
        return movieRecommendationRepository.findAll();
    }
}
