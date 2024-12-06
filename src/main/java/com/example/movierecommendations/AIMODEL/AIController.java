package com.example.movierecommendations.AIMODEL;

import com.example.movierecommendations.member.dto.UserMovieInfoResponse;
import com.example.movierecommendations.member.service.AuthenticationService;
import com.example.movierecommendations.member.service.MemberService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/ai")  // 엔드포인트 URL
public class AIController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final AIModelService aiModelService;  // AIModelService를 주입
    private final MemberService memberService;

    @Autowired
    public AIController(AIModelService aiModelService, MemberService memberService) {
        this.aiModelService = aiModelService;
        this.memberService = memberService;
    }

    // HRM 모델을 호출하는 메서드
//    @PostMapping("/predict/{memberId}")
//    public CompletableFuture<ResponseEntity<List<String>>> HRMpredict(@PathVariable Long memberId) {
//        // 사용자별 영화 정보 가져오기
//        UserMovieInfoResponse inputData = memberService.getUserMovieInfo(memberId);
//
//        try {
//            // Jackson을 사용하여 inputData를 JSON 문자열로 변환
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            // Java 8 날짜/시간 모듈 등록
//            objectMapper.registerModule(new JavaTimeModule());
//
//            // null 값 필드 제외 설정
//            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//            // JSON 문자열로 변환
//            String jsonInputData = objectMapper.writeValueAsString(inputData);
//
//            // JSON 형식으로 로그 출력
//            log.info("영화조회정보:{}", jsonInputData);
//        } catch (JsonProcessingException e) {
//            log.error("Error converting inputData to JSON", e);
//        }
//
//        // HRM 모델 호출 (AIModelService의 callHRMModel 사용)
//        aiModelService.callHRMModel(inputData, memberId);
//
//        // 즉시 응답을 반환 (비동기 방식이므로 결과는 나중에 처리됨)
//        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(List.of("HRM recommendation initiated")));
//    }


    // HRM 모델을 호출하는 메서드
    @PostMapping("/predict/{memberId}")
    public CompletableFuture<ResponseEntity<List<String>>> HRMpredict(@PathVariable Long memberId) throws JsonProcessingException {
        // 사용자별 영화 정보 가져오기
        UserMovieInfoResponse inputData = memberService.getUserMovieInfo(memberId);

        // Jackson을 사용하여 inputData를 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8 날짜/시간 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());
        // null 값 필드 제외 설정
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // JSON 문자열로 변환
        String jsonInputData = objectMapper.writeValueAsString(inputData);

        log.info("영화조회정보:{}", jsonInputData);

        // HRM 모델을 호출하고 결과를 반환
        return aiModelService.callHRMModel(jsonInputData, memberId)
                .thenApply(result -> {
                    // HRM 추천 결과가 성공적으로 처리된 후, 클라이언트에게 추천 결과를 반환
                    return ResponseEntity.ok(result); // 추천 결과를 응답으로 반환
                })
                .exceptionally(ex -> {
                    // 예외 처리: 에러가 발생한 경우
                    log.error("Error calling HRM model", ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(List.of("Error occurred while processing the recommendation"));
                });
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
