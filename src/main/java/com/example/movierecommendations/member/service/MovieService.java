package com.example.movierecommendations.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MovieService {
    private static final String API_KEY = "4a5f2946301b60fbdd1fb97dd73c52cb"; // 발급받은 API 키를 입력하세요.
    private static final String MOVIE_LIST_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";

    private final RestTemplate restTemplate;

    public String searchMoviesByName(String movieName, int itemPerPage) {
        try {
            // URI 생성 및 UTF-8 인코딩 (UriComponentsBuilder 자동 인코딩 사용)
            URI uri = UriComponentsBuilder.fromHttpUrl(MOVIE_LIST_URL)
                    .queryParam("key", API_KEY)
                    .queryParam("movieNm", movieName)
                    .queryParam("itemPerPage", itemPerPage)
                    .encode(StandardCharsets.UTF_8) // 인코딩 명시
                    .build()
                    .toUri();

            System.out.println("Request URL: " + uri);

            // HTTP 요청 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json; charset=UTF-8");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody();

            // JSON 포맷팅
            return prettyPrintJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "API 요청 중 오류 발생";
        }
    }

    private String prettyPrintJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object jsonObject = mapper.readValue(json, Object.class);
            mapper.enable(SerializationFeature.INDENT_OUTPUT); // 가독성을 위해 들여쓰기
            return mapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return json; // 포맷팅에 실패하면 원래 JSON을 반환
        }
    }
}
