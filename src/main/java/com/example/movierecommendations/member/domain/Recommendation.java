package com.example.movierecommendations.member.domain;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 회원과의 관계

    @ElementCollection
    @CollectionTable(name = "recommendation_movies", joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "movie_name")
    private List<String> movieRecommendations;  // 추천된 영화 목록

}
