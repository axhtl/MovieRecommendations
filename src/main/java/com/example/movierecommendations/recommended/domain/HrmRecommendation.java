package com.example.movierecommendations.recommended.domain;

import com.example.movierecommendations.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @OneToOne(fetch = FetchType.LAZY)  // @OneToOne 관계로 회원과 연결
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "member_id")
    private Member member;  // 추천 리스트가 속한 회원

    // MovieRecommendation 엔티티와 OneToMany 관계 설정
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hrm_recommendation_id")  // 이 칼럼은 MovieRecommendation에 추가됨
    private List<MovieRecommendation> recommendations;  // 추천 영화 리스트

    public Long getMemberId() {
        return this.member != null ? this.member.getMemberId() : null;
    }
}
