package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.domain.*;
import com.example.movierecommendations.member.dto.*;
import com.example.movierecommendations.member.repository.*;
import com.example.movierecommendations.member.service.AuthenticationService;
import com.example.movierecommendations.member.service.MemberService;
import com.example.movierecommendations.security.JwtTokenProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationService authenticationService;
    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final ReviewRepository reviewRepository;
    private final MovieInfoRepository movieInfoRepository;
    private final MovieActorRepository movieActorRepository;
    private final MovieDirectorRepository movieDirectorRepository;
    private final MovieGenreRepository movieGenreRepository;

    // 사용자별 리뷰, 영화 정보, 설문조사 정보 조회 API
    @GetMapping(value="/user/{memberId}", produces = "application/json")
    public UserMovieInfoResponse getUserMovieInfo(@PathVariable Long memberId) {
        // 사용자 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 설문조사 정보 조회
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        // 사용자 ID로 모든 리뷰 조회
        List<Review> reviews = reviewRepository.findByMember_MemberId(memberId);

        // 리뷰별 관련된 영화 정보 및 그에 대한 배우, 감독, 장르 정보 조회
        List<UserMovieInfoResponse.ReviewInfo> reviewInfos = reviews.stream().map(review -> {
            MovieInfo movieInfo = movieInfoRepository.findByReviewId(review.getReviewId());
            List<MovieActor> actors = movieActorRepository.findByMovieInfo(movieInfo);
            List<MovieDirector> directors = movieDirectorRepository.findByMovieInfo(movieInfo);
            List<MovieGenre> genres = movieGenreRepository.findByMovieInfo(movieInfo);

            return UserMovieInfoResponse.ReviewInfo.builder()
                    .reviewId(review.getReviewId())
                    .movieInfo(movieInfo)
                    .actors(actors.stream().map(MovieActor::getActor).collect(Collectors.toList()))
                    .directors(directors.stream().map(MovieDirector::getDirector).collect(Collectors.toList()))
                    .genres(genres.stream().map(MovieGenre::getGenre).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());

        // 응답 DTO 반환
        return UserMovieInfoResponse.builder()
                .member(member)
                .survey(survey)
                .reviewInfos(reviewInfos)
                .build();
    }

    // 영화 및 관련 정보 조회 응답 DTO
    @Getter
    @Builder
    public static class UserMovieInfoResponse {
        private Member member;  // 사용자 정보 추가
        private Survey survey;  // 설문조사 정보 추가
        private List<ReviewInfo> reviewInfos;  // 리뷰 정보 리스트 추가

        @Builder
        @Getter
        public static class ReviewInfo {
            private Long reviewId;
            private MovieInfo movieInfo;
            private List<String> actors;
            private List<String> directors;
            private List<String> genres;
        }
    }

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

    // 비밀번호 수정
    @PutMapping("/password/{memberId}")
    public ResponseEntity<String> updatePassword(
            @PathVariable Long memberId,
            @RequestBody PasswordUpdateRequestDTO passwordUpdateRequest) {

        memberService.updatePassword(memberId, passwordUpdateRequest);
        return ResponseEntity.ok("비밀번호가 성공적으로 수정되었습니다.");
    }

    // 닉네임 수정
    @PutMapping("/nickname/{memberId}")
    public ResponseEntity<String> updateNickname(
            @PathVariable Long memberId,
            @RequestBody NicknameUpdateRequestDTO nicknameUpdateRequest) {

        memberService.updateNickname(memberId, nicknameUpdateRequest);
        return ResponseEntity.ok("닉네임이 성공적으로 수정되었습니다.");
    }

    // 회원 탈퇴
    @PutMapping("/withdraw/{memberId}")
    public ResponseEntity<String> withdrawMember(@PathVariable Long memberId) {
        memberService.withdrawMember(memberId);
        return ResponseEntity.ok("회원탈퇴가 성공적으로 처리되었습니다.");
    }
}
