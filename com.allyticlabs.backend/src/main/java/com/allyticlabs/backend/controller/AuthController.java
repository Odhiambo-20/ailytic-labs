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

/**
 * Authentication Controller
 * Handles user authentication, registration, token management, and OAuth2 integration
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed.origins=https://allyticlabs-frontend.vercel.app,https://allytic-labs-frontend-git-main-victor-odhiambos-projects.vercel.app,https://allytic-labs-frontend-7l3o6f9fn-victor-odhiambos-projects.vercel.app,http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint - authenticates user with email and password
     * @param authRequest contains username (email) and password
     * @return AuthResponse with access token, refresh token, and user details
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Login attempt for user: {}", authRequest.getUsername());

        try {
            AuthResponse response = authService.authenticate(authRequest);
            log.info("User logged in successfully: {}", authRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {}", authRequest.getUsername(), e);
            throw e;
        }
    }

    /**
     * Register endpoint - creates new user account
     * @param registerRequest contains firstName, lastName, email, and password
     * @return AuthResponse with access token, refresh token, and user details
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration request for user: {}", registerRequest.getEmail());

        try {
            AuthResponse response = authService.register(registerRequest);
            log.info("User registered successfully: {}", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Registration failed for user: {}", registerRequest.getEmail(), e);
            throw e;
        }
    }

    /**
     * Refresh token endpoint - generates new access token using refresh token
     * @param refreshToken the refresh token from Authorization header
     * @return AuthResponse with new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        log.info("Token refresh request");

        try {
            String token = refreshToken.startsWith("Bearer ")
                ? refreshToken.substring(7)
                : refreshToken;

            AuthResponse response = authService.refreshToken(token);
            log.info("Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw e;
        }
    }

    /**
     * Logout endpoint - invalidates user session
     * @param token the access token from Authorization header
     * @return success message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        log.info("Logout request");

        try {
            String jwtToken = token.startsWith("Bearer ")
                ? token.substring(7)
                : token;

            authService.logout(jwtToken);
            log.info("User logged out successfully");

            return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("Logout failed", e);
            throw e;
        }
    }

    /**
     * Validate token endpoint - checks if token is valid
     * @param token the access token from Authorization header
     * @return validation result with token validity status
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        log.info("Token validation request");

        try {
            String jwtToken = token.startsWith("Bearer ")
                ? token.substring(7)
                : token;

            Map<String, Object> validation = authService.validateToken(jwtToken);
            log.info("Token validation completed");
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "message", "Invalid token"
            ));
        }
    }

    /**
     * Get current user endpoint - retrieves authenticated user information
     * @param token the access token from Authorization header
     * @return user information including userId, email, username, roles
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        log.info("Get current user request");

        try {
            String jwtToken = token.startsWith("Bearer ")
                ? token.substring(7)
                : token;

            Map<String, Object> userInfo = authService.getCurrentUser(jwtToken);
            log.info("Current user information retrieved successfully");
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            log.error("Failed to get current user", e);
            throw e;
        }
    }

    /**
     * Health check endpoint for authentication service
     * @return service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Authentication Service",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        log.error("Validation error: {}", errors);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "status", "error",
                "message", "Validation failed",
                "errors", errors
            ));
    }

    /**
     * Exception handler for authentication errors
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex) {

        log.error("Bad credentials error: {}", ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Map.of(
                "status", "error",
                "message", "Invalid email or password"
            ));
    }

    /**
     * Exception handler for user already exists errors
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An error occurred";

        if (ex.getMessage().contains("already registered") ||
            ex.getMessage().contains("already exists")) {
            status = HttpStatus.CONFLICT;
            message = ex.getMessage();
        } else if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            message = ex.getMessage();
        } else if (ex.getMessage().contains("Invalid") ||
                   ex.getMessage().contains("token")) {
            status = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage();
        }

        return ResponseEntity
            .status(status)
            .body(Map.of(
                "status", "error",
                "message", message
            ));
    }
}
