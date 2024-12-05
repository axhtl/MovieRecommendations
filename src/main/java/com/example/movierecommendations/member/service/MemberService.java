package com.example.movierecommendations.member.service;

import com.example.movierecommendations.member.domain.*;
import com.example.movierecommendations.member.dto.*;
import com.example.movierecommendations.member.repository.*;
import com.example.movierecommendations.member.vo.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SurveyRepository surveyRepository;
    private final ReviewRepository reviewRepository;
    private final MovieInfoRepository movieInfoRepository;
    private final MovieActorRepository movieActorRepository;
    private final MovieDirectorRepository movieDirectorRepository;
    private final MovieGenreRepository movieGenreRepository;

    // 특정 사용자의 모든 정보를 가져오는 서비스 메서드
    public UserMovieInfoResponse getUserMovieInfo(Long memberId) {
        // 사용자 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 설문조사 정보 조회
        Survey survey = surveyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        // 사용자 ID로 모든 리뷰 조회
        List<Review> reviews = reviewRepository.findByMember_MemberId(memberId);

        // 선호 장르 및 선호 배우 정보 조회
        List<String> preferredGenres = member.getPreferredGenres()
                .stream()
                .map(PreferredGenre::getGenre)
                .collect(Collectors.toList());

        List<String> preferredActors = member.getPreferredActor()
                .stream()
                .map(PreferredActor::getActor)
                .collect(Collectors.toList());

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
                .preferredGenres(preferredGenres) // 선호 장르
                .preferredActors(preferredActors) // 선호 배우
                .reviewInfos(reviewInfos) // 리뷰 정보
                .build();
    }

    // 전체 사용자 정보를 가져오는 서비스 메서드
    public List<UserMovieInfoResponse> getAllUserMovieInfo() {
        // 모든 사용자 조회
        List<Member> members = memberRepository.findAll();

        // 각 사용자에 대한 정보 조회
        return members.stream().map(member -> {
            // 설문조사 정보 조회
            Survey survey = surveyRepository.findByMemberId(member.getMemberId())
                    .orElseThrow(() -> new RuntimeException("Survey not found"));

            // 사용자 ID로 모든 리뷰 조회
            List<Review> reviews = reviewRepository.findByMember_MemberId(member.getMemberId());

            // 리뷰별 영화 및 관련 정보 조회
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

            // 선호 장르 및 선호 배우 정보 조회
            List<String> preferredGenres = member.getPreferredGenres()
                    .stream()
                    .map(PreferredGenre::getGenre)
                    .collect(Collectors.toList());

            List<String> preferredActors = member.getPreferredActor()
                    .stream()
                    .map(PreferredActor::getActor)
                    .collect(Collectors.toList());

            // 사용자별 정보 응답 DTO 생성
            return UserMovieInfoResponse.builder()
                    .member(member)
                    .survey(survey)
                    .reviewInfos(reviewInfos)
                    .preferredGenres(preferredGenres)
                    .preferredActors(preferredActors)
                    .build();
        }).collect(Collectors.toList());
    }

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
