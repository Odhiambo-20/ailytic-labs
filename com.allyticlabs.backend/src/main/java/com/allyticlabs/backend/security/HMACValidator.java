package com.payment.backend.security;

import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * Validates webhook signatures and generates HMAC signatures
 * for secure payment communication
 */
@Slf4j
@Component
public class HMACValidator {
    
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_SHA512 = "HmacSHA512";
    
    /**
     * Generate HMAC-SHA256 signature
     * @param data Data to sign
     * @param secret Secret key
     * @return Hex encoded signature
     */
    public String generateHMACSHA256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), 
                HMAC_SHA256
            );
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generating HMAC-SHA256", e);
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }
    
    /**
     * Generate HMAC-SHA256 signature (Base64 encoded)
     * @param data Data to sign
     * @param secret Secret key
     * @return Base64 encoded signature
     */
    public String generateHMACSHA256Base64(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), 
                HMAC_SHA256
            );
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generating HMAC-SHA256 Base64", e);
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }
    
    /**
     * Generate HMAC-SHA512 signature for enhanced security
     * @param data Data to sign
     * @param secret Secret key
     * @return Hex encoded signature
     */
    public String generateHMACSHA512(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), 
                HMAC_SHA512
            );
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generating HMAC-SHA512", e);
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }
    
    /**
     * Validate HMAC signature (constant-time comparison)
     * @param data Original data
     * @param signature Signature to validate
     * @param secret Secret key
     * @return true if signature is valid
     */
    public boolean validateHMACSHA256(String data, String signature, String secret) {
        try {
            String computedSignature = generateHMACSHA256(data, secret);
            return constantTimeEquals(signature, computedSignature);
        } catch (Exception e) {
            log.error("Error validating HMAC signature", e);
            return false;
        }
    }
    
    /**
     * Validate HMAC signature with Base64 encoding
     * @param data Original data
     * @param signature Base64 encoded signature
     * @param secret Secret key
     * @return true if signature is valid
     */
    public boolean validateHMACSHA256Base64(String data, String signature, String secret) {
        try {
            String computedSignature = generateHMACSHA256Base64(data, secret);
            return constantTimeEquals(signature, computedSignature);
        } catch (Exception e) {
            log.error("Error validating HMAC Base64 signature", e);
            return false;
        }
    }
    
    /**
     * Validate Stripe webhook signature
     * @param payload Webhook payload
     * @param signature Stripe signature header
     * @param secret Webhook secret
     * @param timestamp Timestamp from header
     * @return true if signature is valid
     */
    public boolean validateStripeSignature(String payload, String signature, 
                                          String secret, long timestamp) {
        try {
            // Stripe uses format: t=timestamp,v1=signature
            String signedPayload = timestamp + "." + payload;
            String computedSignature = generateHMACSHA256(signedPayload, secret);
            
            // Extract v1 signature from header
            String v1Signature = extractSignatureFromHeader(signature, "v1");
            
            return constantTimeEquals(v1Signature, computedSignature);
        } catch (Exception e) {
            log.error("Error validating Stripe signature", e);
            return false;
        }
    }
    
    /**
     * Validate M-Pesa callback signature
     * @param payload Callback payload
     * @param signature M-Pesa signature
     * @param secret Shared secret
     * @return true if signature is valid
     */
    public boolean validateMpesaSignature(String payload, String signature, String secret) {
        try {
            String computedSignature = generateHMACSHA256Base64(payload, secret);
            return constantTimeEquals(signature, computedSignature);
        } catch (Exception e) {
            log.error("Error validating M-Pesa signature", e);
            return false;
        }
    }
    
    /**
     * Generate QR payment signature with expiry
     * @param qrData QR code data
     * @param secret Secret key
     * @param expiryTimestamp Expiry timestamp
     * @return Signature with embedded expiry
     */
    public String generateQRSignature(String qrData, String secret, long expiryTimestamp) {
        String dataWithExpiry = qrData + "|" + expiryTimestamp;
        return generateHMACSHA256(dataWithExpiry, secret);
    }
    
    /**
     * Validate QR payment signature with expiry check
     * @param qrData QR code data
     * @param signature Signature to validate
     * @param secret Secret key
     * @param expiryTimestamp Expiry timestamp
     * @return true if signature is valid and not expired
     */
    public boolean validateQRSignature(String qrData, String signature, 
                                      String secret, long expiryTimestamp) {
        // Check if expired
        if (System.currentTimeMillis() > expiryTimestamp) {
            log.warn("QR code signature expired");
            return false;
        }
        
        String dataWithExpiry = qrData + "|" + expiryTimestamp;
        String computedSignature = generateHMACSHA256(dataWithExpiry, secret);
        return constantTimeEquals(signature, computedSignature);
    }
    
    /**
     * Constant-time string comparison to prevent timing attacks
     * @param a First string
     * @param b Second string
     * @return true if strings are equal
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        
        return MessageDigest.isEqual(aBytes, bBytes);
    }
    
    /**
     * Extract specific signature from Stripe header format
     * @param header Signature header
     * @param version Version to extract (e.g., "v1")
     * @return Extracted signature
     */
    private String extractSignatureFromHeader(String header, String version) {
        String[] parts = header.split(",");
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(version)) {
                return keyValue[1];
            }
        }
        return null;
    }
    
    /**
     * Convert byte array to hex string
     * @param bytes Byte array
     * @return Hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}