package com.example.movierecommendations.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "movie_info")
public class MovieInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieInfoId;
    private Long reviewId;
    private int movieId;
    private String title;
    private String originalTitle;
    private String runtime;
    private String releaseDate;
    private String originCountry;
    @JsonIgnore
    @OneToMany(mappedBy = "movieInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MovieActor> movieActors = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "movieInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MovieGenre> movieGenres = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "movieInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MovieDirector> movieDirectors = new ArrayList<>();
}
