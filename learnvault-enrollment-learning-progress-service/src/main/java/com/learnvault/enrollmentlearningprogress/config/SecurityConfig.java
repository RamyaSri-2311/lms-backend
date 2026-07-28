package com.learnvault.enrollmentlearningprogress.config;

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

                        // Enrollment
                        .requestMatchers(HttpMethod.POST, "/api/enrollments")
                        .hasAnyRole("LEARNER", "ADMIN")

                        // Learning-flow actions (module complete / refresh)
                        .requestMatchers(HttpMethod.POST, "/api/enrollments/**")
                        .hasAnyRole("LEARNER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/enrollments")
                        .hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/enrollments/**")
                        .hasAnyRole("LEARNER", "INSTRUCTOR", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/enrollments/**")
                        .hasAnyRole("LEARNER", "ADMIN")

                        // Module Progress
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/enrollments/*/progress")
                        .hasAnyRole(
                                "LEARNER",
                                "INSTRUCTOR",
                                "ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/enrollments/*/progress/**")
                        .hasAnyRole(
                                "LEARNER",
                                "ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}