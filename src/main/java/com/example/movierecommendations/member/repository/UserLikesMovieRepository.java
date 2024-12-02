package com.example.movierecommendations.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLikesMovieRepository extends JpaRepository<UserLikesMovie, Long> {

    // 특정 사용자가 좋아한 영화를 가져오는 메소드
    List<UserLikesMovie> findByUserId(Long userId);
}
