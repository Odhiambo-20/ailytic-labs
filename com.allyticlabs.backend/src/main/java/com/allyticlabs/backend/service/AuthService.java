package com.allyticlabs.backend.service;

import com.allyticlabs.backend.dto.AuthRequest;
import com.allyticlabs.backend.dto.AuthResponse;
import com.allyticlabs.backend.dto.RegisterRequest;
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

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    // private final UserRepository userRepository; // Add when you have User model

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationInSeconds())
                .username(userDetails.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .message("Login successful")
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        // TODO: Implement user registration
        // 1. Check if user exists
        // 2. Create new user
        // 3. Save to database
        // 4. Generate tokens
        
        throw new UnsupportedOperationException("Registration not yet implemented - add User model and repository");
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String newAccessToken = jwtUtil.generateToken(userDetails);
            
            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationInSeconds())
                    .username(userDetails.getUsername())
                    .build();
        }
        
        throw new RuntimeException("Invalid refresh token");
    }

    public void logout(String token) {
        // TODO: Implement token blacklisting if needed
        log.info("User logged out");
    }

    public Map<String, Object> validateToken(String token) {
        boolean isValid = jwtUtil.validateToken(token);
        String username = isValid ? jwtUtil.extractUsername(token) : null;
        
        return Map.of(
            "valid", isValid,
            "username", username != null ? username : ""
        );
    }

    public Map<String, Object> getCurrentUser(String token) {
        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        return Map.of(
            "username", userDetails.getUsername(),
            "roles", userDetails.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList())
        );
    }
}
