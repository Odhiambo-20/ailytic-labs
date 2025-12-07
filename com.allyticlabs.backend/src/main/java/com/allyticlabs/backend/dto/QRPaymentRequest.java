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
public class QRPaymentRequest {

    @NotBlank(message = "QR payment mode is required")
    @Pattern(regexp = "^(MERCHANT_SCAN|CUSTOMER_SCAN)$", message = "Invalid QR payment mode")
    private String qrMode;

    private String customerQrCode;
    private String qrCodeId;

    @NotBlank(message = "Merchant ID is required")
    private String merchantId;

    private String merchantName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter code")
    private String currency;

    private String userId;
    private String customerId;

    @NotNull(message = "Underlying payment method is required")
    private PaymentMethod underlyingPaymentMethod;

    private String phoneNumber;

    private String stripeCustomerId;
    private String stripePaymentMethodId;

    private String orderId;
    private String description;
    private String storeId;
    private String storeName;

    @NotBlank(message = "QR token is required")
    private String qrToken;

    private String encryptedPayload;
    private Long qrGeneratedTime;
    private Integer qrExpirySeconds;
    private Integer expiryMinutes;

    private String deviceId;
    private String deviceType;
    private String geoLocation;
    private String ipAddress;

    private Map<String, String> metadata;

    private String callbackUrl;
    private String notificationUrl;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    private Long timestamp;
    private String signature;

    private Boolean isDynamicAmount;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    // QR code usage limits
    private Integer maxScans;
    private Boolean singleUse;

    // Helper method for expiry
    public Integer getExpiryMinutes() {
        if (expiryMinutes != null) {
            return expiryMinutes;
        }
        if (qrExpirySeconds != null) {
            return qrExpirySeconds / 60;
        }
        return 15; // Default 15 minutes
    }
}
