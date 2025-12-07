package com.allyticlabs.backend.dto;
import com.allyticlabs.backend.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    // Payment ID (generated or provided)
    private String paymentId;

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
    private String merchantName;
    private String callbackUrl;
    private String returnUrl;

    // Customer information
    private String customerEmail;
    private String customerPhone;

    // Additional metadata
    private Map<String, String> metadata;

    // Security fields
    private String clientIpAddress;
    private String ipAddress;
    private String userAgent;
    private String deviceId;
    private String deviceFingerprint;
    private String sessionId;

    // Idempotency key to prevent duplicate payments
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    // Timestamp for request validation
    private Long timestamp;

    // Request signature for additional security
    private String signature;

    // Helper methods for backward compatibility
    public String getIpAddress() {
        return ipAddress != null ? ipAddress : clientIpAddress;
    }
}
