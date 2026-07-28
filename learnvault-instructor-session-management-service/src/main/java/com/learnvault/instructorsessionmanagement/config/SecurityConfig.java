package com.learnvault.instructorsessionmanagement.config;

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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // Instructor Management

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/instructors/self")
                        .hasAnyRole(
                                "INSTRUCTOR",
                                "ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/instructors")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/instructors/*/status")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/instructors/*/rating")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/instructors")
                        .hasAnyRole(
                                "ADMIN",
                                "INSTRUCTOR")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/instructors/**")
                        .hasAnyRole(
                                "ADMIN",
                                "INSTRUCTOR")

                        // Training Sessions

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/training-sessions")
                        .hasAnyRole(
                                "INSTRUCTOR",
                                "ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/training-sessions/*/cancel",
                                "/api/training-sessions/*/start",
                                "/api/training-sessions/*/complete")
                        .hasAnyRole(
                                "INSTRUCTOR",
                                "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/training-sessions")
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/training-sessions/**")
                        .authenticated()

                        // Session Registration

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/session-registrations")
                        .hasAnyRole(
                                "LEARNER",
                                "ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/session-registrations/*/attendance")
                        .hasAnyRole(
                                "INSTRUCTOR",
                                "ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/session-registrations/*/cancel")
                        .hasAnyRole(
                                "LEARNER",
                                "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/session-registrations")
                        .authenticated()

                        // Session Feedback

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/session-feedback")
                        .hasAnyRole(
                                "LEARNER",
                                "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/session-feedback")
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