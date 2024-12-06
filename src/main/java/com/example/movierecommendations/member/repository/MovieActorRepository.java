package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.MovieActor;
import com.example.movierecommendations.member.domain.MovieInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieActorRepository extends JpaRepository<MovieActor, Long> {
    List<MovieActor> findByMovieInfo(MovieInfo movieInfo);
    void deleteByMovieInfo_MovieInfoId(Long movieInfoId);  // MovieInfo의 movieInfoId를 기준으로 삭제
}
