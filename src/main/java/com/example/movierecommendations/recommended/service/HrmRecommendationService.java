//package com.example.movierecommendations.recommended.service;
//
//import com.example.movierecommendations.recommended.domain.MovieRecommendation;
//import com.example.movierecommendations.recommended.dto.MovieRecommendationDTO;
//import com.example.movierecommendations.recommended.domain.HrmRecommendation;
//import com.example.movierecommendations.recommended.repository.HrmRecommendationRepository;
//import com.example.movierecommendations.recommended.repository.MovieRecommendationRepository;
//import com.example.movierecommendations.member.domain.Member;
//import com.example.movierecommendations.member.repository.MemberRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class HrmRecommendationService {
//
//    private static final Logger logger = LoggerFactory.getLogger(HrmRecommendationService.class);
//
//    private final HrmRecommendationRepository hrmRecommendationRepository;
//    private final MovieRecommendationRepository movieRecommendationRepository;
//    private final MemberRepository memberRepository;
//
//    @Autowired
//    public HrmRecommendationService(HrmRecommendationRepository hrmRecommendationRepository,
//                                    MovieRecommendationRepository movieRecommendationRepository,
//                                    MemberRepository memberRepository) {
//        this.hrmRecommendationRepository = hrmRecommendationRepository;
//        this.movieRecommendationRepository = movieRecommendationRepository;
//        this.memberRepository = memberRepository;
//    }
//
//    // 특정 회원에 대한 추천 리스트 조회 (통합된 추천 리스트)
//    public HrmRecommendation getRecommendationByMemberId(Long memberId) {
//        logger.info("Attempting to fetch recommendations for memberId: {}", memberId);
//
//        HrmRecommendation hrmRecommendation = hrmRecommendationRepository.findByMemberMemberId(memberId)
//                .orElseThrow(() -> {
//                    logger.error("Recommendation not found for memberId: {}", memberId);
//                    return new RuntimeException("Recommendation not found for memberId: " + memberId);
//                });
//
//        logger.info("Recommendation found: {}", hrmRecommendation);
//        return hrmRecommendation;
//    }
//
//    public void saveRecommendationForMember(Long memberId, List<MovieRecommendationDTO> recommendedMovies) {
//        logger.info("Saving recommendation for memberId: {}", memberId);
//
//        // 회원 조회
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> {
//                    logger.error("Member not found for memberId: {}", memberId);
//                    return new RuntimeException("Member not found");
//                });
//        logger.info("Member found: {}", member);
//
//        // MovieRecommendation 엔티티로 변환 후 저장
//        List<MovieRecommendation> movieRecommendations = recommendedMovies.stream()
//                .map(dto -> {
//                    MovieRecommendation movieRecommendation = MovieRecommendation.builder()
//                            .movieCd(dto.getMovieCd())
//                            .movieNm(dto.getMovieNm())
//                            .build();
//                    logger.debug("Mapped MovieRecommendation: {}", movieRecommendation);  // 디버그 로그 추가
//                    return movieRecommendation;
//                })
//                .collect(Collectors.toList());
//
//        logger.info("Saving movie recommendations: {}", movieRecommendations);
//        movieRecommendationRepository.saveAll(movieRecommendations);
//        logger.info("Movie recommendations saved successfully.");
//
//        // MovieRecommendation 엔티티에서 영화 이름만 추출하여 List<String>으로 변환
//        List<String> movieNames = movieRecommendations.stream()
//                .map(movieRecommendation -> {
//                    String movieNm = movieRecommendation.getMovieNm();
//                    if (movieNm == null) {
//                        logger.warn("Found null movieNm for movieCd: {}", movieRecommendation.getMovieCd());
//                        return "";  // 또는 "Unknown" 등으로 대체 가능
//                    }
//                    return movieNm;
//                })
//                .collect(Collectors.toList());  // List<String>으로 변환
//
//        logger.debug("Extracted movie names: {}", movieNames);
//
//        // HrmRecommendation 엔티티 생성 및 저장 (통합된 추천 리스트)
//        HrmRecommendation hrmRecommendation = HrmRecommendation.builder()
//                .member(member)
//                .recommendations(movieRecommendations)  // MovieRecommendation 객체들을 직접 넣음
//                .build();
//
//        logger.info("Saving HrmRecommendation: {}", hrmRecommendation);
//        hrmRecommendationRepository.save(hrmRecommendation);
//        logger.info("HrmRecommendation saved successfully.");
//
//        // 추가: movieRecommendations와 HrmRecommendation 관계 설정
//        movieRecommendations.forEach(movieRecommendation -> {
//            movieRecommendation.setHrmRecommendation(hrmRecommendation);  // HrmRecommendation과 연결
//        });
//        movieRecommendationRepository.saveAll(movieRecommendations);  // 관계 업데이트
//        logger.info("Updated movie recommendations with HrmRecommendation.");
//    }
//
//
//    // DTO에서 받은 추천 영화 목록을 저장하는 메서드
//    public void saveRecommendation(MovieRecommendationDTO movieRecommendationDTO) {
//        logger.info("Saving single recommendation for movieCd: {}", movieRecommendationDTO.getMovieCd());
//
//        // DTO에서 MovieRecommendation 엔티티로 변환 후 저장
//        MovieRecommendation movieRecommendation = MovieRecommendation.builder()
//                .movieCd(movieRecommendationDTO.getMovieCd())
//                .movieNm(movieRecommendationDTO.getMovieNm())
//                .build();
//
//        logger.debug("Mapped MovieRecommendation: {}", movieRecommendation);
//        movieRecommendationRepository.save(movieRecommendation);
//        logger.info("MovieRecommendation saved successfully.");
//    }
//}



package com.example.movierecommendations.recommended.service;

import com.example.movierecommendations.recommended.domain.MovieRecommendation;
import com.example.movierecommendations.recommended.dto.HrmRecommendationDTO;
import com.example.movierecommendations.recommended.dto.MovieRecommendationDTO;
import com.example.movierecommendations.recommended.domain.HrmRecommendation;
import com.example.movierecommendations.recommended.repository.HrmRecommendationRepository;
import com.example.movierecommendations.recommended.repository.MovieRecommendationRepository;
import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HrmRecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(HrmRecommendationService.class);

    private final HrmRecommendationRepository hrmRecommendationRepository;
    private final MovieRecommendationRepository movieRecommendationRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public HrmRecommendationService(HrmRecommendationRepository hrmRecommendationRepository,
                                    MovieRecommendationRepository movieRecommendationRepository,
                                    MemberRepository memberRepository) {
        this.hrmRecommendationRepository = hrmRecommendationRepository;
        this.movieRecommendationRepository = movieRecommendationRepository;
        this.memberRepository = memberRepository;
    }

    // 특정 회원에 대한 추천 리스트 조회 (통합된 추천 리스트)
//    public HrmRecommendation getRecommendationByMemberId(Long memberId) {
//        logger.info("Attempting to fetch recommendations for memberId: {}", memberId);
//
//        HrmRecommendation hrmRecommendation = hrmRecommendationRepository.findByMemberMemberId(memberId)
//                .orElseThrow(() -> {
//                    logger.error("Recommendation not found for memberId: {}", memberId);
//                    return new RuntimeException("Recommendation not found for memberId: " + memberId);
//                });
//
//        logger.info("Recommendation found: {}", hrmRecommendation);
//        return hrmRecommendation;
//    }

    // 특정 회원에 대한 추천 리스트 조회
    public HrmRecommendation getRecommendationByMemberId(Long memberId) {

        return hrmRecommendationRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new HrmRecommendationNotFoundException("Recommendation not found for memberId: " + memberId));
    }




    //    @Transactional
