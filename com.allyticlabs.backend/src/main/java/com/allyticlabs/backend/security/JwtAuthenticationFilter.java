package com.allyticlabs.backend.security;

import com.allyticlabs.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * Intercepts requests to validate JWT tokens and set authentication in SecurityContext
 *
 * This filter processes JWT tokens when present and always continues the filter chain,
 * allowing Spring Security's SecurityFilterChain to determine access control based on
 * the authentication state and endpoint configuration.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");

        log.debug("Processing request to: {} | Auth header present: {}",
                 path, authHeader != null && authHeader.startsWith("Bearer "));

        // Only process JWT if Authorization header is present
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Extract JWT token
                final String jwt = authHeader.substring(7);
                final String userEmail = jwtUtil.extractUsername(jwt);

                log.debug("JWT token found for user: {}", userEmail);

                // Validate token and set authentication if not already set
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("JWT token validated successfully for user: {} on path: {}",
                                 userEmail, path);
                    } else {
                        log.warn("Invalid JWT token for user: {} on path: {}", userEmail, path);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing JWT token for path {}: {}", path, e.getMessage());
                // Don't throw exception - let Spring Security handle unauthorized access
            }
        } else {
            log.debug("No Bearer token in request to: {} - will be handled by SecurityFilterChain", path);
        }

        // Always continue the filter chain
        // Spring Security's SecurityFilterChain will determine if the request should be allowed
        // based on the authentication state and endpoint configuration
        filterChain.doFilter(request, response);
    }
}
