package com.example.movierecommendations.recommended.controller;

import com.example.movierecommendations.recommended.domain.HrmRecommendation;
import com.example.movierecommendations.recommended.dto.HrmRecommendationDTO;
import com.example.movierecommendations.recommended.dto.MovieRecommendationDTO;
import com.example.movierecommendations.recommended.service.HrmRecommendationNotFoundException;
import com.example.movierecommendations.recommended.service.HrmRecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
public class HrmRecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(HrmRecommendationController.class);
    private final HrmRecommendationService hrmRecommendationService;

    @Autowired
    public HrmRecommendationController(HrmRecommendationService hrmRecommendationService) {
        this.hrmRecommendationService = hrmRecommendationService;
    }

    // 특정 회원의 추천 리스트를 조회하는 엔드포인트
    @GetMapping(value = "/recommend/{memberId}", produces = "application/json")
    public ResponseEntity<HrmRecommendation> getRecommendationByMemberId(@PathVariable Long memberId) {
        logger.info("Received request to get recommendations for memberId: {}", memberId);

        try {
            // 서비스 계층에서 추천 리스트를 조회
            HrmRecommendation hrmRecommendation = hrmRecommendationService.getRecommendationByMemberId(memberId);
            logger.debug("Recommendation data retrieved: {}", hrmRecommendation);

            // 조회한 추천 데이터를 그대로 반환
            return ResponseEntity.ok(hrmRecommendation);
        } catch (RuntimeException e) {
            logger.error("Error occurred while retrieving recommendations for memberId: {}", memberId, e);
            return ResponseEntity.status(404).body(null);
        }
    }

//    @GetMapping(value = "/recommend/{memberId}", produces = "application/json")
//    public ResponseEntity<HrmRecommendationDTO> getRecommendationByMemberId(@PathVariable Long memberId) {
//        logger.info("Received request to get recommendations for memberId: {}", memberId);
//
//        try {
//            // 서비스 계층에서 추천 리스트를 조회
//            HrmRecommendation hrmRecommendation = hrmRecommendationService.getRecommendationByMemberId(memberId);
//            logger.debug("Recommendation data retrieved: {}", hrmRecommendation);
//
//            // HrmRecommendation 엔티티를 DTO로 변환
//            HrmRecommendationDTO hrmRecommendationDTO = convertToDTO(hrmRecommendation);
//
//            // 조회한 추천 데이터를 DTO로 반환
//            return ResponseEntity.ok(hrmRecommendationDTO);
//        } catch (HrmRecommendationNotFoundException e) {
//            logger.error("Recommendation not found for memberId: {}", memberId, e);
//            return ResponseEntity.status(404).body(null);  // 404 - Not Found
//        } catch (Exception e) {
//            logger.error("Error occurred while retrieving recommendations for memberId: {}", memberId, e);
//            return ResponseEntity.status(500).body(null);  // 500 - Internal Server Error
//        }
//    }
//
//    private HrmRecommendationDTO convertToDTO(HrmRecommendation hrmRecommendation) {
//        // HrmRecommendationDTO 객체 생성
//        HrmRecommendationDTO dto = new HrmRecommendationDTO();
//
//        // 필요한 데이터만 DTO에 설정
//        dto.setMemberId(hrmRecommendation.getMember().getMemberId());
//
//        // 추천 리스트를 DTO로 변환
//        List<MovieRecommendationDTO> movieRecommendations = hrmRecommendation.getRecommendations().stream()
//                .map(movie -> new MovieRecommendationDTO(
//                        movie.getMovieCd() != null ? movie.getMovieCd() : "N/A",  // null 처리
//                        movie.getMovieNm() != null ? movie.getMovieNm() : "Unknown"  // null 처리
//                ))
//                .collect(Collectors.toList());
//
//        dto.setRecommendations(movieRecommendations);
//
//        return dto;
//    }


    // 회원의 추천 리스트를 저장하는 엔드포인트
    @PostMapping("/save/{memberId}")
    public ResponseEntity<String> saveRecommendation(@PathVariable Long memberId, @RequestBody List<MovieRecommendationDTO> movieRecommendationDTOs) {
        logger.info("Received request to save recommendation for memberId {}: {}", memberId, movieRecommendationDTOs);

        try {
            // 여러 영화 추천을 저장하는 서비스 호출
            hrmRecommendationService.saveRecommendationForMember(memberId, movieRecommendationDTOs);  // 요청 받은 memberId 사용
            logger.info("Recommendation saved successfully");

            return ResponseEntity.ok("Recommendation saved successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while saving recommendation for memberId {}: {}", memberId, movieRecommendationDTOs, e);
            return ResponseEntity.status(500).body("Error saving recommendation.");
        }
    }
}
