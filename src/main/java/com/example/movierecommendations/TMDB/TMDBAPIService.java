package com.example.movierecommendations.TMDB;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service // 서비스 클래스, Spring에서 관리
public class TMDBAPIService {

    private final OkHttpClient client = new OkHttpClient(); // HTTP 클라이언트
    private final String SEARCH_API_URL = "https://api.themoviedb.org/3/search/movie"; // 검색 API URL
    private final String DETAIL_API_URL = "https://api.themoviedb.org/3/movie/"; // 영화 상세정보 API URL
    private final String CREDIT_API_URL = "https://api.themoviedb.org/3/"; // 영화 크레딧 API URL

    @Value("${tmdb.apikey}") // API Key를 외부 설정에서 주입
    private String API_KEY;

    // 영화 검색 메서드
    public String searchMovies(String query, boolean includeAdult, String language, int page) {
        String url = String.format("%s?query=%s&include_adult=%s&language=%s&page=%d",
                SEARCH_API_URL, query, includeAdult, language, page); // URL 생성

        Request request = new Request.Builder() // API 요청 생성
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        return executeRequest(request); // 요청 실행
    }

    // 영화 상세 정보 조회 메서드
    public String getMovieDetails(int movieId, String language) {
        String url = DETAIL_API_URL + movieId + "?language=" + language; // 상세 정보 URL 생성

        Request request = new Request.Builder() // API 요청 생성
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization","Bearer " + API_KEY)
                .build();

        return executeRequest(request); // 요청 실행
    }

    // 영화 크레딧 정보 조회 메서드
    public String getMovieCredits(int movieId, String language) {
        String url = CREDIT_API_URL + "movie/" + movieId + "/credits?language=" + language; // 크레딧 정보 URL 생성

        Request request = new Request.Builder() // API 요청 생성
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("accept", "application/json")
                .build();

        return executeRequest(request); // 요청 실행
    }

    // API 요청 실행 공통 메서드
    private String executeRequest(Request request) {
        try (Response response = client.newCall(request).execute()) { // 요청 실행 및 응답 처리
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string(); // 성공적 응답 처리
            } else {
                throw new RuntimeException("API 호출 실패: " + response.code() + " - " + response.message()); // 오류 발생 시
            }
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생: " + e.getMessage(), e); // 예외 처리
        }
    }
}
