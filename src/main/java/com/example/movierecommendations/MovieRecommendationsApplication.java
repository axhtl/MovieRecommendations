package com.example.movierecommendations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.movierecommendations")
public class MovieRecommendationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieRecommendationsApplication.class, args);
	}

}
