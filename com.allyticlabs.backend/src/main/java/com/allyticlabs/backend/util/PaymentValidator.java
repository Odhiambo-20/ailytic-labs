package com.allyticlabs.backend.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Payment Validator Utility
 * Validates payment data, card numbers, phone numbers, and other payment-related information
 */
@Component
public class PaymentValidator {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$" // E.164 format
    );
    
    private static final Pattern MPESA_PHONE_PATTERN = Pattern.compile(
        "^254[17]\\d{8}$" // Kenyan M-Pesa format (254700000000 or 254100000000)
    );
    
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile(
        "^[A-Z]{3}$" // ISO 4217 currency codes (USD, KES, EUR, etc.)
    );
    
    // Card number patterns (Luhn algorithm for validation)
    private static final Pattern VISA_PATTERN = Pattern.compile("^4[0-9]{12}(?:[0-9]{3})?$");
    private static final Pattern MASTERCARD_PATTERN = Pattern.compile("^5[1-5][0-9]{14}$");
    private static final Pattern AMEX_PATTERN = Pattern.compile("^3[47][0-9]{13}$");
    private static final Pattern DISCOVER_PATTERN = Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{12}$");
    
    /**
     * Validate email address
     * @param email Email to validate
     * @return true if valid
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number (international format)
     * @param phoneNumber Phone number to validate
     * @return true if valid
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    /**
     * Validate M-Pesa phone number (Kenyan format)
     * @param phoneNumber Phone number to validate
     * @return true if valid M-Pesa number
     */
    public boolean isValidMpesaPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return MPESA_PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    /**
     * Format phone number to M-Pesa format
     * Converts 0712345678 to 254712345678
     * @param phoneNumber Phone number to format
     * @return Formatted phone number
     */
    public String formatToMpesaPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        
        // Remove any non-digit characters
        phoneNumber = phoneNumber.replaceAll("\\D", "");
        
        // If starts with 0, replace with 254
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "254" + phoneNumber.substring(1);
        }
        
        // If doesn't start with 254, add it
        if (!phoneNumber.startsWith("254")) {
            phoneNumber = "254" + phoneNumber;
        }
        
        return phoneNumber;
    }
    
    /**
     * Validate currency code (ISO 4217)
     * @param currencyCode Currency code to validate
     * @return true if valid
     */
    public boolean isValidCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isEmpty()) {
            return false;
        }
        return CURRENCY_CODE_PATTERN.matcher(currencyCode).matches();
    }
    
    /**
     * Validate payment amount
     * @param amount Amount to validate
     * @param minAmount Minimum allowed amount
     * @param maxAmount Maximum allowed amount
     * @return true if valid
     */
    public boolean isValidAmount(double amount, double minAmount, double maxAmount) {
        return amount >= minAmount && amount <= maxAmount && amount > 0;
    }
    
    /**
     * Validate payment amount (default limits)
     * @param amount Amount to validate
     * @return true if valid
     */
    public boolean isValidAmount(double amount) {
        return isValidAmount(amount, 0.01, 1000000.00); // Min: 1 cent, Max: 1 million
    }
    
    /**
     * Validate card number using Luhn algorithm
     * @param cardNumber Card number to validate
     * @return true if valid
     */
    public boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        
        // Remove spaces and dashes
        cardNumber = cardNumber.replaceAll("[\\s-]", "");
        
        // Check if all digits
        if (!cardNumber.matches("\\d+")) {
            return false;
        }
        
        // Check length (13-19 digits)
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }
        
        // Luhn algorithm
        return luhnCheck(cardNumber);
    }
    
    /**
     * Luhn algorithm for card validation
     * @param cardNumber Card number
     * @return true if passes Luhn check
     */
    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10) == 0;
    }
    
    /**
     * Get card type from card number
     * @param cardNumber Card number
     * @return Card type (VISA, MASTERCARD, AMEX, DISCOVER, UNKNOWN)
     */
    public String getCardType(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "UNKNOWN";
        }
        
        cardNumber = cardNumber.replaceAll("[\\s-]", "");
        
        if (VISA_PATTERN.matcher(cardNumber).matches()) {
            return "VISA";
        } else if (MASTERCARD_PATTERN.matcher(cardNumber).matches()) {
            return "MASTERCARD";
        } else if (AMEX_PATTERN.matcher(cardNumber).matches()) {
            return "AMEX";
        } else if (DISCOVER_PATTERN.matcher(cardNumber).matches()) {
            return "DISCOVER";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Validate CVV/CVC code
     * @param cvv CVV to validate
     * @param cardType Card type
     * @return true if valid
     */
    public boolean isValidCVV(String cvv, String cardType) {
        if (cvv == null || cvv.isEmpty()) {
            return false;
        }
        
        // AMEX has 4-digit CVV, others have 3-digit
        if ("AMEX".equals(cardType)) {
            return cvv.matches("\\d{4}");
        } else {
            return cvv.matches("\\d{3}");
        }
    }
    
    /**
     * Validate card expiry date
     * @param expiryMonth Expiry month (1-12)
     * @param expiryYear Expiry year (YYYY)
     * @return true if valid and not expired
     */
    public boolean isValidCardExpiry(int expiryMonth, int expiryYear) {
        if (expiryMonth < 1 || expiryMonth > 12) {
            return false;
        }
        
        Instant now = Instant.now();
        int currentYear = java.time.Year.now().getValue();
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        
        // Card expired
        if (expiryYear < currentYear) {
            return false;
        }
        
        // Same year, check month
        if (expiryYear == currentYear && expiryMonth < currentMonth) {
            return false;
        }
        
        // Card expiry too far in future (more than 10 years)
        if (expiryYear > currentYear + 10) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate transaction reference/ID
     * @param reference Transaction reference
     * @return true if valid
     */
    public boolean isValidTransactionReference(String reference) {
        if (reference == null || reference.isEmpty()) {
            return false;
        }
        
        // Alphanumeric, hyphens, underscores allowed, 5-50 characters
        return reference.matches("^[a-zA-Z0-9_-]{5,50}$");
    }
    
    /**
     * Validate QR code identifier
     * @param qrCode QR code identifier
     * @return true if valid
     */
    public boolean isValidQRCode(String qrCode) {
        if (qrCode == null || qrCode.isEmpty()) {
            return false;
        }
        
        // Must start with PAY_ prefix and be alphanumeric, 10-100 characters
        return qrCode.matches("^PAY_[a-zA-Z0-9]{10,100}$");
    }
    
    /**
     * Validate timestamp is not in future
     * @param timestamp Timestamp to validate
     * @return true if valid
     */
    public boolean isValidTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        
        // Not in future
        if (timestamp > currentTime) {
            return false;
        }
        
        // Not too old (more than 30 days)
        long thirtyDaysAgo = currentTime - (30L * 24 * 60 * 60 * 1000);
        if (timestamp < thirtyDaysAgo) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validate timestamp within tolerance (for webhooks)
     * @param timestamp Timestamp to validate
     * @param toleranceMs Tolerance in milliseconds
     * @return true if within tolerance
     */
    public boolean isTimestampWithinTolerance(long timestamp, long toleranceMs) {
        long currentTime = System.currentTimeMillis();
        long difference = Math.abs(currentTime - timestamp);
        return difference <= toleranceMs;
    }
    
    /**
     * Validate user ID format
     * @param userId User ID
     * @return true if valid
     */
    public boolean isValidUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        
        // UUID or alphanumeric, 8-50 characters
        return userId.matches("^[a-zA-Z0-9-]{8,50}$");
    }
    
    /**
     * Validate merchant ID format
     * @param merchantId Merchant ID
     * @return true if valid
     */
    public boolean isValidMerchantId(String merchantId) {
        if (merchantId == null || merchantId.isEmpty()) {
            return false;
        }
        
        // Alphanumeric, 5-50 characters
        return merchantId.matches("^[a-zA-Z0-9_-]{5,50}$");
    }
    
    /**
     * Validate payment description
     * @param description Description
     * @return true if valid
     */
    public boolean isValidDescription(String description) {
        if (description == null || description.isEmpty()) {
            return false;
        }
        
        // 3-500 characters, allow alphanumeric, spaces, and common punctuation
        return description.matches("^[a-zA-Z0-9\\s.,!?-]{3,500}$");
    }
    
    /**
     * Validate callback URL
     * @param url URL to validate
     * @return true if valid
     */
    public boolean isValidCallbackUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        // Simple URL validation
        return url.matches("^https?://[\\w.-]+(:\\d+)?(/[\\w./-]*)?$");
    }
    
    /**
     * Sanitize input to prevent injection attacks
     * @param input Input to sanitize
     * @return Sanitized input
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"'%;()&+]", "");
    }
    
    /**
     * Validate M-Pesa transaction code
     * @param transactionCode M-Pesa transaction code
     * @return true if valid
     */
    public boolean isValidMpesaTransactionCode(String transactionCode) {
        if (transactionCode == null || transactionCode.isEmpty()) {
            return false;
        }
        
        // M-Pesa codes are typically 10 alphanumeric characters
        return transactionCode.matches("^[A-Z0-9]{10}$");
    }
    
    /**
     * Validate Stripe payment intent ID
     * @param paymentIntentId Stripe payment intent ID
     * @return true if valid
     */
    public boolean isValidStripePaymentIntentId(String paymentIntentId) {
        if (paymentIntentId == null || paymentIntentId.isEmpty()) {
            return false;
        }
        
        // Stripe payment intents start with "pi_"
        return paymentIntentId.matches("^pi_[a-zA-Z0-9]{24,}$");
    }
    
    /**
     * Validate Stripe customer ID
     * @param customerId Stripe customer ID
     * @return true if valid
     */
    public boolean isValidStripeCustomerId(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            return false;
        }
        
        // Stripe customer IDs start with "cus_"
        return customerId.matches("^cus_[a-zA-Z0-9]{14,}$");
    }
    
    /**
     * Check if amount is within M-Pesa transaction limits
     * @param amount Amount in KES
     * @return true if within limits
     */
    public boolean isWithinMpesaLimits(double amount) {
        // M-Pesa limits: Min 1 KES, Max 150,000 KES
        return amount >= 1.0 && amount <= 150000.0;
    }
    
    /**
     * Check if amount is within Stripe limits
     * @param amount Amount in cents
     * @param currency Currency code
     * @return true if within limits
     */
    public boolean isWithinStripeLimits(long amount, String currency) {
        // Stripe minimum charges vary by currency
        // USD: 50 cents minimum
        if ("USD".equals(currency)) {
            return amount >= 50;
        }
        // Add other currencies as needed
        return amount > 0;
    }
}