package com.learnvault.coursecatalogcontentmanagement.config;

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

                        // Course View
                        .requestMatchers(HttpMethod.GET, "/api/courses")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/courses/**")
                        .authenticated()

                        // Learning Path View
                        .requestMatchers(HttpMethod.GET, "/api/learning-paths")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/learning-paths/**")
                        .authenticated()

                        // Course Management (ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/courses")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/courses/*")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH,
                                "/api/courses/*/publish")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH,
                                "/api/courses/*/archive")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/courses/*")
                        .hasRole("ADMIN")

                        // Module Management
                        .requestMatchers(HttpMethod.POST,
                                "/api/courses/*/modules")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/courses/*/modules/*")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/courses/*/modules/*")
                        .hasRole("ADMIN")

                        // Learning Path Management
                        .requestMatchers(HttpMethod.POST,
                                "/api/learning-paths")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH,
                                "/api/learning-paths/*/status")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/learning-paths/*")
                        .hasRole("ADMIN")

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