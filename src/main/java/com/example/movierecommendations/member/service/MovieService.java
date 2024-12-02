package com.example.movierecommendations.member.service;

import com.example.movierecommendations.member.dto.SaveResponseDTO;
import com.example.movierecommendations.member.dto.survey.SurveyResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MovieService {

    @Value("${movie.apikey}")
    private String API_KEY; // 영화 API 키를 외부 설정 파일에서 불러오기

    private static final String MOVIE_LIST_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json"; // 영화 목록 검색 API URL
    private static final String MOVIE_DETAIL_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo"; // 영화 상세정보 조회 API URL

    private final RestTemplate restTemplate = new RestTemplate(); // REST API 호출을 위한 RestTemplate 객체 생성

    /**
     * Flask 서버로 설문 데이터를 전달하고, 추천 결과를 받는 메소드
     * @param surveyData 설문조사 결과를 담고 있는 객체
     * @param movieName 검색된 영화 이름
     * @return 추천 결과
     */
    public String getRecommendationFromFlask(SurveyResponseDTO surveyData, String movieName) {
        String flaskUrl = "http://localhost:5000/api/ai-recommendation"; // Flask 서버 URL

        // URI에 movieName을 쿼리 파라미터로 추가
        URI uri = UriComponentsBuilder.fromHttpUrl(flaskUrl)
                .queryParam("movieName", movieName) // 영화 이름을 쿼리 파라미터로 추가
                .encode(StandardCharsets.UTF_8) // 인코딩 명시
                .build()
                .toUri();

        // HTTP 요청 헤더 및 본문 설정 (설문 데이터는 JSON 바디로 전송)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SurveyResponseDTO> entity = new HttpEntity<>(surveyData, headers); // 설문 데이터를 본문에 포함

        // POST 요청을 보내고 응답을 받음
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        return responseEntity.getBody(); // 추천 결과 반환
    }

    /**
     * 영화 이름으로 영화를 검색하고 결과를 반환하는 메소드
     * @param movieName 검색할 영화 이름
     * @param itemPerPage 한 번에 가져올 항목 수
     * @return 영화 목록을 JSON 형태로 반환
     */
    public String searchMoviesByName(String movieName, int itemPerPage) {
        try {
            // URI 생성 및 UTF-8 인코딩 (UriComponentsBuilder 자동 인코딩 사용)
            URI uri = UriComponentsBuilder.fromHttpUrl(MOVIE_LIST_URL)
                    .queryParam("key", API_KEY) // API 키
                    .queryParam("movieNm", movieName) // 검색할 영화 이름
                    .queryParam("itemPerPage", itemPerPage) // 한 번에 가져올 영화 목록 수
                    .encode(StandardCharsets.UTF_8) // 인코딩 명시
                    .build()
                    .toUri();

            System.out.println("Request URL: " + uri); // 디버깅을 위한 요청 URL 출력

            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json; charset=UTF-8");
            HttpEntity<String> entity = new HttpEntity<>(headers); // 헤더 설정만 포함한 HTTP 엔티티 생성

            // API 호출
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody(); // API 응답 내용

            // JSON 포맷팅 후 반환
            return prettyPrintJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "API 요청 중 오류 발생"; // 오류 발생 시 메시지 반환
        }
    }

    /**
     * 영화 상세 정보를 조회하는 메소드
     * @param movieCd 영화 코드
     * @param responseType 응답 형식 (json 또는 xml)
     * @return 영화 상세 정보 (JSON 또는 XML)
     */
    public String searchMovieDetails(String movieCd, String responseType) {
        try {
            // URI 생성 (응답 형식에 따라 .xml 또는 .json으로 구분)
            URI uri = UriComponentsBuilder.fromHttpUrl(MOVIE_DETAIL_URL + "." + responseType)
                    .queryParam("key", API_KEY) // API 키
                    .queryParam("movieCd", movieCd) // 영화 코드
                    .encode(StandardCharsets.UTF_8) // 인코딩 명시
                    .build()
                    .toUri();

            System.out.println("Request URL: " + uri); // 디버깅을 위한 요청 URL 출력

            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/" + responseType + "; charset=UTF-8"); // 응답 형식에 따라 Content-Type 설정
            HttpEntity<String> entity = new HttpEntity<>(headers); // 헤더 설정만 포함한 HTTP 엔티티 생성

            // API 호출
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody(); // API 응답 내용

            // 응답 형식에 맞게 JSON 포맷팅 또는 그대로 반환 (XML은 포맷팅하지 않음)
            if ("json".equalsIgnoreCase(responseType)) {
                return prettyPrintJson(response); // JSON 형식은 보기 좋게 포맷팅
            } else {
                return response; // XML 형식은 그대로 반환
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "API 요청 중 오류 발생"; // 오류 발생 시 메시지 반환
        }
    }

    /**
     * JSON 문자열을 보기 좋게 포맷팅하는 메소드
     * @param json 포맷팅할 JSON 문자열
     * @return 포맷팅된 JSON 문자열
     */
    private String prettyPrintJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper(); // Jackson의 ObjectMapper를 사용
            Object jsonObject = mapper.readValue(json, Object.class); // JSON 문자열을 객체로 변환
            mapper.enable(SerializationFeature.INDENT_OUTPUT); // JSON 가독성을 위한 들여쓰기 활성화
            return mapper.writeValueAsString(jsonObject); // 객체를 다시 JSON 문자열로 변환

        } catch (Exception e) {
            e.printStackTrace();
            return json; // 포맷팅에 실패하면 원본 JSON을 그대로 반환
        }
    }
}
