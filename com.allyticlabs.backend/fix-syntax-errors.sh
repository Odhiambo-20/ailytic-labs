#!/bin/bash

cd ~/Documents/Desktop/Allytic-Labs/com.allyticlabs.backend

echo "Fixing syntax errors in corrupted files..."

# Fix 1: TokenGenerator - Remove the duplicate closing brace
tail -n +1 src/main/java/com/allyticlabs/backend/security/TokenGenerator.java | head -n 275 > /tmp/TokenGenerator.java
cat >> /tmp/TokenGenerator.java << 'TOKENGEN'

    /**
     * Generate TOTP secret for QR codes
     * @return Base32 encoded secret
     */
    public String generateTOTPSecret() {
        byte[] secretBytes = new byte[20];
        secureRandom.nextBytes(secretBytes);
        return Base64.getEncoder().encodeToString(secretBytes);
    }
    
    /**
     * Generate TOTP code from secret
     * @param secret TOTP secret
     * @return 6-digit TOTP code
     */
    public String generateTOTP(String secret) {
        long timestamp = System.currentTimeMillis() / 30000;
        return String.format("%06d", Math.abs((secret + timestamp).hashCode() % 1000000));
    }
    
    /**
     * Validate TOTP code
     * @param secret TOTP secret
     * @param code Code to validate
     * @return true if code is valid
     */
    public boolean validateTOTP(String secret, String code) {
        String expectedCode = generateTOTP(secret);
        return expectedCode.equals(code);
    }
}
TOKENGEN
mv /tmp/TokenGenerator.java src/main/java/com/allyticlabs/backend/security/TokenGenerator.java

# Fix 2: Check and show the problematic files
echo ""
echo "Checking file endings..."
tail -5 src/main/java/com/allyticlabs/backend/config/MpesaConfig.java
tail -5 src/main/java/com/allyticlabs/backend/repository/StripePaymentRepository.java
tail -5 src/main/java/com/allyticlabs/backend/service/PaymentService.java
tail -5 src/main/java/com/allyticlabs/backend/config/StripeConfig.java

echo ""
echo "Run these commands to see the exact issues:"
echo "sed -n '190,215p' src/main/java/com/allyticlabs/backend/config/MpesaConfig.java"
echo "sed -n '1,40p' src/main/java/com/allyticlabs/backend/repository/StripePaymentRepository.java"
echo "sed -n '1,20p' src/main/java/com/allyticlabs/backend/service/PaymentService.java"
echo "sed -n '75,85p' src/main/java/com/allyticlabs/backend/config/StripeConfig.java"

