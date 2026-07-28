package com.learnvault.assessmentevaluation.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // INSTRUCTOR & ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/assessments")
                        .hasAnyRole("INSTRUCTOR", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/assessments/*/questions")
                        .hasAnyRole("INSTRUCTOR", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/assessments/*/questions/*")
                        .hasAnyRole("INSTRUCTOR", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/assessments/*/status")
                        .hasAnyRole("INSTRUCTOR", "ADMIN")

                        // LEARNER
                        .requestMatchers(HttpMethod.POST, "/api/attempts")
                        .hasAnyRole("LEARNER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/attempts")
                        .hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/attempts/**")
                        .hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")

                        // Any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/assessments")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/assessments/**")
                        .authenticated()

                        .anyRequest()
                        .authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}