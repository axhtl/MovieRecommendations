package com.example.movierecommendations.member.service;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.dto.CreateMemberRequestDTO;
import com.example.movierecommendations.member.dto.NicknameUpdateRequestDTO;
import com.example.movierecommendations.member.dto.PasswordUpdateRequestDTO;
import com.example.movierecommendations.member.repository.MemberRepository;
import com.example.movierecommendations.member.vo.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public Long saveMember(CreateMemberRequestDTO createMemberRequest) {
        // 입력값 검증
        validateCreateMemberRequest(createMemberRequest);

        // 아이디 중복 체크
        if (memberRepository.findByMembername(createMemberRequest.getMembername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 닉네임 중복 체크
        if (memberRepository.findByNickname(createMemberRequest.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화 후 DB에 회원정보 저장
        String encodedPassword = passwordEncoder.encode(createMemberRequest.getPassword());
        Member member = createMemberRequest.toMember(encodedPassword);
        memberRepository.save(member);

        return member.getMemberId();
    }

    @Transactional
    // 사용자 이름으로 memberId 반환
    public Long getMemberIdByMembername(String membername) {
        Member member = memberRepository.findByMembername(membername)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        return member.getMemberId(); // memberId 반환
    }

    @Transactional
    public void updatePassword(Long memberId, PasswordUpdateRequestDTO passwordUpdateRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 암호화 후 업데이트
        member.updatePassword(passwordEncoder.encode(passwordUpdateRequest.getPassword()));
    }

    @Transactional
    public void updateNickname(Long memberId, NicknameUpdateRequestDTO nicknameUpdateRequest) {
        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(nicknameUpdateRequest.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 닉네임 업데이트
        member.updateNickname(nicknameUpdateRequest.getNickname());
    }

    @Transactional
    public void withdrawMember(Long memberId) {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // refreshToken을 NULL로 설정하고, 회원 상태를 WITHDRAWN으로 변경
        member.updateRefreshToken(null); // refreshToken을 null로 설정
        member.updateMemberStatus(MemberStatus.WITHDRAWN); // 회원 상태를 WITHDRAWN으로 변경
        member.updateDeletedAt(LocalDateTime.now());
    }

    private void validateCreateMemberRequest(CreateMemberRequestDTO request) {
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
