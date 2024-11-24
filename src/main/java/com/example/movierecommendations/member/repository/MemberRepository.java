package com.example.movierecommendations.member.repository;

import com.example.movierecommendations.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMembername(String membername);
    Optional<Member> findByNickname(String nickname);
    boolean existsByNickname(String nickname); // 닉네임 중복 체크
}
