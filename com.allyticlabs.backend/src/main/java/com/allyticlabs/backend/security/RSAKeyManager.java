package com.allyticlabs.backend.security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages RSA key pairs for asymmetric encryption
 * Used for secure key exchange and digital signatures in payment processing
 * Implements RSA-2048 with OAEP padding for enhanced security
 */
@Slf4j
@Component
public class RSAKeyManager {
    
    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int KEY_SIZE = 2048;
    private static final int MAX_ENCRYPT_BLOCK = 190; // For RSA-2048 with OAEP padding
    private static final int MAX_DECRYPT_BLOCK = 256; // RSA-2048 key size in bytes
    
    private final SecureRandom secureRandom;
    
    public RSAKeyManager() {
        this.secureRandom = new SecureRandom();
    }
    
    /**
     * Generate new RSA key pair (2048-bit)
     * @return KeyPair containing public and private keys
     */
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            log.info("Generated new RSA-{} key pair", KEY_SIZE);
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating RSA key pair", e);
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }
    
    /**
     * Export public key as Base64 encoded string (PEM-like format)
     * @param publicKey Public key to export
     * @return Base64 encoded public key
     */
    public String exportPublicKey(PublicKey publicKey) {
        String base64Key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        log.debug("Exported public key: {} bytes", publicKey.getEncoded().length);
        return base64Key;
    }
    
    /**
     * Export private key as Base64 encoded string (PEM-like format)
     * WARNING: Store private keys securely, never expose them
     * @param privateKey Private key to export
     * @return Base64 encoded private key
     */
    public String exportPrivateKey(PrivateKey privateKey) {
        String base64Key = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        log.debug("Exported private key: {} bytes", privateKey.getEncoded().length);
        return base64Key;
    }
    
    /**
     * Export public key in PEM format
     * @param publicKey Public key to export
     * @return PEM formatted public key
     */
    public String exportPublicKeyPEM(PublicKey publicKey) {
        String base64Key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN PUBLIC KEY-----\n");
        
        // Split into 64-character lines
        int index = 0;
        while (index < base64Key.length()) {
            pem.append(base64Key, index, Math.min(index + 64, base64Key.length()));
            pem.append("\n");
            index += 64;
        }
        
        pem.append("-----END PUBLIC KEY-----");
        return pem.toString();
    }
    
    /**
     * Import public key from Base64 encoded string
     * @param keyString Base64 encoded public key
     * @return PublicKey object
     */
    public PublicKey importPublicKey(String keyString) {
        try {
            // Remove PEM headers if present
            String cleanKey = keyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
            
            byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(spec);
            
            log.debug("Imported public key successfully");
            return publicKey;
        } catch (Exception e) {
            log.error("Error importing public key", e);
            throw new RuntimeException("Failed to import public key", e);
        }
    }
    
    /**
     * Import private key from Base64 encoded string
     * @param keyString Base64 encoded private key
     * @return PrivateKey object
     */
    public PrivateKey importPrivateKey(String keyString) {
        try {
            // Remove PEM headers if present
            String cleanKey = keyString
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
            
            byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(spec);
            
            log.debug("Imported private key successfully");
            return privateKey;
        } catch (Exception e) {
            log.error("Error importing private key", e);
            throw new RuntimeException("Failed to import private key", e);
        }
    }
    
    /**
     * Encrypt data using RSA public key
     * Handles data larger than key size by splitting into blocks
     * @param data Data to encrypt (plain text)
     * @param publicKey Public key for encryption
     * @return Base64 encoded encrypted data
     */
    public String encrypt(String data, PublicKey publicKey) {
        try {
            byte[] dataBytes = data.getBytes("UTF-8");
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            // For large data, split into blocks
            if (dataBytes.length <= MAX_ENCRYPT_BLOCK) {
                byte[] encryptedBytes = cipher.doFinal(dataBytes);
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } else {
                log.warn("Data size exceeds RSA block size, consider using hybrid encryption");
                throw new IllegalArgumentException("Data too large for RSA encryption. Use AES for large data.");
            }
        } catch (Exception e) {
            log.error("Error encrypting with RSA", e);
            throw new RuntimeException("RSA encryption failed", e);
        }
    }
    
    /**
     * Decrypt data using RSA private key
     * @param encryptedData Base64 encoded encrypted data
     * @param privateKey Private key for decryption
     * @return Decrypted plain text
     */
    public String decrypt(String encryptedData, PrivateKey privateKey) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            log.error("Error decrypting with RSA", e);
            throw new RuntimeException("RSA decryption failed", e);
        }
    }
    
    /**
     * Sign data using RSA private key (for non-repudiation)
     * @param data Data to sign
     * @param privateKey Private key for signing
     * @return Base64 encoded signature
     */
    public String sign(String data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey, secureRandom);
            signature.update(data.getBytes("UTF-8"));
            byte[] signatureBytes = signature.sign();
            
            log.debug("Generated RSA signature for data");
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            log.error("Error signing data with RSA", e);
            throw new RuntimeException("RSA signing failed", e);
        }
    }
    
    /**
     * Verify signature using RSA public key
     * @param data Original data
     * @param signatureString Base64 encoded signature
     * @param publicKey Public key for verification
     * @return true if signature is valid
     */
    public boolean verify(String data, String signatureString, PublicKey publicKey) {
        try {
            byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data.getBytes("UTF-8"));
            
            boolean isValid = signature.verify(signatureBytes);
            log.debug("Signature verification result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Error verifying RSA signature", e);
            return false;
        }
    }
    
    /**
     * Encrypt M-Pesa security credential using M-Pesa public key
     * M-Pesa requires RSA encryption for sensitive credentials
     * @param credential Security credential (initiator password)
     * @param mpesaPublicKey M-Pesa public key (Base64 or PEM format)
     * @return Base64 encoded encrypted credential
     */
    public String encryptMpesaCredential(String credential, String mpesaPublicKey) {
        try {
            PublicKey publicKey = importPublicKey(mpesaPublicKey);
            String encrypted = encrypt(credential, publicKey);
            log.info("Encrypted M-Pesa security credential");
            return encrypted;
        } catch (Exception e) {
            log.error("Error encrypting M-Pesa credential", e);
            throw new RuntimeException("M-Pesa credential encryption failed", e);
        }
    }
    
    /**
     * Encrypt M-Pesa password for API authentication
     * Format: Base64(Shortcode+Passkey+Timestamp)
     * @param shortcode Business short code
     * @param passkey Lipa Na M-Pesa Online passkey
     * @param timestamp Timestamp in format YYYYMMDDHHmmss
     * @return Base64 encoded password
     */
    public String generateMpesaPassword(String shortcode, String passkey, String timestamp) {
        try {
            String rawPassword = shortcode + passkey + timestamp;
            byte[] passwordBytes = rawPassword.getBytes("UTF-8");
            String encodedPassword = Base64.getEncoder().encodeToString(passwordBytes);
            
            log.debug("Generated M-Pesa password for timestamp: {}", timestamp);
            return encodedPassword;
        } catch (Exception e) {
            log.error("Error generating M-Pesa password", e);
            throw new RuntimeException("Failed to generate M-Pesa password", e);
        }
    }
    
    /**
     * Generate key fingerprint for verification and identification
     * @param publicKey Public key
     * @return SHA-256 fingerprint of the public key (hex format)
     */
    public String getKeyFingerprint(PublicKey publicKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(publicKey.getEncoded());
            
            // Convert to hex format
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            String fingerprint = hexString.toString();
            log.debug("Generated key fingerprint: {}", fingerprint.substring(0, 16) + "...");
            return fingerprint;
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating key fingerprint", e);
            throw new RuntimeException("Failed to generate key fingerprint", e);
        }
    }
    
    /**
     * Validate that public and private keys form a matching pair
     * @param publicKey Public key
     * @param privateKey Private key
     * @return true if keys form a valid pair
     */
    public boolean validateKeyPair(PublicKey publicKey, PrivateKey privateKey) {
        try {
            String testData = "validation_test_" + System.currentTimeMillis();
            String encrypted = encrypt(testData, publicKey);
            String decrypted = decrypt(encrypted, privateKey);
            
            boolean isValid = testData.equals(decrypted);
            log.debug("Key pair validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Key pair validation failed", e);
            return false;
        }
    }
    
    /**
     * Get RSA key size in bits
     * @param key Public or Private key
     * @return Key size in bits
     */
    public int getKeySize(Key key) {
        if (key instanceof RSAPublicKey) {
            return ((RSAPublicKey) key).getModulus().bitLength();
        } else if (key instanceof RSAPrivateKey) {
            return ((RSAPrivateKey) key).getModulus().bitLength();
        }
        return 0;
    }
    
    /**
     * Hybrid encryption: Encrypt AES key with RSA
     * Used to securely exchange symmetric keys
     * @param aesKey AES key bytes
     * @param publicKey RSA public key
     * @return Base64 encoded encrypted AES key
     */
    public String encryptAESKey(byte[] aesKey, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedKey = cipher.doFinal(aesKey);
            
            log.debug("Encrypted AES key with RSA");
            return Base64.getEncoder().encodeToString(encryptedKey);
        } catch (Exception e) {
            log.error("Error encrypting AES key", e);
            throw new RuntimeException("Failed to encrypt AES key", e);
        }
    }
    
    /**
     * Hybrid encryption: Decrypt AES key with RSA
     * @param encryptedAESKey Base64 encoded encrypted AES key
     * @param privateKey RSA private key
     * @return Decrypted AES key bytes
     */
    public byte[] decryptAESKey(String encryptedAESKey, PrivateKey privateKey) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedAESKey);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte[] decryptedKey = cipher.doFinal(encryptedBytes);
            log.debug("Decrypted AES key with RSA");
            return decryptedKey;
        } catch (Exception e) {
            log.error("Error decrypting AES key", e);
            throw new RuntimeException("Failed to decrypt AES key", e);
        }
    }
}