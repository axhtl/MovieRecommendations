package com.example.movierecommendations.recommended.domain;

import com.example.movierecommendations.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "recommendation")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 추천 리스트의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 추천 리스트가 속한 회원

    @ElementCollection
    @CollectionTable(name = "movie_recommendation", joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "movie_info")  // 영화 제목과 추천 이유를 담은 문자열로 저장
    private List<String> recommendedMovies;  // 추천 영화 리스트 (영화 제목과 추천 이유 포함)

    public void addMovieRecommendation(String movieInfo) {
        this.recommendedMovies.add(movieInfo);  // 새로운 영화 추천 추가
    }

    public void removeMovieRecommendation(String movieInfo) {
        this.recommendedMovies.remove(movieInfo);  // 영화 추천 삭제
    }
}
