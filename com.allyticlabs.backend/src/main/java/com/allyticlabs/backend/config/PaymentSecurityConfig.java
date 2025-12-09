package com.allyticlabs.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ConfigurationProperties(prefix = "payment.security")
@Data
public class PaymentSecurityConfig {

    private String encryptionAlgorithm = "AES/GCM/NoPadding";
    private int encryptionKeySize = 256;
    private int gcmTagLength = 128;
    private int gcmIvLength = 12;
    private String hashAlgorithm = "SHA-256";
    private String hmacAlgorithm = "HmacSHA256";
    private int rsaKeySize = 2048;
    private String rsaAlgorithm = "RSA/ECB/PKCS1Padding";
    private String jwtSecret;
    private long jwtExpirationMs = 86400000;
    private String jwtIssuer = "payment-service";
    private List<String> allowedOrigins = Arrays.asList("http://localhost:3000", "https://yourdomain.com");
    private List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");
    private List<String> allowedHeaders = Arrays.asList("*");
    private boolean allowCredentials = true;
    private long maxAge = 3600L;
    private int rateLimitPerMinute = 60;
    private int rateLimitPerHour = 1000;
    private boolean enableRateLimiting = true;
    private long webhookTimestampTolerance = 300000;
    private boolean validateWebhookSignature = true;
    private List<String> whitelistedIps;
    private boolean enableIpWhitelisting = false;
    private boolean maskSensitiveData = true;
    private boolean enableAuditLogging = true;
    private int passwordMinLength = 12;
    private boolean requireStrongPasswords = true;
    private int sessionTimeout = 1800;
    private boolean useSecureCookies = true;
    private String cookieSameSite = "Strict";

    @Bean(name = "aesSecretKey")
    public SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(encryptionKeySize);
        return keyGenerator.generateKey();
    }

    @Bean(name = "rsaKeyPair")
    public KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(rsaKeySize);
        return keyPairGenerator.generateKeyPair();
    }

    @Bean
    public MessageDigest messageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(hashAlgorithm);
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

    public boolean isIpWhitelisted(String ipAddress) {
        if (!enableIpWhitelisting) {
            return true;
        }
        if (whitelistedIps == null || whitelistedIps.isEmpty()) {
            return true;
        }
        return whitelistedIps.contains(ipAddress);
    }

    public boolean isTimestampValid(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long difference = Math.abs(currentTime - timestamp);
        return difference <= webhookTimestampTolerance;
    }

    public String generateSecureToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] token = new byte[length];
        random.nextBytes(token);
        return bytesToHex(token);
    }

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

    public String maskSensitiveData(String data) {
        if (!maskSensitiveData || data == null || data.length() <= 4) {
            return data;
        }
        int visibleChars = 4;
        int totalLength = data.length();
        String masked = "*".repeat(totalLength - visibleChars);
        return masked + data.substring(totalLength - visibleChars);
    }

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 13) {
            return "****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    public boolean isPasswordStrong(String password) {
        if (!requireStrongPasswords) {
            return password != null && password.length() >= passwordMinLength;
        }
        if (password == null || password.length() < passwordMinLength) {
            return false;
        }
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

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

    public String calculateSignature(String data, String secret) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance(hmacAlgorithm);
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
            secret.getBytes(), hmacAlgorithm
        );
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        return bytesToHex(hash);
    }

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
