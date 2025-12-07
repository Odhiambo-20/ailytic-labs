#!/bin/bash
set -e

echo "============================================"
echo "Fixing all compilation errors..."
echo "============================================"

# Function to add import if not exists
add_import() {
    local file=$1
    local import=$2
    if [ -f "$file" ]; then
        if ! grep -q "import $import;" "$file"; then
            sed -i "/^package/a import $import;" "$file"
            echo "  ✓ Added import $import to $(basename $file)"
        fi
    fi
}

echo ""
echo "Step 1: Fixing QRCodeService.java"
echo "-----------------------------------"
FILE="src/main/java/com/allyticlabs/backend/service/QRCodeService.java"

# Add missing imports
add_import "$FILE" "java.nio.charset.StandardCharsets"

# Fix type conversions - BigDecimal to String
sed -i 's/\.setAmount(request\.getAmount())/.setAmount(request.getAmount().toString())/g' "$FILE"
sed -i 's/BigDecimal amount =/String amount =/g' "$FILE"
sed -i 's/new BigDecimal(item\.get("amount")\.n())/item.get("amount").n()/g' "$FILE"

# Fix InvalidQRCodeException calls - add proper parameters
sed -i 's/throw new InvalidQRCodeException("\([^"]*\)");/throw new InvalidQRCodeException("\1", InvalidQRCodeException.QRErrorReason.INVALID_FORMAT, null);/g' "$FILE"

# Fix findByMerchantId call - remove extra parameter
sed -i 's/findByMerchantId(merchantId, limit)/findByMerchantId(merchantId)/g' "$FILE"

echo "  ✓ QRCodeService.java fixed"

echo ""
echo "Step 2: Fixing Repository classes"
echo "-----------------------------------"

# Fix QRPaymentRepository
FILE="src/main/java/com/allyticlabs/backend/repository/QRPaymentRepository.java"
add_import "$FILE" "software.amazon.awssdk.services.dynamodb.model.AttributeValue"
add_import "$FILE" "software.amazon.awssdk.services.dynamodb.model.GetItemRequest"
add_import "$FILE" "software.amazon.awssdk.services.dynamodb.model.GetItemResponse"

# Fix Instant to String conversions
sed -i 's/\.setCreatedAt(Instant\.now())/\.setCreatedAt(Instant.now().toString())/g' "$FILE"
sed -i 's/\.setUpdatedAt(Instant\.now())/\.setUpdatedAt(Instant.now().toString())/g' "$FILE"

# Fix Instant.parse to just use string
sed -i 's/Instant\.parse(item\.get("createdAt")\.s())/item.get("createdAt").s()/g' "$FILE"
sed -i 's/Instant\.parse(item\.get("expiresAt")\.s())/item.get("expiresAt").s()/g' "$FILE"

# Fix amount method reference
sed -i 's/\.mapToDouble(QRPayment::getAmount)/\.mapToDouble(qr -> Double.parseDouble(qr.getAmount()))/g' "$FILE"

echo "  ✓ QRPaymentRepository.java fixed"

# Fix PaymentRepository
FILE="src/main/java/com/allyticlabs/backend/repository/PaymentRepository.java"
sed -i 's/\.setCreatedAt(Instant\.now())/\.setCreatedAt(Instant.now().toString())/g' "$FILE"
sed -i 's/\.setUpdatedAt(Instant\.now())/\.setUpdatedAt(Instant.now().toString())/g' "$FILE"
sed -i 's/Instant\.parse(item\.get("createdAt")\.s())/item.get("createdAt").s()/g' "$FILE"
sed -i 's/\.mapToDouble(Payment::getAmount)/\.mapToDouble(p -> Double.parseDouble(p.getAmount()))/g' "$FILE"

echo "  ✓ PaymentRepository.java fixed"

# Fix TransactionRepository
FILE="src/main/java/com/allyticlabs/backend/repository/TransactionRepository.java"
sed -i 's/\.setCreatedAt(Instant\.now())/\.setCreatedAt(Instant.now().toString())/g' "$FILE"
sed -i 's/Instant\.parse(item\.get("createdAt")\.s())/item.get("createdAt").s()/g' "$FILE"

echo "  ✓ TransactionRepository.java fixed"

echo ""
echo "Step 3: Fixing Service classes"
echo "-----------------------------------"

# Fix MpesaService
FILE="src/main/java/com/allyticlabs/backend/service/MpesaService.java"
sed -i 's/\.setAmount(amount)/\.setAmount(amount.toString())/g' "$FILE"

echo "  ✓ MpesaService.java fixed"

# Fix StripeService  
FILE="src/main/java/com/allyticlabs/backend/service/StripeService.java"
sed -i 's/\.setAmount(new BigDecimal(intent\.getAmount()))/\.setAmount(String.valueOf(intent.getAmount()))/g' "$FILE"
sed -i 's/new BigDecimal(intent\.getAmountReceived())/String.valueOf(intent.getAmountReceived())/g' "$FILE"

echo "  ✓ StripeService.java fixed"

# Fix PaymentService
FILE="src/main/java/com/allyticlabs/backend/service/PaymentService.java"
add_import "$FILE" "org.springframework.http.HttpHeaders"
add_import "$FILE" "org.springframework.http.HttpEntity"
add_import "$FILE" "org.springframework.http.HttpMethod"
add_import "$FILE" "org.springframework.http.ResponseEntity"
add_import "$FILE" "org.springframework.http.MediaType"
add_import "$FILE" "java.util.Base64"
add_import "$FILE" "com.fasterxml.jackson.databind.JsonNode"

sed -i 's/\.setAmount(amount)/\.setAmount(amount.toString())/g' "$FILE"

echo "  ✓ PaymentService.java fixed"

# Fix WebhookVerificationService
FILE="src/main/java/com/allyticlabs/backend/service/WebhookVerificationService.java"
sed -i 's/validateStripeSignature(payload, signature, secret)/validateStripeSignature(payload, signature, secret, System.currentTimeMillis())/g' "$FILE"

echo "  ✓ WebhookVerificationService.java fixed"

echo ""
echo "Step 4: Fixing Config classes"
echo "-----------------------------------"

# Fix StripeConfig
FILE="src/main/java/com/allyticlabs/backend/config/StripeConfig.java"
sed -i 's/Stripe\.maxNetworkRetries/\/\/ Stripe.maxNetworkRetries/g' "$FILE"
sed -i 's/Stripe\.apiVersion/\/\/ Stripe.apiVersion/g' "$FILE"

echo "  ✓ StripeConfig.java fixed"

# Fix MpesaConfig
FILE="src/main/java/com/allyticlabs/backend/config/MpesaConfig.java"
sed -i 's/return sandbox/return this.sandbox/g' "$FILE"

echo "  ✓ MpesaConfig.java fixed"

# Fix RSAKeyManager
FILE="src/main/java/com/allyticlabs/backend/security/RSAKeyManager.java"
add_import "$FILE" "java.security.interfaces.RSAPublicKey"
add_import "$FILE" "java.security.interfaces.RSAPrivateKey"

echo "  ✓ RSAKeyManager.java fixed"

# Fix PaymentVerificationException
FILE="src/main/java/com/allyticlabs/backend/exception/PaymentVerificationException.java"
sed -i 's/new PaymentVerificationException(message, null, reason, details)/new PaymentVerificationException(message, reason, details)/g' "$FILE"

echo "  ✓ PaymentVerificationException.java fixed"

echo ""
echo "============================================"
echo "All fixes applied! Testing compilation..."
echo "============================================"
echo ""

mvn clean compile -DskipTests

