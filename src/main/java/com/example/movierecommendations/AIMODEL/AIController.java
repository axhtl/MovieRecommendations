package com.example.movierecommendations.AIMODEL;

import com.example.movierecommendations.member.dto.UserMovieInfoResponse;
import com.example.movierecommendations.member.service.MemberService;
import com.example.movierecommendations.member.service.ReviewService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
        try {
            // 예시 데이터 (실제로는 DB에서 조회되는 데이터)
            UserMovieInfoResponse inputData = memberService.getUserMovieInfo(memberId);

            // Jackson의 ObjectMapper로 JSON 형식으로 변환
            ObjectMapper mapper = new ObjectMapper();

            // null 값도 포함시키고, 필드 이름의 규격 맞추기 위해 필요한 설정
            mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);  // null 값 포함
            mapper.enable(SerializationFeature.INDENT_OUTPUT);  // 예쁘게 출력
            mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, false);  // 숫자값을 문자열로 처리하지 않도록 설정
//            mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);  // 숫자도 문자열로 처리

            // JSON을 String으로 변환
            String jsonResponse = mapper.writeValueAsString(inputData);

                        logger.info("영화조회정보 (Pretty Printed):\n{}", jsonResponse); // JSON 형식으로 로그 출력

            // HRM 모델 호출
            aiModelService.callHRMModel(jsonResponse, memberId);
        } catch (Exception e) {
            logger.error("Error converting inputData to JSON", e);
        }
            return CompletableFuture.completedFuture(ResponseEntity.status(500).body(null));
        }
//        // 사용자별 영화 정보 가져오기
//        UserMovieInfoResponse inputData = memberService.getUserMovieInfo(memberId);
//
//        // ObjectMapper 설정
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty Print 활성화
//        objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true); // 필드 이름에 따옴표 포함
//
//        try {
//            // Pretty Printed JSON 생성
//            String jsonInputData = objectMapper.writeValueAsString(inputData);
//            logger.info("영화조회정보 (Pretty Printed):\n{}", jsonInputData); // JSON 형식으로 로그 출력
//
//            // HRM 모델 호출
//            aiModelService.callHRMModel(jsonInputData, memberId);
//        } catch (Exception e) {
//            logger.error("Error converting inputData to JSON", e);
//        }
//
//        // 즉시 응답을 반환 (비동기 방식이므로 결과는 나중에 처리됨)
//        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(List.of("HRM recommendation initiated")));
//    }

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