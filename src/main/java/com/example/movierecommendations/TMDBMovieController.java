package com.example.movierecommendations;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/api/movies")
//@RequiredArgsConstructor
//public class TMDBMovieController {
//
//    private final TMDBMovieService TMDBMovieService;
//
//    @GetMapping("/search")
//    public String searchMovies(
//            @RequestParam String query,
//            @RequestParam(defaultValue = "false") boolean includeAdult,
//            @RequestParam(defaultValue = "ko") String language,
//            @RequestParam(defaultValue = "1") int page) {
//        return TMDBMovieService.searchMovies(query, includeAdult, language, page);
//    }
//}

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class TMDBMovieController {

    private final TMDBMovieService TMDBMovieService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 추가

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
}