package com.example.movierecommendations.member.dto.survey;

import com.example.movierecommendations.member.domain.Member;
import com.example.movierecommendations.member.domain.PreferredActor;
import com.example.movierecommendations.member.domain.PreferredGenre;
import com.example.movierecommendations.member.domain.Survey;
import com.example.movierecommendations.member.vo.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSurveyRequestDTO {
    private Gender gender;
    private String age;
    private List<String> preferredGenres;
    private List<String> preferredActors;

    public Survey toSurvey(Long memberId) {
        return Survey.builder()
                .memberId(memberId)
                .gender(gender)
                .age(age)
                .build();
    }

    public List<PreferredGenre> toPreferredGenres(Member member) {
        return preferredGenres.stream()
                .map(genre -> PreferredGenre.builder()
                        .member(member)  // Member 객체 설정
                        .genre(genre)
                        .build())
                .collect(Collectors.toList());
    }

    public List<PreferredActor> toPreferredActor(Member member) {
        return preferredActors.stream()
                .map(actor -> PreferredActor.builder()
                        .member(member)  // Member 객체 설정
                        .actor(actor)
                        .build())
                .collect(Collectors.toList());
    }
}
