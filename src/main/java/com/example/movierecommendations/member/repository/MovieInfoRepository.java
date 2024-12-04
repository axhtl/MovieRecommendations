package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MovieInfoRepository extends JpaRepository<MovieInfo, Long> {
    boolean existsByMovieId(int movieId);

    // reviewId로 삭제하는 JPQL 쿼리
    @Modifying
    @Transactional
    @Query("DELETE FROM movie_info m WHERE m.reviewId = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);
}
