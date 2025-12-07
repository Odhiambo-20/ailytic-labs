package com.allyticlabs.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive Security Configuration for Payment System
 * Implements industry-standard security measures for payment processing
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ConfigurationProperties(prefix = "payment.security")
@Data
public class PaymentSecurityConfig {

    // Encryption Settings
    private String encryptionAlgorithm = "AES/GCM/NoPadding";
    private int encryptionKeySize = 256;
    private int gcmTagLength = 128;
    private int gcmIvLength = 12;
    
    // Hashing Settings
    private String hashAlgorithm = "SHA-256";
    private String hmacAlgorithm = "HmacSHA256";
    
    // RSA Settings for M-Pesa
    private int rsaKeySize = 2048;
    private String rsaAlgorithm = "RSA/ECB/PKCS1Padding";
    
    // JWT Settings
    private String jwtSecret;
    private long jwtExpirationMs = 86400000; // 24 hours
    private String jwtIssuer = "payment-service";
    
    // API Security
    private List<String> allowedOrigins = Arrays.asList("http://localhost:3000", "https://yourdomain.com");
    private List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");
    private List<String> allowedHeaders = Arrays.asList("*");
    private boolean allowCredentials = true;
    private long maxAge = 3600L;
    
    // Rate Limiting
    private int rateLimitPerMinute = 60;
    private int rateLimitPerHour = 1000;
    private boolean enableRateLimiting = true;
    
    // Webhook Security
    private long webhookTimestampTolerance = 300000; // 5 minutes in milliseconds
    private boolean validateWebhookSignature = true;
    
    // IP Whitelisting
    private List<String> whitelistedIps;
    private boolean enableIpWhitelisting = false;
    
    // PCI DSS Compliance Settings
    private boolean maskSensitiveData = true;
    private boolean enableAuditLogging = true;
    private int passwordMinLength = 12;
    private boolean requireStrongPasswords = true;
    
    // Session Settings
    private int sessionTimeout = 1800; // 30 minutes in seconds
    private boolean useSecureCookies = true;
    private String cookieSameSite = "Strict";
    
    /**
     * Configure HTTP Security with payment-specific rules
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/api/webhooks/**",  // Webhooks from M-Pesa and Stripe
                    "/api/payments/callback/**"  // Callback URLs
                )
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/webhooks/**").permitAll()
                .requestMatchers("/api/payments/callback/**").permitAll()
                .requestMatchers("/api/health", "/api/status").permitAll()
                
                // QR code generation (requires authentication)
                .requestMatchers("/api/qr/generate").authenticated()
                
                // Payment endpoints (requires authentication)
                .requestMatchers("/api/payments/**").authenticated()
                .requestMatchers("/api/mpesa/**").authenticated()
                .requestMatchers("/api/stripe/**").authenticated()
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; frame-ancestors 'none'; form-action 'self'")
                )
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentTypeOptions(contentType -> contentType.disable())
            );
        
        return http.build();
    }
    
    /**
     * Configure CORS for cross-origin requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Password encoder for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // BCrypt with strength 12
    }
    
    /**
     * Generate AES SecretKey for encryption
     */
    @Bean(name = "aesSecretKey")
    public SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(encryptionKeySize);
        return keyGenerator.generateKey();
    }
    
    /**
     * Generate RSA KeyPair for M-Pesa communication
     */
    @Bean(name = "rsaKeyPair")
    public KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(rsaKeySize);
        return keyPairGenerator.generateKeyPair();
    }
    
    /**
     * Get MessageDigest for hashing
     */
    @Bean
    public MessageDigest messageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(hashAlgorithm);
    }
    
    /**
     * SecureRandom for generating cryptographically strong random values
     */
    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
    
    /**
     * Validate if IP address is whitelisted
     */
    public boolean isIpWhitelisted(String ipAddress) {
        if (!enableIpWhitelisting) {
            return true;
        }
        
        if (whitelistedIps == null || whitelistedIps.isEmpty()) {
            return true;
        }
        
        return whitelistedIps.contains(ipAddress);
    }
    
    /**
     * Check if timestamp is within acceptable tolerance
     */
    public boolean isTimestampValid(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long difference = Math.abs(currentTime - timestamp);
        return difference <= webhookTimestampTolerance;
    }
    
    /**
     * Generate secure random token
     */
    public String generateSecureToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] token = new byte[length];
        random.nextBytes(token);
        return bytesToHex(token);
    }
    
    /**
     * Convert byte array to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Mask sensitive data for logging (PCI DSS compliance)
     */
    public String maskSensitiveData(String data) {
        if (!maskSensitiveData || data == null || data.length() <= 4) {
            return data;
        }
        
        int visibleChars = 4;
        int totalLength = data.length();
        String masked = "*".repeat(totalLength - visibleChars);
        
        return masked + data.substring(totalLength - visibleChars);
    }
    
    /**
     * Mask card number (PCI DSS Level 1 compliance)
     */
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 13) {
            return "****";
        }
        
        // Show only last 4 digits
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
    
    /**
     * Validate password strength
     */
    public boolean isPasswordStrong(String password) {
        if (!requireStrongPasswords) {
            return password != null && password.length() >= passwordMinLength;
        }
        
        if (password == null || password.length() < passwordMinLength) {
            return false;
        }
        
        // Check for uppercase, lowercase, digit, and special character
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
    
    /**
     * Get security headers for responses
     */
    public java.util.Map<String, String> getSecurityHeaders() {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        headers.put("X-Content-Type-Options", "nosniff");
        headers.put("X-Frame-Options", "DENY");
        headers.put("X-XSS-Protection", "1; mode=block");
        headers.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        headers.put("Content-Security-Policy", "default-src 'self'");
        headers.put("Referrer-Policy", "strict-origin-when-cross-origin");
        headers.put("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        
        return headers;
    }
    
    /**
     * Calculate request signature for API authentication
     */
    public String calculateSignature(String data, String secret) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance(hmacAlgorithm);
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
            secret.getBytes(), hmacAlgorithm
        );
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        return bytesToHex(hash);
    }
    
    /**
     * Validate request signature
     */
    public boolean validateSignature(String data, String signature, String secret) {
        try {
            String calculatedSignature = calculateSignature(data, secret);
            return MessageDigest.isEqual(
                signature.getBytes(), 
                calculatedSignature.getBytes()
            );
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get masked configuration for logging
     */
    public String getMaskedConfig() {
        return String.format(
            "PaymentSecurityConfig[encryption=%s, keySize=%d, rateLimitPerMin=%d, auditLogging=%s]",
            encryptionAlgorithm,
            encryptionKeySize,
            rateLimitPerMinute,
            enableAuditLogging
        );
    }
}