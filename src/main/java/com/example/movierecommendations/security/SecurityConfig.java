package com.example.movierecommendations.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(
////                                "/member/logout", "/member/password/**",
////                                "member/nickname/**", "/member/withdraw/**",
////                                "/review/**", "/survey/**",
//                                "/api/ai/predict/**"
//                        ).authenticated() // 특정 URL에 대해 인증 필요
//                        .requestMatchers("/**").permitAll()
//                )
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
//                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // 프레임 사용 허용
//
//        return http.build();
//    }


//@Bean
//public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http
//            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
//            .authorizeHttpRequests(authorize -> authorize
//                    // 인증 없이 접근 가능한 경로들
//                    .requestMatchers("/static/**", "/error", "/favicon.ico", "/robots.txt", "/icons/**").permitAll()
//
//                    // 인증이 필요한 경로들
//                    .requestMatchers("/api/ai/predict/**").authenticated()
//
//                    .anyRequest().permitAll()
//            )
//            // JWT 필터를 모든 경로에 적용하기 전에 예외를 설정
//            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
//            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // 프레임 사용 허용
//
//    return http.build();
//}


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
//                .authorizeRequests(authorize -> authorize
//                        // 모든 요청에 대해 인증을 요구하지 않음
//                        .requestMatchers("/**").permitAll()  // 모든 경로에 대해 인증 없이 접근 허용
//                )
//                // JWT 필터를 모든 경로에 적용하기 전에 예외를 설정
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
//                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // 프레임 사용 허용
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/static/**", "/icons/**", "/img/**").permitAll()
                        .requestMatchers("/api/ai/predict/**").authenticated()
                        .requestMatchers("/**").permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // 프레임 사용 허용

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
