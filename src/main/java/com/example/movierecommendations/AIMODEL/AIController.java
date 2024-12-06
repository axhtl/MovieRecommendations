package com.example.movierecommendations.AIMODEL;

import com.example.movierecommendations.member.dto.UserMovieInfoResponse;
import com.example.movierecommendations.member.service.MemberService;
import com.example.movierecommendations.member.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/ai")  // 엔드포인트 URL
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final AIModelService aiModelService;  // AIModelService를 주입
    private final MemberService memberService;

    // AIModelService 및 MemberService 주입
    @Autowired
    public AIController(AIModelService aiModelService, MemberService memberService) {
        this.aiModelService = aiModelService;
        this.memberService = memberService;
    }

    // 기존 HRM 모델을 호출하는 메서드
    @PostMapping("/predict/{memberId}")
    public CompletableFuture<ResponseEntity<List<String>>> HRMpredict(@PathVariable("memberId") Long memberId) {
        // 사용자별 영화 정보 가져오기
        UserMovieInfoResponse inputData = memberService.getUserMovieInfo(memberId);

        // ObjectMapper를 사용하여 객체를 JSON 형식으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonInputData = objectMapper.writeValueAsString(inputData);
            logger.info("영화조회정보: {}", jsonInputData); // JSON 형식으로 로그 출력
//            aiModelService.callHRMModel(jsonInputData, memberId);
        } catch (Exception e) {
            logger.error("Error converting inputData to JSON", e);
        }

//        // HRM 모델 호출 (AIModelService의 callHRMModel 사용)
//        aiModelService.callHRMModel(jsonInputData, memberId);

        // 즉시 응답을 반환 (비동기 방식이므로 결과는 나중에 처리됨)
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(List.of("HRM recommendation initiated")));
    }

    // LLM 모델을 호출하는 메서드
    @PostMapping("/predict2/{memberId}")
    public CompletableFuture<ResponseEntity<List<String>>> LLMpredict(@PathVariable Long memberId) {
        // 사용자별 영화 정보 가져오기
        UserMovieInfoResponse inputData = memberService.getUserMovieInfo(memberId);

        // LLM 모델 호출 (AIModelService의 callLLMModel 사용)
        aiModelService.callLLMModel(inputData, memberId);

        // 즉시 응답을 반환 (비동기 방식이므로 결과는 나중에 처리됨)
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(List.of("LLM recommendation initiated")));
    }
}