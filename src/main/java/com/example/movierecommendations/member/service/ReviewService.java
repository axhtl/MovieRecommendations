package com.example.movierecommendations.member.service;

import com.example.movierecommendations.TMDBMovieService;
import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.domain.MovieInfo;
import com.example.movierecommendations.member.domain.Review;
import com.example.movierecommendations.member.dto.review.CreateReviewRequestDTO;
import com.example.movierecommendations.member.repository.MemberRepository;
import com.example.movierecommendations.member.repository.MovieInfoRepository;
import com.example.movierecommendations.member.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${movie.apikey}")
    private String API_KEY;

    private final TMDBMovieService tmdbMovieService;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final MovieInfoRepository movieInfoRepository;
//    private final MovieActorRepository movieActorRepository;
//    private final MovieGenreRepository movieGenreRepository;
//    private final MovieDirectorRepository movieDirectorRepository;

    @Transactional
    public Long saveReview(Long memberId, CreateReviewRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 리뷰 저장
        Review review = request.toReview(member);
        reviewRepository.save(review);

        // 영화 정보 저장
        saveMovieInfo(request.getMovieId(), review.getReviewId());

        return review.getReviewId();
    }

    private void saveMovieInfo(int movieId, Long reviewId) {
        try {
            // 이미 저장된 영화인지 확인
            if (movieInfoRepository.existsByMovieId(movieId)) {
                logger.info("Movie with ID {} already exists. Skipping save.", movieId);
                return;
            }

            // TMDB API 호출
            String movieInfoResponse = tmdbMovieService.getMovieDetails(movieId, "ko");
            JsonNode jsonNode = objectMapper.readTree(movieInfoResponse);

            // JSON 데이터 파싱 및 MovieInfo 객체 생성
            MovieInfo movieInfo = MovieInfo.builder()
                    .reviewId(reviewId)
                    .movieId(movieId)
                    .title(jsonNode.get("title").asText())
                    .originalTitle(jsonNode.get("original_title").asText())
                    .runtime(jsonNode.has("runtime") && !jsonNode.get("runtime").isNull() ? jsonNode.get("runtime").asText() : null)
                    .releaseDate(jsonNode.has("release_date") && !jsonNode.get("release_date").isNull() ? jsonNode.get("release_date").asText() : null)
                    .originCountry(jsonNode.has("production_countries") && !jsonNode.get("production_countries").isEmpty()
                            ? jsonNode.get("production_countries").get(0).get("name").asText()
                            : null)
                    .build();

            // MovieInfo 저장
            movieInfoRepository.save(movieInfo);
            logger.info("MovieInfo saved successfully for movie ID {}", movieId);

        } catch (Exception e) {
            logger.error("Error while saving MovieInfo for movie ID {}: {}", movieId, e.getMessage());
            throw new RuntimeException("Error while saving MovieInfo: " + e.getMessage());
        }
    }
}
