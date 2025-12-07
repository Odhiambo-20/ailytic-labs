package com.yourcompany.payment.dto;

import com.yourcompany.payment.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for initiating payment requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter code")
    private String currency;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    // For M-Pesa payments
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    // For Stripe payments
    private String stripePaymentMethodId;
    private String stripeCustomerId;
    
    // For QR payments
    private String qrCodeId;
    private String qrToken;
    
    // Common fields
    private String description;
    private String orderId;
    private String merchantId;
    private String callbackUrl;
    private String returnUrl;
    
    // Additional metadata
    private Map<String, String> metadata;
    
    // Security fields
    private String clientIpAddress;
    private String userAgent;
    private String deviceId;
    private String sessionId;
    
    // Idempotency key to prevent duplicate payments
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
    
    // Timestamp for request validation
    private Long timestamp;
    
    // Request signature for additional security
    private String signature;
}