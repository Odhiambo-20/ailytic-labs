package com.payment.backend.security;

import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles encryption and decryption of sensitive payment data
 * Uses AES-256-GCM for symmetric encryption
 */
@Slf4j
@Component
public class PaymentEncryption {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int AES_KEY_SIZE = 256;
    
    private final SecureRandom secureRandom;
    
    public PaymentEncryption() {
        this.secureRandom = new SecureRandom();
    }
    
    /**
     * Generate a new AES-256 encryption key
     * @return Base64 encoded key
     */
    public String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(AES_KEY_SIZE, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("Error generating encryption key", e);
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }
    
    /**
     * Encrypt sensitive payment data
     * @param data Plain text data to encrypt
     * @param keyString Base64 encoded encryption key
     * @return Base64 encoded encrypted data with IV prepended
     */
    public String encrypt(String data, String keyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyString);
            SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
            
            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
            
            byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));
            
            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("Error encrypting data", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    /**
     * Decrypt encrypted payment data
     * @param encryptedData Base64 encoded encrypted data with IV
     * @param keyString Base64 encoded encryption key
     * @return Decrypted plain text
     */
    public String decrypt(String encryptedData, String keyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyString);
            SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
            
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            
            // Extract IV and encrypted data
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
            
            byte[] decryptedData = cipher.doFinal(cipherText);
            return new String(decryptedData, "UTF-8");
        } catch (Exception e) {
            log.error("Error decrypting data", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    /**
     * Hash sensitive data using SHA-256
     * @param data Data to hash
     * @return Base64 encoded hash
     */
    public String hash(String data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Error hashing data", e);
            throw new RuntimeException("Hashing failed", e);
        }
    }
    
    /**
     * Encrypt card data specifically (PCI DSS compliant approach)
     * @param cardNumber Card number to encrypt
     * @param key Encryption key
     * @return Encrypted card number
     */
    public String encryptCardData(String cardNumber, String key) {
        // Remove spaces and validate
        String cleanCardNumber = cardNumber.replaceAll("\\s+", "");
        return encrypt(cleanCardNumber, key);
    }
    
    /**
     * Mask sensitive data for logging
     * @param data Sensitive data
     * @return Masked data showing only last 4 characters
     */
    public String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        return "****" + data.substring(data.length() - 4);
    }
}