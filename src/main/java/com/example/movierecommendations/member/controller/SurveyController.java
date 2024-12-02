package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.CreateSurveyRequestDTO;
import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.service.SurveyService;
import com.example.movierecommendations.member.vo.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping("/{memberId}")
    public ResponseEntity<SaveResponseDTO> saveSurvey(@PathVariable Long memberId, @RequestBody CreateSurveyRequestDTO request) {
        // 1. 설문조사 데이터를 DB에 저장
        Long surveyId = surveyService.saveSurvey(memberId, request);

        // 2. AI 서버로 추천 요청 보내기 (이제 CreateSurveyRequestDTO만 전달)
        List<String> recommendations = sendToAiServer(request); // 사용자 행동 데이터 대신 요청 데이터 전달

        // 3. 추천 결과 처리
        return ResponseEntity.ok(new SaveResponseDTO(surveyId, HttpStatus.OK.value(), "설문조사가 정상적으로 등록되었고, 추천 결과를 받았습니다."));
    }

    // AI 서버로 데이터를 보내고, 추천 결과를 받는 메소드
    private List<String> sendToAiServer(CreateSurveyRequestDTO request) {
        RestTemplate restTemplate = new RestTemplate();

        // Flask AI 서버 URL
        String AI_SERVER_URL = "http://localhost:5000/api/ai-recommendation";

        // AI 서버에 POST 요청을 보내는 코드git add
        // CreateSurveyRequestDTO를 바로 Flask 서버에 전달
        ResponseEntity<AIRecommendationResponse> response = restTemplate.postForEntity(AI_SERVER_URL, request, AIRecommendationResponse.class);

        // AI 서버의 응답에서 추천 결과 추출
        if (response.getStatusCode() == HttpStatus.OK) {
            return Objects.requireNonNull(response.getBody()).getRecommendations();
        } else {
            return List.of("추천을 가져올 수 없습니다.");
        }
    }

    // AI 서버의 응답을 받을 DTO (AIRecommendationResponse)
    @Setter
    @Getter
    public static class AIRecommendationResponse {
        private List<String> recommendations;
    }

    // 성별 수정
    @PatchMapping("/{memberId}/gender")
    public ResponseEntity<String> updateGender(@PathVariable Long memberId, @RequestBody Gender gender) {
        surveyService.updateGender(memberId, gender);
        return ResponseEntity.ok("성별이 성공적으로 수정되었습니다.");
    }

    // 나이 수정
    @PatchMapping("/{memberId}/age")
    public ResponseEntity<String> updateAge(@PathVariable Long memberId, @RequestBody String age) {
        surveyService.updateAge(memberId, age);
        return ResponseEntity.ok("나이가 성공적으로 수정되었습니다.");
    }

    // 선호 장르 추가
    @PostMapping("/{memberId}/genre")
    public ResponseEntity<String> addPreferredGenre(@PathVariable Long memberId, @RequestBody String genre) {
        surveyService.addPreferredGenre(memberId, genre);
        return ResponseEntity.ok("선호 장르가 성공적으로 추가되었습니다.");
    }

    // 선호 장르 삭제
    @DeleteMapping("/preferred-genres/{preferredGenreId}")
    public ResponseEntity<String> deletePreferredGenre(@PathVariable Long preferredGenreId) {
        surveyService.deletePreferredGenre(preferredGenreId);
        return ResponseEntity.ok("선호 장르가 성공적으로 삭제되었습니다.");
    }

    // 선호 배우 추가
    @PostMapping("/{memberId}/actor")
    public ResponseEntity<String> addPreferredActor(@PathVariable Long memberId, @RequestBody String actor) {
        surveyService.addPreferredActor(memberId, actor);
        return ResponseEntity.ok("선호 배우가 성공적으로 추가되었습니다.");
    }

    // 선호 배우 삭제
    @DeleteMapping("/preferred-actors/{preferredActorId}")
    public ResponseEntity<String> deletePreferredActor(@PathVariable Long preferredActorId) {
        surveyService.deletePreferredActor(preferredActorId);
        return ResponseEntity.ok("선호 배우가 성공적으로 삭제되었습니다.");
    }
}
