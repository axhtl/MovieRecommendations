package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.CreateMemberRequestDTO;
import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<SaveResponseDTO> signup(@Validated @RequestBody CreateMemberRequestDTO createMemberRequest) {
        Long memberId = memberService.saveMember(createMemberRequest);
        return ResponseEntity.ok(new SaveResponseDTO(
                memberId, HttpStatus.OK.value(), "회원가입이 정상적으로 진행되었습니다."
        ));
    }
}
