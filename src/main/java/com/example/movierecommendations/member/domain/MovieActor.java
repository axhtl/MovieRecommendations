package com.example.movierecommendations.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "movie_actor")
public class MovieActor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieActorId;

    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movieInfoId", nullable = false)
    private MovieInfo movieInfo;

    private String actor;

    public void updateMovieActor(String actor) {
        this.actor=actor;
    }
}