//    public void saveRecommendationForMember(Long memberId, List<MovieRecommendationDTO> recommendedMovies) {
//        logger.info("Saving recommendation for memberId: {}", memberId);
//
//        // 회원 조회
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> {
//                    logger.error("Member not found for memberId: {}", memberId);
//                    return new RuntimeException("Member not found");
//                });
//        logger.info("Member found: {}", member);
//
//        // MovieRecommendation 엔티티로 변환 후 저장
//        List<MovieRecommendation> movieRecommendations = recommendedMovies.stream()
//                .map(dto -> {
//                    MovieRecommendation movieRecommendation = MovieRecommendation.builder()
//                            .movieCd(dto.getMovieCd())
//                            .movieNm(dto.getMovieNm())
//                            .build();
//                    logger.debug("Mapped MovieRecommendation: {}", movieRecommendation);  // 디버그 로그 추가
//                    return movieRecommendation;
//                })
//                .collect(Collectors.toList());
//
//        logger.info("Saving movie recommendations: {}", movieRecommendations);
//        movieRecommendationRepository.saveAll(movieRecommendations);
//        logger.info("Movie recommendations saved successfully.");
//
//        // MovieRecommendation 엔티티에서 영화 이름만 추출하여 List<String>으로 변환
//        List<String> movieNames = movieRecommendations.stream()
//                .map(movieRecommendation -> {
//                    String movieNm = movieRecommendation.getMovieNm();
//                    if (movieNm == null) {
//                        logger.warn("Found null movieNm for movieCd: {}", movieRecommendation.getMovieCd());
//                        return "";  // 또는 "Unknown" 등으로 대체 가능
//                    }
//                    return movieNm;
//                })
//                .collect(Collectors.toList());  // List<String>으로 변환
//
//        logger.debug("Extracted movie names: {}", movieNames);
//
//        // HrmRecommendation 엔티티 생성 및 저장 (통합된 추천 리스트)
//        HrmRecommendation hrmRecommendation = HrmRecommendation.builder()
//                .member(member)
//                .recommendations(movieRecommendations)  // MovieRecommendation 객체들을 직접 넣음
//                .build();
//
//        logger.info("Saving HrmRecommendation: {}", hrmRecommendation);
//        hrmRecommendationRepository.save(hrmRecommendation);
//        logger.info("HrmRecommendation saved successfully.");
//
//        // 추가: movieRecommendations와 HrmRecommendation 관계 설정
//        movieRecommendations.forEach(movieRecommendation -> {
//            movieRecommendation.setHrmRecommendation(hrmRecommendation);  // HrmRecommendation과 연결
//        });
//        movieRecommendationRepository.saveAll(movieRecommendations);  // 관계 업데이트
//        logger.info("Updated movie recommendations with HrmRecommendation.");
//    }
@Transactional
public void saveRecommendationForMember(Long memberId, List<MovieRecommendationDTO> recommendedMovies) {
    logger.info("Saving recommendation for memberId: {}", memberId);

    // 회원 조회
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> {
                logger.error("Member not found for memberId: {}", memberId);
                return new RuntimeException("Member not found");
            });
    logger.info("Member found: {}", member);

    // MovieRecommendation 엔티티로 변환 후 저장
    List<MovieRecommendation> movieRecommendations = recommendedMovies.stream()
            .map(dto -> {
                MovieRecommendation movieRecommendation = MovieRecommendation.builder()
                        .movieCd(dto.getMovieCd())
                        .movieNm(dto.getMovieNm())
                        .build();
                logger.debug("Mapped MovieRecommendation: {}", movieRecommendation);
                return movieRecommendation;
            })
            .collect(Collectors.toList());

    // HrmRecommendation 엔티티 생성 및 저장 (통합된 추천 리스트)
    // 먼저, HrmRecommendation 객체를 생성해야 합니다.
    HrmRecommendation hrmRecommendation = HrmRecommendation.builder()
            .member(member)  // 회원 정보를 설정
            .recommendations(movieRecommendations)  // 추천 영화 리스트를 설정
            .build();

    // HrmRecommendation 객체를 먼저 저장
    logger.info("Saving HrmRecommendation: {}", hrmRecommendation);
    hrmRecommendationRepository.save(hrmRecommendation);
    logger.info("HrmRecommendation saved successfully.");

    // movieRecommendations와 HrmRecommendation 관계 설정
    movieRecommendations.forEach(movieRecommendation -> {
        movieRecommendation.setHrmRecommendation(hrmRecommendation);  // HrmRecommendation과 연결
    });

    // movieRecommendations 저장 (HrmRecommendation과 관계가 설정된 상태)
    movieRecommendationRepository.saveAll(movieRecommendations);
    logger.info("Updated movie recommendations with HrmRecommendation.");
}


}

