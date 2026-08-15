#!/bin/bash
set -e

echo "============================================"
echo "Fixing broken import insertions..."
echo "============================================"

# List of all files with import issues
FILES=(
    "src/main/java/com/bellatechnologies/backend/repository/WebhookRepository.java"
    "src/main/java/com/bellatechnologies/backend/service/PaymentValidationService.java"
    "src/main/java/com/bellatechnologies/backend/repository/NewsletterRepository.java"
    "src/main/java/com/bellatechnologies/backend/repository/StripePaymentRepository.java"
    "src/main/java/com/bellatechnologies/backend/service/NewsletterService.java"
    "src/main/java/com/bellatechnologies/backend/service/DroneService.java"
    "src/main/java/com/bellatechnologies/backend/repository/PaymentRepository.java"
    "src/main/java/com/bellatechnologies/backend/service/PaymentService.java"
    "src/main/java/com/bellatechnologies/backend/repository/SolarPanelRepository.java"
    "src/main/java/com/bellatechnologies/backend/service/ContactService.java"
    "src/main/java/com/bellatechnologies/backend/service/SolarPanelService.java"
    "src/main/java/com/bellatechnologies/backend/service/MpesaService.java"
    "src/main/java/com/bellatechnologies/backend/service/RobotService.java"
    "src/main/java/com/bellatechnologies/backend/service/WebhookVerificationService.java"
    "src/main/java/com/bellatechnologies/backend/repository/QRPaymentRepository.java"
    "src/main/java/com/bellatechnologies/backend/repository/TransactionRepository.java"
    "src/main/java/com/bellatechnologies/backend/repository/RobotRepository.java"
    "src/main/java/com/bellatechnologies/backend/repository/MpesaPaymentRepository.java"
    "src/main/java/com/bellatechnologies/backend/service/PaymentEncryptionService.java"
    "src/main/java/com/bellatechnologies/backend/repository/ContactRepository.java"
    "src/main/java/com/bellatechnologies/backend/repository/DroneRepository.java"
    "src/main/java/com/bellatechnologies/backend/service/QRCodeService.java"
    "src/main/java/com/bellatechnologies/backend/service/StripeService.java"
)

for FILE in "${FILES[@]}"; do
    if [ -f "$FILE" ]; then
        echo "Fixing $FILE..."
        
        # Extract package line
        PACKAGE_LINE=$(head -1 "$FILE")
        
        # Check if line 2 has an import that should come after package
        LINE2=$(sed -n '2p' "$FILE")
        
        if [[ "$LINE2" == import* ]]; then
            # The import was inserted before the package declaration
            # We need to move it after the package line
            
            # Create temp file with correct structure
            {
                echo "$PACKAGE_LINE"
                echo ""
                tail -n +2 "$FILE" | grep "^import " | sort -u
                echo ""
                tail -n +2 "$FILE" | grep -v "^import "
            } > "${FILE}.tmp"
            
            mv "${FILE}.tmp" "$FILE"
            echo "  ✓ Fixed $(basename $FILE)"
        fi
    fi
done

# Fix StripeConfig.java specifically (line 83 issue)
FILE="src/main/java/com/bellatechnologies/backend/config/StripeConfig.java"
if [ -f "$FILE" ]; then
    # Check for the problematic line
    if grep -q "^import.*83.*" "$FILE" 2>/dev/null; then
        echo "Fixing StripeConfig.java line 83..."
        # Restore from backup
        cp "backup/config/StripeConfig.java" "$FILE"
        
        # Re-apply only the valid fixes
        sed -i 's/Stripe\.maxNetworkRetries/\/\/ Stripe.maxNetworkRetries/g' "$FILE"
        sed -i 's/Stripe\.apiVersion/\/\/ Stripe.apiVersion/g' "$FILE"
        
        echo "  ✓ StripeConfig.java restored and fixed"
    fi
fi

echo ""
echo "============================================"
echo "Testing compilation..."
echo "============================================"
echo ""

mvn clean compile -DskipTests

