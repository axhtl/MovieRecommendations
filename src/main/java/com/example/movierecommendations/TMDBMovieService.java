package com.example.movierecommendations;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TMDBMovieService {

    private final OkHttpClient client = new OkHttpClient();
    private final String SEARCH_API_URL = "https://api.themoviedb.org/3/search/movie";
    private final String DETAIL_API_URL = "https://api.themoviedb.org/3/movie/";

    @Value("${tmdb.apikey}")
    private String API_KEY;

    public String searchMovies(String query, boolean includeAdult, String language, int page) {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("%s?query=%s&include_adult=%s&language=%s&page=%d",
                SEARCH_API_URL, query, includeAdult, language, page);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
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

    public String getMovieDetails(int movieId, String language) {
        String url = DETAIL_API_URL + movieId + "?language=" + language;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization","Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new RuntimeException("Failed to fetch movie details: " + response.code());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while calling API: " + e.getMessage(), e);
        }
    }
}