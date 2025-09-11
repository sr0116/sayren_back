package com.imchobo.sayren_back.domain.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
// 포트원 테스트용 config
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable()) //  Postman 테스트용 CSRF 해제
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/**").permitAll()  //  구독 API는 누구나 허용
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // CORS Preflight 허용
                    .anyRequest().authenticated()
            );

    return http.build();
  }
}
