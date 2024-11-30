package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.PreferredActor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferredActorRepository extends JpaRepository<PreferredActor, Long> {
    List<PreferredActor> findByMember_MemberId(Long memberId);
}
