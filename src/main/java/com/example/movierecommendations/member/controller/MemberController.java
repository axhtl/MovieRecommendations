package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.CreateMemberRequestDTO;
import com.example.movierecommendations.member.dto.LoginRequestDTO;
import com.example.movierecommendations.member.dto.LoginResponseDTO;
import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.service.AuthenticationService;
import com.example.movierecommendations.member.service.MemberService;
import com.example.movierecommendations.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationService authenticationService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SaveResponseDTO> signup(@Validated @RequestBody CreateMemberRequestDTO createMemberRequest) {
        Long memberId = memberService.saveMember(createMemberRequest);
        return ResponseEntity.ok(new SaveResponseDTO(
                memberId, HttpStatus.OK.value(), "회원가입이 정상적으로 진행되었습니다."
        ));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = authenticationService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String token
    ) {
        // Bearer 부분 제거
        String jwtToken = token.substring(7);

        // Access Token 검증
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        // Access Token에서 사용자 정보 추출 (membername)
        String membername = jwtTokenProvider.getMembername(jwtToken);

        // 사용자 정보에서 memberId 조회 (로그아웃 대상 사용자)
        Long memberId = memberService.getMemberIdByMembername(membername);

        // DB에서 해당 사용자의 Refresh Token 삭제
        authenticationService.deleteRefreshToken(memberId);

        // SecurityContextHolder를 명시적으로 클리어
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
    }
}
