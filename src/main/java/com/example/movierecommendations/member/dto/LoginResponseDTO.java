package com.example.movierecommendations.member.dto;

import com.example.movierecommendations.member.vo.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private Long memberId;
    private int statusCode;
    private String message;
    private String accessToken;
    private String refreshToken;
    private Role role;
}