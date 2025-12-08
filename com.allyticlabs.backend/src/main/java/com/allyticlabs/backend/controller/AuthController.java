package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.dto.AuthRequest;
import com.allyticlabs.backend.dto.AuthResponse;
import com.allyticlabs.backend.dto.RegisterRequest;
import com.allyticlabs.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest authRequest) {

        log.info("Login attempt for user: {}", authRequest.getUsername());

        AuthResponse response = authService.authenticate(authRequest);

        log.info("User logged in successfully: {}", authRequest.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        log.info("Registration request for user: {}", registerRequest.getUsername());

        AuthResponse response = authService.register(registerRequest);

        log.info("User registered successfully: {}", registerRequest.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("Authorization") String refreshToken) {

        log.info("Token refresh request");

        String token = refreshToken.startsWith("Bearer ") 
            ? refreshToken.substring(7) 
            : refreshToken;

        AuthResponse response = authService.refreshToken(token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String token) {

        log.info("Logout request");

        String jwtToken = token.startsWith("Bearer ") 
            ? token.substring(7) 
            : token;

        authService.logout(jwtToken);

        return ResponseEntity.ok(Map.of(
            "message", "Logged out successfully"
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader("Authorization") String token) {

        log.info("Token validation request");

        String jwtToken = token.startsWith("Bearer ") 
            ? token.substring(7) 
            : token;

        Map<String, Object> validation = authService.validateToken(jwtToken);

        return ResponseEntity.ok(validation);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestHeader("Authorization") String token) {

        log.info("Get current user request");

        String jwtToken = token.startsWith("Bearer ") 
            ? token.substring(7) 
            : token;

        Map<String, Object> userInfo = authService.getCurrentUser(jwtToken);

        return ResponseEntity.ok(userInfo);
    }
}
