package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.dto.survey.CreateSurveyRequestDTO;
import com.example.movierecommendations.member.service.SurveyService;
import com.example.movierecommendations.member.vo.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;

    // 설문조사 등록
    @PostMapping("/{memberId}")
    public ResponseEntity<SaveResponseDTO> saveSurvey(@PathVariable("memberId") Long memberId, @RequestBody CreateSurveyRequestDTO request) {
        Long surveyId = surveyService.saveSurvey(memberId, request);
        return ResponseEntity.ok(new SaveResponseDTO(
                surveyId, HttpStatus.OK.value(), "설문조사가 정상적으로 등록되었습니다."
        ));
    }

    // 성별 수정
    @PatchMapping("/{memberId}/gender")
    public ResponseEntity<String> updateGender(@PathVariable("memberId") Long memberId, @RequestBody Gender gender) {
        surveyService.updateGender(memberId, gender);
        return ResponseEntity.ok("성별이 성공적으로 수정되었습니다.");
    }

    // 나이 수정
    @PatchMapping("/{memberId}/age")
    public ResponseEntity<String> updateAge(@PathVariable("memberId") Long memberId, @RequestBody String age) {
        surveyService.updateAge(memberId, age);
        return ResponseEntity.ok("나이가 성공적으로 수정되었습니다.");
    }

    // 선호 장르 추가
    @PostMapping("/{memberId}/genre")
    public ResponseEntity<String> addPreferredGenre(@PathVariable("memberId") Long memberId, @RequestBody String genre) {
        surveyService.addPreferredGenre(memberId, genre);
        return ResponseEntity.ok("선호 장르가 성공적으로 추가되었습니다.");
    }

    // 선호 장르 삭제
    @DeleteMapping("/preferred-genres/{preferredGenreId}")
    public ResponseEntity<String> deletePreferredGenre(@PathVariable("preferredGenreId") Long preferredGenreId) {
        surveyService.deletePreferredGenre(preferredGenreId);
        return ResponseEntity.ok("선호 장르가 성공적으로 삭제되었습니다.");
    }

    // 선호 배우 추가
    @PostMapping("/{memberId}/actor")
    public ResponseEntity<String> addPreferredActor(@PathVariable("memberId") Long memberId, @RequestBody String actor) {
        surveyService.addPreferredActor(memberId, actor);
        return ResponseEntity.ok("선호 배우가 성공적으로 추가되었습니다.");
    }

    // 선호 배우 삭제
    @DeleteMapping("/preferred-actors/{preferredActorId}")
    public ResponseEntity<String> deletePreferredActor(@PathVariable("preferredActorId") Long preferredActorId) {
        surveyService.deletePreferredActor(preferredActorId);
        return ResponseEntity.ok("선호 배우가 성공적으로 삭제되었습니다.");
    }
}
