package com.example.movierecommendations.member.domain;

import com.example.movierecommendations.member.vo.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "survey")
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyId;

    @Column
    private Long memberId;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String age;

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateAge(String age) {
        this.age = age;
    }
}
