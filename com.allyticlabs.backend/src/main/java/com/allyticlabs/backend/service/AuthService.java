package com.allyticlabs.backend.service;

import com.allyticlabs.backend.dto.AuthRequest;
import com.allyticlabs.backend.dto.AuthResponse;
import com.allyticlabs.backend.dto.RegisterRequest;
import com.allyticlabs.backend.model.User;
import com.allyticlabs.backend.repository.UserRepository;
import com.allyticlabs.backend.security.CustomUserDetails;
import com.allyticlabs.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authenticating user: {}", request.getUsername());

        // Authenticate with email and password
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Update last login time
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        log.info("User authenticated successfully: {}", userDetails.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationInSeconds())
                .userId(userDetails.getUserId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .message("Login successful")
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.error("Password confirmation does not match for user: {}", request.getEmail());
            throw new RuntimeException("Passwords do not match");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email already registered: {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        // Generate username from email if not provided
        String username = request.getUsername() != null ?
                request.getUsername() :
                request.getEmail().split("@")[0];

        // Check if username is taken
        if (userRepository.existsByUsername(username)) {
            username = username + "_" + UUID.randomUUID().toString().substring(0, 8);
            log.info("Username was taken, generated new username: {}", username);
        }

        // Create new user
        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .provider("local")
                .roles(Arrays.asList("ROLE_USER"))
                .enabled(true)
                .emailVerified(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        userRepository.save(user);
        log.info("User saved to database: {}", user.getUserId());

        // Generate tokens
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        log.info("User registered successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationInSeconds())
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .message("Registration successful")
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");

        String email = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtUtil.generateToken(userDetails);

            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

            log.info("Token refreshed successfully for user: {}", email);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationInSeconds())
                    .userId(customUserDetails.getUserId())
                    .username(userDetails.getUsername())
                    .email(customUserDetails.getEmail())
                    .build();
        }

        log.error("Invalid refresh token");
        throw new RuntimeException("Invalid refresh token");
    }

    public void logout(String token) {
        // TODO: Implement token blacklisting if needed
        log.info("User logged out");
    }

    public Map<String, Object> validateToken(String token) {
        boolean isValid = jwtUtil.validateToken(token);
        String email = isValid ? jwtUtil.extractUsername(token) : null;

        return Map.of(
            "valid", isValid,
            "email", email != null ? email : ""
        );
    }

    public Map<String, Object> getCurrentUser(String token) {
        String email = jwtUtil.extractUsername(token);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        return Map.of(
            "userId", userDetails.getUserId(),
            "email", userDetails.getEmail(),
            "username", userDetails.getUsername(),
            "firstName", userDetails.getFirstName(),
            "lastName", userDetails.getLastName(),
            "roles", userDetails.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList())
        );
    }
}
