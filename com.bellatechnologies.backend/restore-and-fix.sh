#!/bin/bash
set -e

echo "============================================"
echo "Restoring from backup and applying fixes properly..."
echo "============================================"

# Restore all files from backup
echo "Restoring files from backup..."
cp -r backup/service/* src/main/java/com/bellatechnologies/backend/service/
cp -r backup/repository/* src/main/java/com/bellatechnologies/backend/repository/
cp -r backup/config/* src/main/java/com/bellatechnologies/backend/config/

echo "✓ Files restored"
echo ""

# Now apply fixes correctly using perl instead of sed
echo "Applying fixes with proper syntax..."

# Fix QRCodeService.java
FILE="src/main/java/com/bellatechnologies/backend/service/QRCodeService.java"
perl -i -pe 's/\.setAmount\(request\.getAmount\(\)\)/.setAmount(request.getAmount().toString())/g' "$FILE"
perl -i -pe 's/throw new InvalidQRCodeException\("([^"]*)"\);/throw new InvalidQRCodeException("$1", InvalidQRCodeException.QRErrorReason.INVALID_FORMAT, null);/g' "$FILE"
perl -i -pe 's/findByMerchantId\(merchantId, limit\)/findByMerchantId(merchantId)/g' "$FILE"
echo "✓ QRCodeService.java"

# Fix QRPaymentRepository.java
FILE="src/main/java/com/bellatechnologies/backend/repository/QRPaymentRepository.java"
perl -i -pe 's/\.setCreatedAt\(Instant\.now\(\)\)/.setCreatedAt(Instant.now().toString())/g' "$FILE"
perl -i -pe 's/\.setUpdatedAt\(Instant\.now\(\)\)/.setUpdatedAt(Instant.now().toString())/g' "$FILE"
perl -i -pe 's/Instant\.parse\(item\.get\("createdAt"\)\.s\(\)\)/item.get("createdAt").s()/g' "$FILE"
perl -i -pe 's/Instant\.parse\(item\.get\("expiresAt"\)\.s\(\)\)/item.get("expiresAt").s()/g' "$FILE"
perl -i -pe 's/\.mapToDouble\(QRPayment::getAmount\)/.mapToDouble(qr -> Double.parseDouble(qr.getAmount()))/g' "$FILE"
echo "✓ QRPaymentRepository.java"

# Fix PaymentRepository.java
FILE="src/main/java/com/bellatechnologies/backend/repository/PaymentRepository.java"
perl -i -pe 's/\.setCreatedAt\(Instant\.now\(\)\)/.setCreatedAt(Instant.now().toString())/g' "$FILE"
perl -i -pe 's/\.setUpdatedAt\(Instant\.now\(\)\)/.setUpdatedAt(Instant.now().toString())/g' "$FILE"
perl -i -pe 's/Instant\.parse\(item\.get\("createdAt"\)\.s\(\)\)/item.get("createdAt").s()/g' "$FILE"
perl -i -pe 's/\.mapToDouble\(Payment::getAmount\)/.mapToDouble(p -> Double.parseDouble(p.getAmount()))/g' "$FILE"
echo "✓ PaymentRepository.java"

# Fix TransactionRepository.java
FILE="src/main/java/com/bellatechnologies/backend/repository/TransactionRepository.java"
perl -i -pe 's/\.setCreatedAt\(Instant\.now\(\)\)/.setCreatedAt(Instant.now().toString())/g' "$FILE"
perl -i -pe 's/Instant\.parse\(item\.get\("createdAt"\)\.s\(\)\)/item.get("createdAt").s()/g' "$FILE"
echo "✓ TransactionRepository.java"

# Fix MpesaService.java
FILE="src/main/java/com/bellatechnologies/backend/service/MpesaService.java"
perl -i -pe 's/\.setAmount\(amount\)/.setAmount(amount.toString())/g' "$FILE"
echo "✓ MpesaService.java"

# Fix StripeService.java
FILE="src/main/java/com/bellatechnologies/backend/service/StripeService.java"
perl -i -pe 's/\.setAmount\(new BigDecimal\(intent\.getAmount\(\)\)\)/.setAmount(String.valueOf(intent.getAmount()))/g' "$FILE"
perl -i -pe 's/new BigDecimal\(intent\.getAmountReceived\(\)\)/String.valueOf(intent.getAmountReceived())/g' "$FILE"
echo "✓ StripeService.java"

# Fix PaymentService.java
FILE="src/main/java/com/bellatechnologies/backend/service/PaymentService.java"
perl -i -pe 's/\.setAmount\(amount\)/.setAmount(amount.toString())/g' "$FILE"
echo "✓ PaymentService.java"

# Fix WebhookVerificationService.java
FILE="src/main/java/com/bellatechnologies/backend/service/WebhookVerificationService.java"
perl -i -pe 's/validateStripeSignature\(payload, signature, secret\)/validateStripeSignature(payload, signature, secret, System.currentTimeMillis())/g' "$FILE"
echo "✓ WebhookVerificationService.java"

# Fix StripeConfig.java
FILE="src/main/java/com/bellatechnologies/backend/config/StripeConfig.java"
perl -i -pe 's/Stripe\.maxNetworkRetries/\/\/ Stripe.maxNetworkRetries/g' "$FILE"
perl -i -pe 's/Stripe\.apiVersion/\/\/ Stripe.apiVersion/g' "$FILE"
echo "✓ StripeConfig.java"

# Fix MpesaConfig.java
FILE="src/main/java/com/bellatechnologies/backend/config/MpesaConfig.java"
perl -i -pe 's/return sandbox(?!\.)/return this.sandbox/g' "$FILE"
echo "✓ MpesaConfig.java"

# Fix PaymentVerificationException.java
FILE="src/main/java/com/bellatechnologies/backend/exception/PaymentVerificationException.java"
perl -i -pe 's/new PaymentVerificationException\(message, null, reason, details\)/new PaymentVerificationException(message, reason, details)/g' "$FILE"
echo "✓ PaymentVerificationException.java"

echo ""
echo "============================================"
echo "Testing compilation..."
echo "============================================"
echo ""

mvn clean compile -DskipTests

