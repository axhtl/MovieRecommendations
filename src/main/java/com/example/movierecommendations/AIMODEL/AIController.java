package com.example.movierecommendations.AIMODEL;

import com.example.movierecommendations.member.dto.UserMovieInfoResponse;
import com.example.movierecommendations.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")  // 엔드포인트 URL
public class AIController {

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
    public CompletableFuture<ResponseEntity<List<String>>> HRMpredict(@PathVariable Long memberId) {
        // 사용자별 영화 정보 가져오기
        UserMovieInfoResponse inputData = memberService.getUserMovieInfo(memberId);

        // HRM 모델 호출 (AIModelService의 callHRMModel 사용)
        aiModelService.callHRMModel(inputData, memberId);

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
