package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.domain.PreferredActor;
import com.example.movierecommendations.member.domain.PreferredGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferredActorRepository extends JpaRepository<PreferredActor, Long> {
    List<PreferredActor> findByMember_MemberId(Long memberId);
    void deleteByPreferredActorId(Long preferredActorId);
}
