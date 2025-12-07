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
 * DTO for QR code payment requests (similar to WeChat Pay QR system)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRPaymentRequest {
    
    // Mode: "MERCHANT_SCAN" (customer shows QR) or "CUSTOMER_SCAN" (merchant shows QR)
    @NotBlank(message = "QR payment mode is required")
    @Pattern(regexp = "^(MERCHANT_SCAN|CUSTOMER_SCAN)$", message = "Invalid QR payment mode")
    private String qrMode;
    
    // For MERCHANT_SCAN: customer's QR code
    private String customerQrCode;
    private String qrCodeId;
    
    // For CUSTOMER_SCAN: merchant details
    @NotBlank(message = "Merchant ID is required")
    private String merchantId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter code")
    private String currency;
    
    // User information
    private String userId;
    private String customerId;
    
    // Underlying payment method for QR (M-Pesa or Stripe)
    @NotNull(message = "Underlying payment method is required")
    private PaymentMethod underlyingPaymentMethod;
    
    // For M-Pesa QR payments
    private String phoneNumber;
    
    // For Stripe QR payments
    private String stripeCustomerId;
    private String stripePaymentMethodId;
    
    // Transaction details
    private String orderId;
    private String description;
    private String storeId;
    private String storeName;
    
    // QR code security
    @NotBlank(message = "QR token is required")
    private String qrToken;
    
    private String encryptedPayload;
    private Long qrGeneratedTime;
    private Integer qrExpirySeconds;
    
    // Device and location information
    private String deviceId;
    private String deviceType;
    private String geoLocation;
    private String ipAddress;
    
    // Additional metadata
    private Map<String, String> metadata;
    
    // Callback URLs
    private String callbackUrl;
    private String notificationUrl;
    
    // Security fields
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
    
    private Long timestamp;
    private String signature;
    
    // For dynamic QR codes (amount can be entered by merchant)
    private Boolean isDynamicAmount;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}