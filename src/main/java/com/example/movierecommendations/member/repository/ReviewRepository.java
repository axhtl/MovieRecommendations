package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 영화 코드(movieCd)에 해당하는 모든 리뷰 조회
    List<Review> findByMovieCd(String movieCd);

    // 특정 회원(memberId)의 모든 리뷰 조회
    List<Review> findByMember_MemberId(Long memberId);

    // 특정 리뷰 ID로 리뷰 조회
    Optional<Review> findByReviewId(Long reviewId);
}
