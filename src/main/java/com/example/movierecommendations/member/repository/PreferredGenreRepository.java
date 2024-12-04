package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.domain.PreferredGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferredGenreRepository extends JpaRepository<PreferredGenre, Long> {
    List<PreferredGenre> findByMember_MemberId(Long memberId);
    void deleteByPreferredGenreId(Long preferredGenreId);
}
