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
@Entity(name = "preferred_actor")
public class PreferredActor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferredActorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    private String actor;

    public void updatePreferredActor(String actor) {
        this.actor=actor;
    }
}
