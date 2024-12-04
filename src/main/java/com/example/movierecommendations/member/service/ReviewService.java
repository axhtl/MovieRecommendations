package com.example.movierecommendations.member.service;

import com.example.movierecommendations.TMDB.TMDBAPIService;
import com.example.movierecommendations.member.domain.*;
import com.example.movierecommendations.member.dto.review.CreateReviewRequestDTO;
import com.example.movierecommendations.member.repository.*;
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

    private final TMDBAPIService tmdbMovieService;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final MovieInfoRepository movieInfoRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final MovieActorRepository movieActorRepository;
    private final MovieDirectorRepository movieDirectorRepository;

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

            // 장르 정보 저장
            saveMovieGenres(movieId, reviewId, movieInfo);

            // 영화 출연진 정보 저장
            saveMovieActors(movieId, reviewId, movieInfo);

            // 영화 감독 정보 저장
            saveMovieDirectors(movieId, reviewId, movieInfo);

        } catch (Exception e) {
            logger.error("Error while saving MovieInfo for movie ID {}: {}", movieId, e.getMessage());
            throw new RuntimeException("Error while saving MovieInfo: " + e.getMessage());
        }
    }

    private void saveMovieGenres(int movieId, long reviewId, MovieInfo movieInfo) {
        try {
            String movieInfoResponse = tmdbMovieService.getMovieDetails(movieId, "ko");
            JsonNode jsonNode = objectMapper.readTree(movieInfoResponse);

            JsonNode genresNode = jsonNode.get("genres");
            if (genresNode != null) {
                for (JsonNode genreNode : genresNode) {
                    String genre = genreNode.get("name").asText();
                    MovieGenre movieGenre = MovieGenre.builder()
                            .movieInfo(movieInfo)
                            .reviewId(reviewId)
                            .genre(genre)
                            .build();
                    movieGenreRepository.save(movieGenre);
                    logger.info("Saved genre: {}", genre);
                }
            }
        } catch (Exception e) {
            logger.error("Error while saving genres for movie ID {}: {}", movieId, e.getMessage());
            throw new RuntimeException("Error while saving genres: " + e.getMessage());
        }
    }

    private void saveMovieActors(int movieId, long reviewId, MovieInfo movieInfo) {
        try {
            // TMDB API 호출 (영화 출연진)
            String movieCreditsResponse = tmdbMovieService.getMovieCredits(movieId, "ko");
            JsonNode creditsNode = objectMapper.readTree(movieCreditsResponse);

            JsonNode castNode = creditsNode.get("cast");
            if (castNode != null) {
                for (JsonNode actorNode : castNode) {
                    String actorName = actorNode.get("name").asText();

                    // MovieActor 객체 생성 및 저장
                    MovieActor movieActor = MovieActor.builder()
                            .movieInfo(movieInfo)
                            .reviewId(reviewId)
                            .actor(actorName)
                            .build();
                    movieActorRepository.save(movieActor);
                    logger.info("Saved actor: {}", actorName);
                }
            }
        } catch (Exception e) {
            logger.error("Error while saving actors for movie ID {}: {}", movieId, e.getMessage());
            throw new RuntimeException("Error while saving actors: " + e.getMessage());
        }
    }

    private void saveMovieDirectors(int movieId, long reviewId, MovieInfo movieInfo) {
        try {
            // TMDB 크레딧 API 호출 (제작진 정보)
            String creditsResponse = tmdbMovieService.getMovieCredits(movieId, "ko");
            JsonNode creditsNode = objectMapper.readTree(creditsResponse);

            // 감독 정보 추출 (job이 "Director"인 사람들만)
            for (JsonNode crewMember : creditsNode.get("crew")) {
                if ("Director".equals(crewMember.get("job").asText())) {
                    String directorName = crewMember.get("name").asText();

                    // MovieDirector 객체 생성
                    MovieDirector movieDirector = MovieDirector.builder()
                            .movieInfo(movieInfo)
                            .reviewId(reviewId)
                            .director(directorName)
                            .build();

                    // MovieDirector 저장
                    movieDirectorRepository.save(movieDirector);
                    logger.info("Director {} saved for movie ID {}", directorName, movieId);
                }
            }

        } catch (Exception e) {
            logger.error("Error while saving MovieDirectors for movie ID {}: {}", movieId, e.getMessage());
            throw new RuntimeException("Error while saving MovieDirectors: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteReviewById(Long reviewId) {
        // 존재 여부 확인 후 삭제
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // MovieInfo 테이블에서 reviewId로 삭제
        movieInfoRepository.deleteByReviewId(reviewId);

        // Review 테이블에서 삭제
        reviewRepository.deleteById(reviewId);
    }
}
