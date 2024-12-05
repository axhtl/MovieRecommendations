package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.LoginResponseDTO;
import com.example.movierecommendations.member.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    // 리프레쉬 토큰으로 새로운 액세스 토큰 발급 요청
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDTO> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        // AuthenticationService에서 refresh Token으로 새로운 Access Token 발급
        LoginResponseDTO responseDTO = authenticationService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(responseDTO);
    }
}