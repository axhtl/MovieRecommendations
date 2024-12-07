package com.example.movierecommendations.recommended.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecommendationRequestDTO {
    private Long memberId;
    private List<MovieRecommendationDTO> recommendations;

    // getters and setters

}
