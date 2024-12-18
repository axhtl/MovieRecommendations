package com.example.movierecommendations.member.service;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.dto.admin.CreateAdminRequestDTO;
import com.example.movierecommendations.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long saveAdmin(CreateAdminRequestDTO createAdminRequest) {
        // 입력값 검증
        validateCreateAdminRequest(createAdminRequest);

        // 아이디 중복 체크
        if (memberRepository.findByMembername(createAdminRequest.getMembername()).isPresent()) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }

        // 닉네임 중복 체크
        if (memberRepository.findByNickname(createAdminRequest.getNickname()).isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임입니다.");
        }

        // 비밀번호 암호화 후 DB에 회원정보 저장
        String encodedPassword = passwordEncoder.encode(createAdminRequest.getPassword());
        Member member = createAdminRequest.toMember(encodedPassword);
        memberRepository.save(member);

        return member.getMemberId();
    }

    private void validateCreateAdminRequest(CreateAdminRequestDTO request) {
        if (request.getMembername() == null || request.getMembername().length() < 4) {
            throw new IllegalArgumentException("아이디는 최소 4자 이상이어야 합니다.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        if (request.getNickname() == null || request.getNickname().length() < 2) {
            throw new IllegalArgumentException("닉네임은 최소 2자 이상이어야 합니다.");
        }
    }
}
