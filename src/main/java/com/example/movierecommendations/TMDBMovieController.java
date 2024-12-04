package com.example.movierecommendations;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class TMDBMovieController {

    private final TMDBMovieService TMDBMovieService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 추가

    // 영화 검색
    @GetMapping("/search")
    public ResponseEntity<String> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "false") boolean includeAdult,
            @RequestParam(defaultValue = "ko") String language,
            @RequestParam(defaultValue = "1") int page) {

        // TMDB API로부터 받아온 JSON 응답
        String jsonResponse = TMDBMovieService.searchMovies(query, includeAdult, language, page);

        try {
            // JSON을 보기 좋게 포맷팅
            String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));

            // 포맷팅된 JSON 응답 반환
            return ResponseEntity.ok(formattedJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error formatting JSON response.");
        }
    }

    // 영화 상세정보 검색
    @GetMapping("/{movieId}")
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
}