package com.example.movierecommendations.member.dto.survey;

import com.example.movierecommendations.member.vo.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSurveyRequestDTO {
    private Gender gender; // 성별 수정
    private String age; // 나이 수정
    private List<String> preferredGenres; // 선호 장르 수정
    private List<String> preferredActors; // 선호 배우 수정
}
