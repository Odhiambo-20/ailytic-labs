package com.allyticlabs.backend.config;

import com.stripe.Stripe;
import com.stripe.net.RequestOptions;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "stripe")
@Data
public class StripeConfig {

    private String publicKey;
    private String secretKey;
    private String webhookSecret;
    private String apiVersion = "2023-10-16";
    private int maxNetworkRetries = 2;
    private int connectTimeout = 30000;
    private int readTimeout = 80000;
    private String currency = "USD";
    private String defaultPaymentMethod = "card";
    private boolean captureAutomatically = true;
    private String statementDescriptor = "Payment Service";
    private String statementDescriptorSuffix;
    private String webhookEndpoint;
    private long webhookTolerance = 300;
    private boolean enableApplePay = true;
    private boolean enableGooglePay = true;
    private boolean enablePaymentIntents = true;
    private boolean enableSetupIntents = false;
    private boolean enableAutomaticTax = false;
    private String[] paymentMethodTypes = {"card", "us_bank_account"};
    private String successUrl;
    private String cancelUrl;
    private String mode = "payment";
    private boolean saveCustomer = true;
    private boolean attachPaymentMethod = true;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Stripe secret key is not configured");
        }

        Stripe.apiKey = secretKey;

        try {
            java.lang.reflect.Field retriesField = Stripe.class.getDeclaredField("maxNetworkRetries");
            retriesField.setAccessible(true);
            retriesField.set(null, maxNetworkRetries);
        } catch (Exception e) {
            System.out.println("Warning: Could not set maxNetworkRetries: " + e.getMessage());
        }

        System.out.println("Stripe initialized successfully with API version: " + apiVersion);
    }

    public RequestOptions createRequestOptions(String idempotencyKey) {
        return RequestOptions.builder()
                .setApiKey(secretKey)
                .setIdempotencyKey(idempotencyKey)
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .build();
    }

    @Bean
    public RequestOptions getDefaultRequestOptions() {
        return RequestOptions.builder()
                .setApiKey(secretKey)
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .build();
    }

    public Map<String, Object> getPaymentIntentParams(Long amount, String currency, String customerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency != null ? currency : this.currency);
        params.put("automatic_payment_methods", Map.of("enabled", true));

        if (customerId != null && !customerId.isEmpty()) {
            params.put("customer", customerId);
        }

        if (statementDescriptor != null) {
            params.put("statement_descriptor", statementDescriptor);
        }

        if (statementDescriptorSuffix != null) {
            params.put("statement_descriptor_suffix", statementDescriptorSuffix);
        }

        params.put("capture_method", captureAutomatically ? "automatic" : "manual");

        return params;
    }

    public Map<String, Object> getCheckoutSessionParams(
            Long amount,
            String currency,
            String successUrl,
            String cancelUrl) {

        Map<String, Object> params = new HashMap<>();
        params.put("mode", mode);
        params.put("success_url", successUrl != null ? successUrl : this.successUrl);
        params.put("cancel_url", cancelUrl != null ? cancelUrl : this.cancelUrl);

        Map<String, Object> priceData = new HashMap<>();
        priceData.put("currency", currency != null ? currency : this.currency);
        priceData.put("unit_amount", amount);
        priceData.put("product_data", Map.of("name", "Payment"));

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price_data", priceData);
        lineItem.put("quantity", 1);

        params.put("line_items", new Object[]{lineItem});
        params.put("payment_method_types", paymentMethodTypes);

        return params;
    }

    public Map<String, Object> getCustomerParams(String email, String name, String phone) {
        Map<String, Object> params = new HashMap<>();

        if (email != null && !email.isEmpty()) {
            params.put("email", email);
        }

        if (name != null && !name.isEmpty()) {
            params.put("name", name);
        }

        if (phone != null && !phone.isEmpty()) {
            params.put("phone", phone);
        }

        return params;
    }

    public String getWebhookSecret() {
        if (webhookSecret == null || webhookSecret.isEmpty()) {
            throw new IllegalStateException("Stripe webhook secret is not configured");
        }
        return webhookSecret;
    }

    public boolean isConfigurationValid() {
        return secretKey != null && !secretKey.isEmpty() &&
               publicKey != null && !publicKey.isEmpty() &&
               webhookSecret != null && !webhookSecret.isEmpty();
    }

    public Long convertToSmallestUnit(double amount) {
        return Math.round(amount * 100);
    }

    public double convertFromSmallestUnit(Long amount) {
        return amount / 100.0;
    }

    public String[] getEnabledPaymentMethods() {
        return paymentMethodTypes;
    }

    public String getMaskedConfig() {
        return String.format(
            "StripeConfig[publicKey=%s, secretKey=%s, currency=%s, apiVersion=%s]",
            publicKey != null ? publicKey.substring(0, Math.min(7, publicKey.length())) + "***" : "null",
            secretKey != null ? "sk_***" : "null",
            currency,
            apiVersion
        );
    }

    public String getDashboardUrl() {
        if (secretKey != null && secretKey.startsWith("sk_live_")) {
            return "https://dashboard.stripe.com";
        } else {
            return "https://dashboard.stripe.com/test";
        }
    }
}