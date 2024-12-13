package com.example.movierecommendations.AIMODEL;

import com.example.movierecommendations.member.dto.UserMovieInfoResponse;
import com.example.movierecommendations.member.service.AuthenticationService;
import com.example.movierecommendations.member.service.MemberService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
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
    @PostMapping("/predict/{memberId}")
    public ResponseEntity<List<Map<String, Object>>> HRMpredict(@PathVariable Long memberId) throws JsonProcessingException {
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

        // HRM 모델을 호출하고 결과를 동기적으로 반환
        try {
            List<Map<String, Object>> result = aiModelService.callHRMModel(jsonInputData, memberId);

            // 추천 결과가 성공적으로 처리된 후, 클라이언트에게 추천 결과를 JSON 형태로 반환
            return ResponseEntity.ok(result);  // List<Map<String, Object>> 형태로 반환
        } catch (Exception ex) {
            // 예외 처리: 에러가 발생한 경우
            log.error("Error calling HRM model", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of(Map.of("error", "Error occurred while processing the recommendation")));
        }
    }


    // LLM 모델1을 호출하는 메서드
    @PostMapping("/chatbot/{memberId}")
    public ResponseEntity<List<Map<String, Object>>> LLMpredict(@PathVariable Long memberId, @RequestBody String inputString) throws JsonProcessingException {
        log.info("사용자가 입력한 텍스트: {}", inputString);

        // LLM 모델을 호출하고 결과를 동기적으로 반환
        try {
            // LLM 모델을 호출하고, 반환 값은 FastAPI로부터 받은 응답
            List<Map<String, Object>> result = aiModelService.callLLMModel(inputString, memberId);

            // 결과가 제대로 온 경우, 적절한 응답 포맷으로 변환
            if (result != null && !result.isEmpty()) {
                // FastAPI의 응답에서 "llm_response" 값을 가져오기
                String llmResponse = (String) result.get(0).get("llm_response");
                List<Map<String, Object>> response = List.of(Map.of("llm_response", llmResponse));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(List.of(Map.of("error", "Failed to get valid response from LLM")));
            }
        } catch (Exception ex) {
            // 예외 처리: 에러가 발생한 경우
            log.error("Error calling LLM model", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of(Map.of("error", "Error occurred while processing the recommendation")));
        }
    }


    // LLM 모델2을 호출하는 메서드
    @PostMapping("/chatbot/llm/reason/{movieCd}")
    public ResponseEntity<List<Map<String, Object>>> LLMReasonpredict(@PathVariable Long movieCd, @RequestBody String inputString) throws JsonProcessingException {
        log.info("사용자가 입력한 텍스트: {}", inputString);

        // LLM 모델을 호출하고 결과를 동기적으로 반환
        try {
            // LLM 모델을 호출하고, 반환 값은 FastAPI로부터 받은 응답
            List<Map<String, Object>> result = aiModelService.callLLMReasonModel(inputString, movieCd);

            // 결과가 제대로 온 경우, 적절한 응답 포맷으로 변환
            if (result != null && !result.isEmpty()) {
                // FastAPI의 응답에서 "llm_response" 값을 가져오기
                String llmResponse = (String) result.get(0).get("llm_response");
                List<Map<String, Object>> response = List.of(Map.of("llm_response", llmResponse));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(List.of(Map.of("error", "Failed to get valid response from LLM-Reason")));
            }
        } catch (Exception ex) {
            // 예외 처리: 에러가 발생한 경우
            log.error("Error calling LLM-Reason model", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of(Map.of("error", "Error occurred while processing the recommendation")));
        }
    }


}
