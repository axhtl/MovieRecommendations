package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.dto.admin.CreateAdminRequestDTO;
import com.example.movierecommendations.member.service.AdminService;
import com.example.movierecommendations.member.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // 관리자 회원가입
    @PostMapping("/admin/signup")
    public ResponseEntity<SaveResponseDTO> signup(@Validated @RequestBody CreateAdminRequestDTO createAdminRequest) {
        Long memberId = adminService.saveAdmin(createAdminRequest);
        return ResponseEntity.ok(new SaveResponseDTO(
                memberId, HttpStatus.OK.value(), "회원가입이 정상적으로 진행되었습니다."
        ));
    }
}
