package com.example.movierecommendations;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TMDBMovieService {

    private final String API_URL = "https://api.themoviedb.org/3/search/movie";

    @Value("${tmdb.apikey}")
    private String API_KEY;

    public String searchMovies(String query, boolean includeAdult, String language, int page) {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("%s?query=%s&include_adult=%s&language=%s&page=%d",
                API_URL, query, includeAdult, language, page);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
//                .addHeader("Authorization", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new RuntimeException("Failed to fetch movie data: " + response.message());
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while calling the TMDb API", e);
        }
    }
}