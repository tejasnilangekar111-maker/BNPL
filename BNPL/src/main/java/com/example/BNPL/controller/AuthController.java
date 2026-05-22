package com.example.BNPL.controller;

import com.example.BNPL.dto.AuthRequest;
import com.example.BNPL.dto.RegisterRequest;
import com.example.BNPL.entity.User;
import com.example.BNPL.service.UserService;
import com.example.BNPL.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "userId", user.getUserId(),
                "initialCreditScore", user.getCreditScore()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        final UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
        final String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
