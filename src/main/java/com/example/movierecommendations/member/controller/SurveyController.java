package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.domain.Recommendation;
import com.example.movierecommendations.member.dto.CreateSurveyRequestDTO;
import com.example.movierecommendations.member.dto.RecommendationDTO;
import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.service.SurveyService;
import com.example.movierecommendations.member.vo.Gender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;

    // 생성자 주입을 통해 SurveyService를 초기화
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    // 설문조사 저장 및 AI 서버에서 추천 결과 받기
    @PostMapping("/{memberId}")
    public ResponseEntity<SaveResponseDTO> saveSurvey(@PathVariable Long memberId, @RequestBody CreateSurveyRequestDTO request) {
        // 설문조사를 저장하고, AI 서버에서 추천 결과를 받아옴
        Long surveyId = surveyService.saveSurvey(memberId, request);

        // 응답 반환 - 설문조사 저장 성공 및 추천 결과가 반환되었다는 메시지
        return ResponseEntity.ok(new SaveResponseDTO(
                surveyId, HttpStatus.OK.value(), "설문조사가 정상적으로 등록되었고, 추천 결과를 받았습니다."
        ));
    }

    // 추천 결과 반환 (추천 영화 리스트)
    @GetMapping("/{memberId}/recommendations")
    public ResponseEntity<RecommendationDTO> getRecommendations(@PathVariable Long memberId) {
        // 추천 정보를 DTO로 반환
        RecommendationDTO recommendationDTO = surveyService.getRecommendation(memberId);

        return ResponseEntity.ok(recommendationDTO);
    }

    // 성별 수정
    @PatchMapping("/{memberId}/gender")
    public ResponseEntity<String> updateGender(@PathVariable Long memberId, @RequestBody Gender gender) {
        // 성별을 수정하는 서비스 호출
        surveyService.updateGender(memberId, gender);
        return ResponseEntity.ok("성별이 성공적으로 수정되었습니다.");
    }

    // 나이 수정
    @PatchMapping("/{memberId}/age")
    public ResponseEntity<String> updateAge(@PathVariable Long memberId, @RequestBody String age) {
        // 나이를 수정하는 서비스 호출
        surveyService.updateAge(memberId, age);
        return ResponseEntity.ok("나이가 성공적으로 수정되었습니다.");
    }

    // 선호 장르 추가
    @PostMapping("/{memberId}/genre")
    public ResponseEntity<String> addPreferredGenre(@PathVariable Long memberId, @RequestBody String genre) {
        // 선호 장르를 추가하는 서비스 호출
        surveyService.addPreferredGenre(memberId, genre);
        return ResponseEntity.ok("선호 장르가 성공적으로 추가되었습니다.");
    }

    // 선호 장르 삭제
    @DeleteMapping("/preferred-genres/{preferredGenreId}")
    public ResponseEntity<String> deletePreferredGenre(@PathVariable Long preferredGenreId) {
        // 선호 장르를 삭제하는 서비스 호출
        surveyService.deletePreferredGenre(preferredGenreId);
        return ResponseEntity.ok("선호 장르가 성공적으로 삭제되었습니다.");
    }

    // 선호 배우 추가
    @PostMapping("/{memberId}/actor")
    public ResponseEntity<String> addPreferredActor(@PathVariable Long memberId, @RequestBody String actor) {
        // 선호 배우를 추가하는 서비스 호출
        surveyService.addPreferredActor(memberId, actor);
        return ResponseEntity.ok("선호 배우가 성공적으로 추가되었습니다.");
    }

    // 선호 배우 삭제
    @DeleteMapping("/preferred-actors/{preferredActorId}")
    public ResponseEntity<String> deletePreferredActor(@PathVariable Long preferredActorId) {
        // 선호 배우를 삭제하는 서비스 호출
        surveyService.deletePreferredActor(preferredActorId);
        return ResponseEntity.ok("선호 배우가 성공적으로 삭제되었습니다.");
    }
}
