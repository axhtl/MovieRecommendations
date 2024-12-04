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
@Entity(name = "preferred_genre")
public class PreferredGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferredGenreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    private String genre;

    public void updatePreferredGenre(String genre) {
        this.genre=genre;
    }
}