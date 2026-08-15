#!/bin/bash

cd ~/Documents/Desktop/Bella-Technologies/com.bellatechnologies.backend

echo "Fixing corrupted files..."

# Fix 1: MpesaConfig - Remove duplicate methods appended outside class
echo "1. Fixing MpesaConfig..."
# Find the line before the duplicate methods and keep only up to there
LINE=$(grep -n "^    public String getQueryUrl()" src/main/java/com/bellatechnologies/backend/config/MpesaConfig.java | head -1 | cut -d: -f1)
if [ ! -z "$LINE" ]; then
    head -n $((LINE - 1)) src/main/java/com/bellatechnologies/backend/config/MpesaConfig.java > /tmp/MpesaConfig.java
    
    # Add the methods properly inside the class
    cat >> /tmp/MpesaConfig.java << 'MPESA'
    
    public String getQueryUrl() {
        return baseUrl + "/mpesa/stkpushquery/v1/query";
    }
    
    public String getRegisterUrlEndpoint() {
        return baseUrl + "/mpesa/c2b/v1/registerurl";
    }
    
    public String getSimulateUrl() {
        return baseUrl + "/mpesa/c2b/v1/simulate";
    }
    
    public String getBalanceUrl() {
        return baseUrl + "/mpesa/accountbalance/v1/query";
    }
    
    public String getReversalUrl() {
        return baseUrl + "/mpesa/reversal/v1/request";
    }
}
MPESA
    mv /tmp/MpesaConfig.java src/main/java/com/bellatechnologies/backend/config/MpesaConfig.java
fi

# Fix 2: StripePaymentRepository - It's an interface, remove the implementation
echo "2. Fixing StripePaymentRepository..."
cat > src/main/java/com/bellatechnologies/backend/repository/StripePaymentRepository.java << 'STRIPE_REPO'
package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.StripePayment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StripePaymentRepository {
    StripePayment save(StripePayment payment);
    Optional<StripePayment> findById(String id);
    Optional<StripePayment> findByPaymentIntentId(String paymentIntentId);
    Optional<StripePayment> findByStripePaymentIntentId(String paymentIntentId);
    List<StripePayment> findByPaymentId(String paymentId);
    List<StripePayment> findAll(int limit);
    void delete(String id);
}
STRIPE_REPO

# Fix 3: PaymentService - Fix the import statements that got prepended
echo "3. Fixing PaymentService..."
# Get line where package declaration starts
LINE=$(grep -n "^package com.bellatechnologies" src/main/java/com/bellatechnologies/backend/service/PaymentService.java | head -1 | cut -d: -f1)
if [ ! -z "$LINE" ]; then
    tail -n +$LINE src/main/java/com/bellatechnologies/backend/service/PaymentService.java > /tmp/PaymentService_temp.java
    
    # Add proper imports at the top
    cat > /tmp/PaymentService.java << 'IMPORTS'
// ============================================================================
// File: service/PaymentService.java
// ============================================================================
IMPORTS
    cat /tmp/PaymentService_temp.java >> /tmp/PaymentService.java
    
    # Now add the missing imports after package declaration
    sed -i '/^package/a\
\
import java.security.MessageDigest;\
import java.nio.charset.StandardCharsets;\
import java.util.UUID;\
import java.time.Instant;\
import java.math.BigDecimal;' /tmp/PaymentService.java
    
    mv /tmp/PaymentService.java src/main/java/com/bellatechnologies/backend/service/PaymentService.java
fi

# Fix 4: StripeService - Remove duplicate import
echo "4. Fixing StripeService..."
# Remove the line that starts with "import com.stripe.net.Webhook;" if it appears twice
awk '!seen[$0]++ || !/^import com.stripe.net.Webhook;/' src/main/java/com/bellatechnologies/backend/service/StripeService.java > /tmp/StripeService.java
mv /tmp/StripeService.java src/main/java/com/bellatechnologies/backend/service/StripeService.java

# Fix 5: StripeConfig - Remove the duplicate closing brace
echo "5. Fixing StripeConfig..."
# Remove lines with just "//" followed by closing brace
sed -i '/^[ ]*\/\/ Stripe\./d' src/main/java/com/bellatechnologies/backend/config/StripeConfig.java

echo ""
echo "All files fixed!"
echo ""
echo "Now run: mvn clean package -DskipTests"

