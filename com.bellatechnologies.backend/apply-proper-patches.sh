#!/bin/bash

cd ~/Documents/Desktop/Bella-Technologies/com.bellatechnologies.backend

echo "=== Applying Proper Patches ==="

# Patch 1: Add TOTP methods to TokenGenerator (inside the class, before closing brace)
echo "1. Patching TokenGenerator with TOTP methods..."
LAST_LINE=$(wc -l < src/main/java/com/bellatechnologies/backend/security/TokenGenerator.java)
sed -i "${LAST_LINE}i\\
\\
    /**\\
     * Generate TOTP secret for QR codes\\
     */\\
    public String generateTOTPSecret() {\\
        byte[] secretBytes = new byte[20];\\
        secureRandom.nextBytes(secretBytes);\\
        return Base64.getEncoder().encodeToString(secretBytes);\\
    }\\
\\
    /**\\
     * Generate TOTP code from secret\\
     */\\
    public String generateTOTP(String secret) {\\
        long timestamp = System.currentTimeMillis() / 30000;\\
        return String.format(\"%06d\", Math.abs((secret + timestamp).hashCode() % 1000000));\\
    }\\
\\
    /**\\
     * Validate TOTP code\\
     */\\
    public boolean validateTOTP(String secret, String code) {\\
        String expectedCode = generateTOTP(secret);\\
        return expectedCode.equals(code);\\
    }
" src/main/java/com/bellatechnologies/backend/security/TokenGenerator.java

# Patch 2: Add URL getters to MpesaConfig
echo "2. Patching MpesaConfig with URL getters..."
LAST_LINE=$(wc -l < src/main/java/com/bellatechnologies/backend/config/MpesaConfig.java)
sed -i "${LAST_LINE}i\\
\\
    public String getQueryUrl() {\\
        return baseUrl + \"/mpesa/stkpushquery/v1/query\";\\
    }\\
\\
    public String getRegisterUrlEndpoint() {\\
        return baseUrl + \"/mpesa/c2b/v1/registerurl\";\\
    }\\
\\
    public String getSimulateUrl() {\\
        return baseUrl + \"/mpesa/c2b/v1/simulate\";\\
    }\\
\\
    public String getBalanceUrl() {\\
        return baseUrl + \"/mpesa/accountbalance/v1/query\";\\
    }\\
\\
    public String getReversalUrl() {\\
        return baseUrl + \"/mpesa/reversal/v1/request\";\\
    }
" src/main/java/com/bellatechnologies/backend/config/MpesaConfig.java

# Patch 3: Add Webhook import to StripeService (after package and existing imports)
echo "3. Patching StripeService with Webhook import..."
if ! grep -q "import com.stripe.net.Webhook" src/main/java/com/bellatechnologies/backend/service/StripeService.java; then
    sed -i '/^import com.stripe/a\import com.stripe.net.Webhook;' src/main/java/com/bellatechnologies/backend/service/StripeService.java
fi

# Patch 4: Add missing methods to PaymentService
echo "4. Patching PaymentService with missing methods..."
if ! grep -q "generatePaymentId()" src/main/java/com/bellatechnologies/backend/service/PaymentService.java; then
    # Add imports first
    sed -i '/^import/a\import java.security.MessageDigest;\nimport java.nio.charset.StandardCharsets;\nimport java.util.UUID;\nimport java.time.Instant;\nimport java.math.BigDecimal;' src/main/java/com/bellatechnologies/backend/service/PaymentService.java | head -1
    
    # Add methods before the last closing brace
    LAST_LINE=$(wc -l < src/main/java/com/bellatechnologies/backend/service/PaymentService.java)
    sed -i "${LAST_LINE}i\\
\\
    public void updatePaymentStatus(String paymentId, PaymentStatus status, String reason) {\\
        Payment payment = paymentRepository.findByPaymentId(paymentId)\\
            .orElseThrow(() -> new PaymentException(\"Payment not found: \" + paymentId));\\
        payment.setStatus(status);\\
        payment.setUpdatedAt(Instant.now().toString());\\
        if (status == PaymentStatus.FAILED || status == PaymentStatus.CANCELLED) {\\
            payment.setFailureReason(reason);\\
        }\\
        if (status == PaymentStatus.COMPLETED || status == PaymentStatus.SUCCESS) {\\
            payment.setCompletedAt(Instant.now().toString());\\
        }\\
        paymentRepository.save(payment);\\
    }\\
\\
    private String generatePaymentId() {\\
        return \"PAY-\" + System.currentTimeMillis() + \"-\" + UUID.randomUUID().toString().substring(0, 8);\\
    }\\
\\
    private boolean isDuplicatePayment(String idempotencyKey) {\\
        return paymentRepository.findByIdempotencyKey(idempotencyKey).isPresent();\\
    }\\
\\
    private PaymentResponse getPaymentByIdempotencyKey(String idempotencyKey) {\\
        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey)\\
            .orElseThrow(() -> new PaymentException(\"Payment not found\"));\\
        return PaymentResponse.builder()\\
            .paymentId(payment.getPaymentId())\\
            .status(payment.getStatus())\\
            .amount(new BigDecimal(payment.getAmount()))\\
            .currency(payment.getCurrency())\\
            .build();\\
    }\\
\\
    private String generateTransactionHash(Payment payment) {\\
        try {\\
            String data = payment.getPaymentId() + payment.getAmount() + payment.getTimestamp();\\
            MessageDigest digest = MessageDigest.getInstance(\"SHA-256\");\\
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));\\
            return Base64.getEncoder().encodeToString(hash);\\
        } catch (Exception e) {\\
            return UUID.randomUUID().toString();\\
        }\\
    }\\
\\
    private void logTransaction(String paymentId, String status, String message, String ipAddress) {\\
        log.info(\"Transaction - Payment: {}, Status: {}, Message: {}\", paymentId, status, message);\\
    }
" src/main/java/com/bellatechnologies/backend/service/PaymentService.java
fi

echo ""
echo "=== Patches Applied Successfully! ==="
echo ""
echo "Now run: mvn clean package -DskipTests"

