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

/**
 * Configuration class for Stripe Payment Integration
 * Handles Stripe API initialization and settings
 */
@Configuration
@ConfigurationProperties(prefix = "stripe")
@Data
public class StripeConfig {

    // Stripe API Keys
    private String publicKey;
    private String secretKey;
    private String webhookSecret;
    
    // API Settings
    private String apiVersion = "2023-10-16"; // Latest Stripe API version
    private int maxNetworkRetries = 2;
    private int connectTimeout = 30000; // 30 seconds
    private int readTimeout = 80000; // 80 seconds (Stripe recommendation)
    
    // Payment Settings
    private String currency = "USD"; // Default currency
    private String defaultPaymentMethod = "card";
    private boolean captureAutomatically = true;
    private String statementDescriptor = "Payment Service";
    private String statementDescriptorSuffix;
    
    // Webhook Settings
    private String webhookEndpoint;
    private long webhookTolerance = 300; // 5 minutes tolerance for webhook timestamps
    
    // Feature Flags
    private boolean enableApplePay = true;
    private boolean enableGooglePay = true;
    private boolean enablePaymentIntents = true;
    private boolean enableSetupIntents = false;
    private boolean enableAutomaticTax = false;
    
    // Payment Method Types
    private String[] paymentMethodTypes = {"card", "us_bank_account"};
    
    // Checkout Settings
    private String successUrl;
    private String cancelUrl;
    private String mode = "payment"; // payment, setup, or subscription
    
    // Customer Settings
    private boolean saveCustomer = true;
    private boolean attachPaymentMethod = true;
    
    /**
     * Initialize Stripe SDK with API key
     */
    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Stripe secret key is not configured");
        }
        
        Stripe.apiKey = secretKey;
        Stripe.maxNetworkRetries = maxNetworkRetries;
        
        // Set API version if specified
        if (apiVersion != null && !apiVersion.isEmpty()) {
            Stripe.apiVersion = apiVersion;
        }
        
        System.out.println("Stripe initialized successfully with API version: " + 
                          (Stripe.apiVersion != null ? Stripe.apiVersion : "default"));
    }
    
    /**
     * Create RequestOptions for Stripe API calls
     * Includes idempotency key for safe retries
     * @param idempotencyKey Unique key for idempotent requests
     * @return RequestOptions configured with API key and idempotency
     */
    public RequestOptions createRequestOptions(String idempotencyKey) {
        return RequestOptions.builder()
                .setApiKey(secretKey)
                .setIdempotencyKey(idempotencyKey)
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .build();
    }
    
    /**
     * Get default RequestOptions without idempotency key
     * @return Default RequestOptions
     */
    @Bean
    public RequestOptions getDefaultRequestOptions() {
        return RequestOptions.builder()
                .setApiKey(secretKey)
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .build();
    }
    
    /**
     * Get payment intent creation parameters
     * @param amount Amount in smallest currency unit (cents for USD)
     * @param currency Currency code
     * @param customerId Stripe customer ID
     * @return Map of parameters for PaymentIntent creation
     */
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
    
    /**
     * Get checkout session creation parameters
     * @param amount Amount in smallest currency unit
     * @param currency Currency code
     * @param successUrl Success redirect URL
     * @param cancelUrl Cancel redirect URL
     * @return Map of parameters for CheckoutSession creation
     */
    public Map<String, Object> getCheckoutSessionParams(
            Long amount, 
            String currency, 
            String successUrl, 
            String cancelUrl) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("mode", mode);
        params.put("success_url", successUrl != null ? successUrl : this.successUrl);
        params.put("cancel_url", cancelUrl != null ? cancelUrl : this.cancelUrl);
        
        // Line items
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
    
    /**
     * Get customer creation parameters
     * @param email Customer email
     * @param name Customer name
     * @param phone Customer phone
     * @return Map of parameters for Customer creation
     */
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
    
    /**
     * Validate webhook signature
     * @return Webhook secret for signature validation
     */
    public String getWebhookSecret() {
        if (webhookSecret == null || webhookSecret.isEmpty()) {
            throw new IllegalStateException("Stripe webhook secret is not configured");
        }
        return webhookSecret;
    }
    
    /**
     * Check if configuration is valid
     * @return true if all required fields are set
     */
    public boolean isConfigurationValid() {
        return secretKey != null && !secretKey.isEmpty() &&
               publicKey != null && !publicKey.isEmpty() &&
               webhookSecret != null && !webhookSecret.isEmpty();
    }
    
    /**
     * Convert amount to smallest currency unit (cents)
     * @param amount Amount in major units (e.g., dollars)
     * @return Amount in smallest units (e.g., cents)
     */
    public Long convertToSmallestUnit(double amount) {
        return Math.round(amount * 100);
    }
    
    /**
     * Convert amount from smallest currency unit
     * @param amount Amount in smallest units (e.g., cents)
     * @return Amount in major units (e.g., dollars)
     */
    public double convertFromSmallestUnit(Long amount) {
        return amount / 100.0;
    }
    
    /**
     * Get payment method types as array
     * @return Array of enabled payment method types
     */
    public String[] getEnabledPaymentMethods() {
        return paymentMethodTypes;
    }
    
    /**
     * Mask sensitive configuration data for logging
     * @return Masked configuration string
     */
    public String getMaskedConfig() {
        return String.format(
            "StripeConfig[publicKey=%s, secretKey=%s, currency=%s, apiVersion=%s]",
            publicKey != null ? publicKey.substring(0, Math.min(7, publicKey.length())) + "***" : "null",
            secretKey != null ? "sk_***" : "null",
            currency,
            apiVersion
        );
    }
    
    /**
     * Get Stripe dashboard URL based on environment
     * @return Dashboard URL
     */
    public String getDashboardUrl() {
        if (secretKey != null && secretKey.startsWith("sk_live_")) {
            return "https://dashboard.stripe.com";
        } else {
            return "https://dashboard.stripe.com/test";
        }
    }
}