package com.example.BNPL.controller;

import com.example.BNPL.dto.AuthRequest;
import com.example.BNPL.dto.RegisterRequest;
import com.example.BNPL.entity.User;
import com.example.BNPL.service.UserService;
import com.example.BNPL.service.UserDetailsServiceImpl;
import com.example.BNPL.config.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        User user = userService.register(request);
        log.info("User registered successfully - userId: {}, email: {}, initialCreditScore: {}",
                user.getUserId(), user.getEmail(), user.getCreditScore());
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "userId", user.getUserId(),
                "initialCreditScore", user.getCreditScore()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            final String token = jwtService.generateToken(userDetails);
            log.info("Login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            log.warn("Login failed for email: {} - reason: {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }
}
