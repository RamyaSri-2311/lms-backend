package com.learnvault.identityaccessmanagement.controller;

import com.learnvault.identityaccessmanagement.config.JwtUtil;
import com.learnvault.identityaccessmanagement.dto.request.LoginRequest;
import com.learnvault.identityaccessmanagement.dto.request.UserRequest;
import com.learnvault.identityaccessmanagement.dto.response.LoginResponse;
import com.learnvault.identityaccessmanagement.dto.response.UserResponse;
import com.learnvault.identityaccessmanagement.entity.User;
import com.learnvault.identityaccessmanagement.exception.BadRequestException;
import com.learnvault.identityaccessmanagement.repository.UserRepository;
import com.learnvault.identityaccessmanagement.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())   // ← ADD THIS
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .build();
    }
    @PostMapping("/register")
    public LoginResponse register(@RequestBody UserRequest request) {

        UserResponse created = userService.createUser(request);

        User user = userRepository.findById(created.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
