package com.learnvault.identityaccessmanagement.controller;

import com.learnvault.identityaccessmanagement.config.JwtUtil;
import com.learnvault.identityaccessmanagement.dto.request.LoginRequest;
import com.learnvault.identityaccessmanagement.dto.request.UserRequest;
import com.learnvault.identityaccessmanagement.dto.response.LoginResponse;
import com.learnvault.identityaccessmanagement.dto.response.UserResponse;
import com.learnvault.identityaccessmanagement.entity.User;
import com.learnvault.identityaccessmanagement.entity.enums.Status;
import com.learnvault.identityaccessmanagement.exception.AccountStatusException;
import com.learnvault.identityaccessmanagement.exception.BadRequestException;
import com.learnvault.identityaccessmanagement.exception.UnauthorizedException;
import com.learnvault.identityaccessmanagement.repository.UserRepository;
import com.learnvault.identityaccessmanagement.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim();

        // 1) The email must belong to an existing account.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("No account found with this email address."));

        // 2) The password must match.
        if (request.getPassword() == null
                || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Incorrect password. Please try again.");
        }

        // 3) The account must be allowed to log in. INACTIVE is the only non-active
        //    status today; LOCKED/DISABLED would map here too if added to Status.
        if (user.getStatus() != Status.ACTIVE) {
            throw new AccountStatusException("Your account is inactive. Please contact the administrator.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
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
