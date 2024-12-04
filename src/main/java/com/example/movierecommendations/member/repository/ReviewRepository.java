package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
