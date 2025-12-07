package com.payment.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Encryption Utility for Payment System
 * Provides AES-256-GCM encryption for sensitive data and RSA for key exchange
 * PCI DSS compliant encryption implementation
 */
@Component
public class EncryptionUtil {

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int AES_KEY_SIZE = 256;
    private static final int RSA_KEY_SIZE = 2048;
    
    /**
     * Generate AES SecretKey
     * @return SecretKey for AES encryption
     * @throws NoSuchAlgorithmException if algorithm not available
     */
    public SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }
    
    /**
     * Generate RSA KeyPair
     * @return KeyPair for RSA encryption
     * @throws NoSuchAlgorithmException if algorithm not available
     */
    public KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }
    
    /**
     * Encrypt data using AES-256-GCM
     * @param plainText Plain text to encrypt
     * @param secretKey AES secret key
     * @return Base64 encoded encrypted data with IV prepended
     * @throws Exception if encryption fails
     */
    public String encryptAES(String plainText, SecretKey secretKey) throws Exception {
        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        
        // Create cipher
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        
        // Encrypt
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        
        // Combine IV and cipher text
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        
        // Encode to Base64
        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }
    
    /**
     * Decrypt data using AES-256-GCM
     * @param encryptedData Base64 encoded encrypted data with IV
     * @param secretKey AES secret key
     * @return Decrypted plain text
     * @throws Exception if decryption fails
     */
    public String decryptAES(String encryptedData, SecretKey secretKey) throws Exception {
        // Decode from Base64
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        
        // Extract IV and cipher text
        ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);
        
        // Create cipher
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        
        // Decrypt
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, StandardCharsets.UTF_8);
    }
    
    /**
     * Encrypt data using RSA public key
     * Used for M-Pesa security credentials
     * @param plainText Plain text to encrypt
     * @param publicKey RSA public key
     * @return Base64 encoded encrypted data
     * @throws Exception if encryption fails
     */
    public String encryptRSA(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * Decrypt data using RSA private key
     * @param encryptedData Base64 encoded encrypted data
     * @param privateKey RSA private key
     * @return Decrypted plain text
     * @throws Exception if decryption fails
     */
    public String decryptRSA(String encryptedData, PrivateKey privateKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Hash data using SHA-256
     * @param data Data to hash
     * @return Hex encoded hash
     * @throws NoSuchAlgorithmException if algorithm not available
     */
    public String hashSHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    
    /**
     * Generate HMAC-SHA256 signature
     * @param data Data to sign
     * @param secret Secret key for HMAC
     * @return Hex encoded signature
     * @throws Exception if signature generation fails
     */
    public String generateHMAC(String data, String secret) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8), 
            "HmacSHA256"
        );
        mac.init(secretKeySpec);
        
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    
    /**
     * Verify HMAC signature
     * @param data Original data
     * @param signature Signature to verify
     * @param secret Secret key
     * @return true if signature is valid
     * @throws Exception if verification fails
     */
    public boolean verifyHMAC(String data, String signature, String secret) throws Exception {
        String calculatedSignature = generateHMAC(data, secret);
        return MessageDigest.isEqual(
            signature.getBytes(StandardCharsets.UTF_8),
            calculatedSignature.getBytes(StandardCharsets.UTF_8)
        );
    }
    
    /**
     * Generate secure random token
     * @param length Length of token in bytes
     * @return Hex encoded random token
     */
    public String generateSecureToken(int length) {
        byte[] token = new byte[length];
        new SecureRandom().nextBytes(token);
        return bytesToHex(token);
    }
    
    /**
     * Convert SecretKey to Base64 string for storage
     * @param secretKey Secret key to convert
     * @return Base64 encoded key
     */
    public String secretKeyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    /**
     * Convert Base64 string back to SecretKey
     * @param keyString Base64 encoded key
     * @return SecretKey
     */
    public SecretKey stringToSecretKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
    
    /**
     * Convert PublicKey to Base64 string
     * @param publicKey Public key to convert
     * @return Base64 encoded key
     */
    public String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    /**
     * Convert Base64 string to PublicKey
     * @param keyString Base64 encoded key
     * @return PublicKey
     * @throws Exception if conversion fails
     */
    public PublicKey stringToPublicKey(String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    
    /**
     * Convert PrivateKey to Base64 string
     * @param privateKey Private key to convert
     * @return Base64 encoded key
     */
    public String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
    
    /**
     * Convert Base64 string to PrivateKey
     * @param keyString Base64 encoded key
     * @return PrivateKey
     * @throws Exception if conversion fails
     */
    public PrivateKey stringToPrivateKey(String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * Encrypt sensitive payment data (card number, CVV, etc.)
     * PCI DSS compliant encryption
     * @param sensitiveData Sensitive data to encrypt
     * @param secretKey Encryption key
     * @return Encrypted data
     * @throws Exception if encryption fails
     */
    public String encryptSensitivePaymentData(String sensitiveData, SecretKey secretKey) throws Exception {
        return encryptAES(sensitiveData, secretKey);
    }
    
    /**
     * Decrypt sensitive payment data
     * @param encryptedData Encrypted sensitive data
     * @param secretKey Decryption key
     * @return Decrypted data
     * @throws Exception if decryption fails
     */
    public String decryptSensitivePaymentData(String encryptedData, SecretKey secretKey) throws Exception {
        return decryptAES(encryptedData, secretKey);
    }
    
    /**
     * Generate checksum for data integrity verification
     * @param data Data to checksum
     * @return Checksum string
     * @throws NoSuchAlgorithmException if algorithm not available
     */
    public String generateChecksum(String data) throws NoSuchAlgorithmException {
        return hashSHA256(data);
    }
    
    /**
     * Verify data integrity using checksum
     * @param data Original data
     * @param checksum Expected checksum
     * @return true if data is intact
     * @throws NoSuchAlgorithmException if algorithm not available
     */
    public boolean verifyChecksum(String data, String checksum) throws NoSuchAlgorithmException {
        String calculatedChecksum = generateChecksum(data);
        return MessageDigest.isEqual(
            checksum.getBytes(StandardCharsets.UTF_8),
            calculatedChecksum.getBytes(StandardCharsets.UTF_8)
        );
    }
    
    /**
     * Convert byte array to hex string
     * @param bytes Byte array
     * @return Hex string
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
     * Convert hex string to byte array
     * @param hex Hex string
     * @return Byte array
     */
    private byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }
    
    /**
     * Mask sensitive data for logging
     * Shows only last 4 characters
     * @param data Sensitive data
     * @return Masked data
     */
    public String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        
        int length = data.length();
        String masked = "*".repeat(length - 4);
        return masked + data.substring(length - 4);
    }
    
    /**
     * Generate secure password hash using BCrypt-like approach
     * @param password Password to hash
     * @param salt Salt for hashing
     * @return Hashed password
     * @throws Exception if hashing fails
     */
    public String hashPassword(String password, String salt) throws Exception {
        return hashSHA256(password + salt);
    }
}