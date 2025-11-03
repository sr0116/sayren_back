package com.imchobo.sayren_back.security.config;


import org.springframework.http.HttpMethod;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.security.filter.JwtAuthenticationFilter;
import com.imchobo.sayren_back.security.handler.OAuth2FailureHandler;
import com.imchobo.sayren_back.security.handler.OAuthSuccessHandler;
import com.imchobo.sayren_back.security.resolver.CustomAuthorizationRequestResolver;
import com.imchobo.sayren_back.security.service.CustomOAuth2UserService;
import com.imchobo.sayren_back.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
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
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/user/**",
                            "/api/auth/**",
                            "/oauth2/**",
                            "/swagger-ui/**",
                            "/api-docs/**",
                            "/v3/api-docs/**"
                    ).permitAll() // 누구나 접근 가능
                    .requestMatchers(HttpMethod.GET, "/api/user/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 전용
                    .anyRequest().authenticated() // 나머지는 로그인 필요
            )
            .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 없음
            .oauth2Login(oauth2 -> oauth2
                    .authorizationEndpoint(auth2 -> auth2
                            .authorizationRequestResolver(
                                    new CustomAuthorizationRequestResolver(clientRegistrationRepository, redisUtil)
                            )
                    )
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                    .successHandler(oAuthSuccessHandler)
                    .failureHandler(oAuthFailureHandler)
            )
            .httpBasic(AbstractHttpConfigurer::disable) // Basic 인증 안씀
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }


  /**
   * CORS 정책 설정
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://15.165.159.88:3000",
            "http://15.165.159.88:8800",
            "http://15.165.159.88:8080",
            "http://sayren-backend:8080"
    ));

    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

}
