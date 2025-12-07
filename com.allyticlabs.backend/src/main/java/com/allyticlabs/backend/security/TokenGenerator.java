package com.allyticlabs.backend.security;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.time.Instant;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;

/**
 * Generates secure tokens for payment transactions, QR codes, and API authentication
 */
@Slf4j
@Component
public class TokenGenerator {
    
    private final SecureRandom secureRandom;
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_TOKEN_LENGTH = 32;
    
    public TokenGenerator() {
        this.secureRandom = new SecureRandom();
    }
    
    /**
     * Generate a cryptographically secure random token
     * @return Base64 encoded token
     */
    public String generateSecureToken() {
        byte[] randomBytes = new byte[DEFAULT_TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * Generate a secure token with specified length
     * @param length Length of random bytes
     * @return Base64 encoded token
     */
    public String generateSecureToken(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * Generate transaction ID with timestamp and randomness
     * Format: TXN-{timestamp}-{random}
     * @return Transaction ID
     */
    public String generateTransactionId() {
        long timestamp = System.currentTimeMillis();
        String randomPart = generateAlphanumericToken(8);
        return String.format("TXN-%d-%s", timestamp, randomPart);
    }
    
    /**
     * Generate payment reference number
     * Format: PAY-{timestamp}-{random}
     * @return Payment reference
     */
    public String generatePaymentReference() {
        long timestamp = System.currentTimeMillis();
        String randomPart = generateAlphanumericToken(10);
        return String.format("PAY-%d-%s", timestamp, randomPart);
    }
    
    /**
     * Generate QR code payment token with expiry
     * @param expiryMinutes Minutes until expiry
     * @return QR token with embedded expiry
     */
    public String generateQRPaymentToken(int expiryMinutes) {
        long expiryTimestamp = Instant.now().plusSeconds(expiryMinutes * 60).toEpochMilli();
        String randomToken = generateSecureToken(24);
        return String.format("%s:%d", randomToken, expiryTimestamp);
    }
    
    /**
     * Generate M-Pesa transaction token
     * Format: MPX-{uuid}-{short-random}
     * @return M-Pesa transaction token
     */
    public String generateMpesaToken() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String random = generateAlphanumericToken(6);
        return String.format("MPX-%s-%s", uuid, random);
    }
    
    /**
     * Generate Stripe payment intent ID
     * Format: PI-{timestamp}-{random}
     * @return Stripe payment intent identifier
     */
    public String generateStripePaymentIntent() {
        long timestamp = System.currentTimeMillis();
        String randomPart = generateAlphanumericToken(16);
        return String.format("PI-%d-%s", timestamp, randomPart);
    }
    
    /**
     * Generate API key for merchant integration
     * @return Secure API key
     */
    public String generateApiKey() {
        byte[] randomBytes = new byte[48];
        secureRandom.nextBytes(randomBytes);
        return "sk_live_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * Generate webhook secret for signature verification
     * @return Webhook secret
     */
    public String generateWebhookSecret() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return "whsec_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * Generate alphanumeric token (uppercase letters and numbers only)
     * @param length Length of token
     * @return Alphanumeric token
     */
    public String generateAlphanumericToken(int length) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(ALPHANUMERIC.length());
            token.append(ALPHANUMERIC.charAt(index));
        }
        return token.toString();
    }
    
    /**
     * Generate one-time payment code
     * Format: 6-digit numeric code
     * @return OTP code
     */
    public String generateOTP() {
        int otp = secureRandom.nextInt(900000) + 100000; // 6-digit number
        return String.valueOf(otp);
    }
    
    /**
     * Generate session token with embedded metadata
     * @param userId User identifier
     * @param deviceId Device identifier
     * @return Session token
     */
    public String generateSessionToken(String userId, String deviceId) {
        long timestamp = System.currentTimeMillis();
        String randomPart = generateSecureToken(16);
        
        // Combine userId hash, deviceId hash, timestamp, and random
        String metadata = String.format("%d:%s:%s:%s", 
            timestamp, 
            hashString(userId), 
            hashString(deviceId), 
            randomPart
        );
        
        return Base64.getUrlEncoder().withoutPadding().encodeToString(metadata.getBytes());
    }
    
    /**
     * Generate idempotency key for preventing duplicate transactions
     * @return Idempotency key
     */
    public String generateIdempotencyKey() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generate merchant reference number
     * @param merchantId Merchant identifier
     * @return Merchant reference
     */
    public String generateMerchantReference(String merchantId) {
        long timestamp = System.currentTimeMillis();
        String random = generateAlphanumericToken(8);
        return String.format("MER-%s-%d-%s", merchantId, timestamp, random);
    }
    
    /**
     * Generate QR code identifier
     * Format: QR-{timestamp}-{random}
     * @return QR code identifier
     */
    public String generateQRCodeId() {
        long timestamp = System.currentTimeMillis();
        String random = generateAlphanumericToken(12);
        return String.format("QR-%d-%s", timestamp, random);
    }
    
    /**
     * Generate refund reference
     * @param originalTransactionId Original transaction ID
     * @return Refund reference
     */
    public String generateRefundReference(String originalTransactionId) {
        String random = generateAlphanumericToken(8);
        return String.format("REF-%s-%s", originalTransactionId, random);
    }
    
    /**
     * Validate token format and expiry for QR payment tokens
     * @param token QR payment token
     * @return true if token is valid and not expired
     */
    public boolean validateQRToken(String token) {
        try {
            String[] parts = token.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            long expiryTimestamp = Long.parseLong(parts[1]);
            return System.currentTimeMillis() < expiryTimestamp;
        } catch (Exception e) {
            log.error("Error validating QR token", e);
            return false;
        }
    }
    
    /**
     * Extract expiry timestamp from QR token
     * @param token QR payment token
     * @return Expiry timestamp in milliseconds
     */
    public long extractExpiryFromQRToken(String token) {
        try {
            String[] parts = token.split(":");
            if (parts.length == 2) {
                return Long.parseLong(parts[1]);
            }
        } catch (Exception e) {
            log.error("Error extracting expiry from token", e);
        }
        return 0;
    }
    
    /**
     * Generate nonce for preventing replay attacks
     * @return Nonce value
     */
    public String generateNonce() {
        byte[] nonceBytes = new byte[16];
        secureRandom.nextBytes(nonceBytes);
        long timestamp = System.currentTimeMillis();
        
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES + nonceBytes.length);
        buffer.putLong(timestamp);
        buffer.put(nonceBytes);
        
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }
    
    /**
     * Simple hash function for token generation
     * @param input Input string
     * @return Hash string
     */
    private String hashString(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 8);
        } catch (Exception e) {
            log.error("Error hashing string", e);
            return generateAlphanumericToken(8);
        }
    }

    /**
     * Generate TOTP secret for QR codes
     */
    public String generateTOTPSecret() {
        byte[] secretBytes = new byte[20];
        secureRandom.nextBytes(secretBytes);
        return java.util.Base64.getEncoder().encodeToString(secretBytes);
    }

    /**
     * Generate TOTP code from secret
     */
    public String generateTOTP(String secret) {
        long timestamp = System.currentTimeMillis() / 30000;
        return String.format("%06d", Math.abs((secret + timestamp).hashCode() % 1000000));
    }

    /**
     * Validate TOTP code
     */
    public boolean validateTOTP(String secret, String code) {
        String expectedCode = generateTOTP(secret);
        return expectedCode.equals(code);
    }

}