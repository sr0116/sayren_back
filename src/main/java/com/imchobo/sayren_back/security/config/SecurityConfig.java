package com.imchobo.sayren_back.security.config;

import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.security.filter.JwtAuthenticationFilter;
import com.imchobo.sayren_back.security.handler.OAuth2FailureHandler;
import com.imchobo.sayren_back.security.handler.OAuthSuccessHandler;
import com.imchobo.sayren_back.security.resolver.CustomAuthorizationRequestResolver;
import com.imchobo.sayren_back.security.service.CustomOAuth2UserService;
import com.imchobo.sayren_back.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuthSuccessHandler oAuthSuccessHandler;
  private final OAuth2FailureHandler oAuthFailureHandler;
  private final ClientRegistrationRepository clientRegistrationRepository;
  private final RedisUtil redisUtil;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .cors(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable) // REST API라면 CSRF 비활성화
//      .authorizeHttpRequests(auth -> auth
//              .requestMatchers("/api/user/**", "/api/auth/**", "/oauth2/**").permitAll() // 누구나 접근 가능
//              .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 전용
//              .anyRequest().authenticated() // 나머지는 로그인 필요
//      )

      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/api/user/**", "/api/auth/**", "/oauth2/**").permitAll()
          .requestMatchers("/api/admin/**").hasRole("ADMIN")
//      .anyRequest().authenticated() // 나머지는 로그인 필요 (원래 설정)
          .anyRequest().permitAll() // 테스트용: 모든 API 접근 허용
      )




      .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 안씀
      .oauth2Login(oauth2 -> oauth2
        .authorizationEndpoint(auth -> auth
          .authorizationRequestResolver(
                  new CustomAuthorizationRequestResolver(clientRegistrationRepository, redisUtil)
          )
        )
        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
        .successHandler(oAuthSuccessHandler)
        .failureHandler(oAuthFailureHandler))
      .httpBasic(AbstractHttpConfigurer::disable) // Basic 인증도 안씀
      .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }


  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    // CORS 정책 객체 생성
    CorsConfiguration configuration = new CorsConfiguration();

    // 주소 설정
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // 쿠키, Authorization 같은 자격 증명 정보를 포함한 요청 허용
    configuration.setAllowCredentials(true);

    // 브라우저가 요청할 때 추가하는 모든 커스텀 헤더를 허용 ("Authorization", "Content-Type" 이런식으로 사용하기도 함)
    configuration.setAllowedHeaders(List.of("*"));

    // CORS 매핑 소스 생성 (URL 패턴별로 CORS 정책을 등록하는 역할)
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    // 모든 경로("/**")에 대해 위에서 정의한 configuration 적용
    source.registerCorsConfiguration("/**", configuration);

    // Spring Security filter chain에서 참조할 수 있도록 Bean 반환
    return source;
  }
}
