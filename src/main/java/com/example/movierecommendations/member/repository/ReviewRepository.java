package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMember_MemberId(Long memberId);
}
