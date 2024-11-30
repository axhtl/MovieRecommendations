package com.example.movierecommendations.member.controller;

import com.example.movierecommendations.member.dto.CreateSurveyRequestDTO;
import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping("/{memberId}")
    public ResponseEntity<SaveResponseDTO> saveSurvey(@PathVariable Long memberId, @RequestBody CreateSurveyRequestDTO request) {
        Long surveyId = surveyService.saveSurvey(memberId, request);
        return ResponseEntity.ok(new SaveResponseDTO(
                surveyId, HttpStatus.OK.value(), "설문조사가 정상적으로 등록되었습니다."
        ));
    }
}
