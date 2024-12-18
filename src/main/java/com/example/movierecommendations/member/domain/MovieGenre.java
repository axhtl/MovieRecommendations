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
@Entity(name = "movie_genre")
public class MovieGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieGenreId;

    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movieInfoId", nullable = false)
    private MovieInfo movieInfo;

    private String genre;

    public void updateMovieGenre(String genre) {
        this.genre=genre;
    }
}
