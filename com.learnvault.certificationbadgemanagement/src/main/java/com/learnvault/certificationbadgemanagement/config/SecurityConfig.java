package com.learnvault.certificationbadgemanagement.config;

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

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Badge Management
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/badges")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/badges")
                        .hasAnyRole(
                                "ADMIN",
                                "INSTRUCTOR",
                                "LEARNER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/badges/**")
                        .hasAnyRole(
                                "ADMIN",
                                "INSTRUCTOR",
                                "LEARNER")

                        // Certifications
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/certifications")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/certifications")
                        .hasAnyRole(
                                "ADMIN",
                                "INSTRUCTOR",
                                "LEARNER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/certifications/**")
                        .hasAnyRole(
                                "ADMIN",
                                "INSTRUCTOR",
                                "LEARNER")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/certifications/*/revoke")
                        .hasRole("ADMIN")

                        // Badge Awards
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/badge-awards")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/badge-awards")
                        .hasAnyRole(
                                "ADMIN",
                                "INSTRUCTOR",
                                "LEARNER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/badge-awards/**")
                        .hasAnyRole(
                                "ADMIN",
                                "INSTRUCTOR",
                                "LEARNER")

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}