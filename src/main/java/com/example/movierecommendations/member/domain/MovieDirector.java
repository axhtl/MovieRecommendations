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
@Entity(name = "movie_director")
public class MovieDirector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieDirectorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movieInfoId", nullable = false)
    private MovieInfo movieInfo;

    private String director;

    public void updateMovieDirector(String director) {
        this.director=director;
    }
}
