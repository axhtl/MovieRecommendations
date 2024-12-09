package com.example.movierecommendations.member.dto;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.vo.MemberStatus;
import com.example.movierecommendations.member.vo.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMemberRequestDTO {
    private Long memberId;
    private String membername;
    private String password;
    private String nickname;

    public Member toMember(String encodedPassword) {
        return Member.builder()
                .membername(membername)
                .password(encodedPassword)
                .nickname(nickname)
                .role(Role.USER)
                .memberStatus(MemberStatus.ACTIVE)
//                .createdAt(LocalDateTime.now())
                .build();
    }
}
