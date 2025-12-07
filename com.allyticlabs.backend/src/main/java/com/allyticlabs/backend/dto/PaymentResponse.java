package com.allyticlabs.backend.dto;
import com.allyticlabs.backend.model.PaymentMethod;
import com.allyticlabs.backend.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private String paymentId;
    private String transactionId;
    private String orderId;
    
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    
    private BigDecimal amount;
    private String currency;
    
    private String userId;
    private String merchantId;
    
    // QR Code specific
    private String qrCodeUrl;
    private String qrCodeData;
    private Long qrCodeExpiryTime;
    
    // M-Pesa specific
    private String mpesaCheckoutRequestId;
    private String mpesaReceiptNumber;
    private String mpesaTransactionId;
    private String phoneNumber;
    
    // Stripe specific
    private String stripePaymentIntentId;
    private String stripeClientSecret;
    private String stripeChargeId;
    
    // Payment gateway response
    private String gatewayResponse;
    private String gatewayTransactionId;
    private String providerTransactionId;
    
    // Status messages
    private String message;
    private String description;
    private String errorCode;
    private String errorMessage;
    
    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;
    
    // URLs for redirect/callback
    private String redirectUrl;
    private String callbackUrl;
    
    // Additional information
    private Map<String, String> metadata;
    
    // Security
    private String responseSignature;
    private String verificationToken;
    
    // Success flag
    private Boolean success;
    
    // Idempotency tracking
    private String idempotencyKey;
}
