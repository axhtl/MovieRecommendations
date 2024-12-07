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
@Entity(name = "llm_recommendation")
public class LlmRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // ManyToOne -> OneToOne으로 변경
    @JoinColumn(name = "member_id") // 연결할 컬럼명
    private Member member;  // 추천 리스트가 속한 회원

    @ElementCollection
    @CollectionTable(name = "llm_recommendations", joinColumns = @JoinColumn(name = "llm_recommendation_id"))
    @Column(name = "movie_info")
    private List<String> llmRecommendations;  // LLM 추천 영화 리스트

    // memberId를 반환하는 메서드 추가
    public Long getMemberId() {
        return this.member != null ? this.member.getMemberId() : null;
    }
}
