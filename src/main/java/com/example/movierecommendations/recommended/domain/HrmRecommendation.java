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
@Entity(name = "hrm_recommendation")
public class HrmRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)  // @OneToOne 관계로 변경
    @JoinColumn(name = "member_id")
    private Member member;  // 추천 리스트가 속한 회원

    // hybridRecommendations 반환하는 메서드
    @Getter
    @ElementCollection
    @CollectionTable(name = "hybrid_recommendation", joinColumns = @JoinColumn(name = "hrm_recommendation_id"))
    @Column(name = "movie_info")
    private List<String> hybridRecommendations;

    // similarMovies 반환하는 메서드
    @Getter
    @ElementCollection
    @CollectionTable(name = "similar_movies", joinColumns = @JoinColumn(name = "hrm_recommendation_id"))
    @Column(name = "movie_info")
    private List<String> similarMovies;

    public Long getMemberId() {
        return this.member != null ? this.member.getMemberId() : null;
    }

}

