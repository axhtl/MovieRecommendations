//package com.example.movierecommendations.member.dto;
//
//import com.example.movierecommendations.member.domain.Member;
//import com.example.movierecommendations.member.domain.MovieInfo;
//import com.example.movierecommendations.member.domain.Survey;
//import lombok.Builder;
//import lombok.Getter;
//
//import java.util.List;
//
//@Getter
//@Builder
//public class AllUserMovieInfoResponse {
//    private Member member; // 사용자 정보
//    private Survey survey; // 설문조사 정보
//    private List<ReviewInfo> reviewInfos; // 리뷰 정보 리스트
//    private List<String> preferredGenres; // 선호 장르
//    private List<String> preferredActors; // 선호 배우
//
//    @Builder
//    @Getter
//    public static class ReviewInfo {
//        private Long reviewId;
//        private MovieInfo movieInfo;
//        private List<String> actors;
//        private List<String> directors;
//        private List<String> genres;
//    }
//}
