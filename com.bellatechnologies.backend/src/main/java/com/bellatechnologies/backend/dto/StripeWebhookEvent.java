package com.bellatechnologies.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for Stripe webhook events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeWebhookEvent {

    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object; // "event"

    @JsonProperty("api_version")
    private String apiVersion;

    @JsonProperty("created")
    private Long created;

    @JsonProperty("data")
    private EventData data;

    @JsonProperty("livemode")
    private Boolean livemode;

    @JsonProperty("pending_webhooks")
    private Integer pendingWebhooks;

    @JsonProperty("request")
    private EventRequest request;

    @JsonProperty("type")
    private String type; // e.g., "payment_intent.succeeded"

    // Raw event JSON for logging/audit
    private String rawEventJson;

    /**
     * Event Data containing the actual object
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventData {

        @JsonProperty("object")
        private Map<String, Object> object;

        @JsonProperty("previous_attributes")
        private Map<String, Object> previousAttributes;
    }

    /**
     * Event Request information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventRequest {

        @JsonProperty("id")
        private String id;

        @JsonProperty("idempotency_key")
        private String idempotencyKey;
    }

    /**
     * Helper methods to extract common fields
     */

    public String getPaymentIntentId() {
        if (data != null && data.getObject() != null) {
            Object id = data.getObject().get("id");
            return id != null ? id.toString() : null;
        }
        return null;
    }

    public String getChargeId() {
        if (data != null && data.getObject() != null) {
            Object id = data.getObject().get("id");
            if (type != null && type.contains("charge")) {
                return id != null ? id.toString() : null;
            }
            // For payment_intent events, extract charge from charges object
            Object charges = data.getObject().get("charges");
            if (charges instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> chargesMap = (Map<String, Object>) charges;
                Object dataObj = chargesMap.get("data");
                if (dataObj instanceof java.util.List && !((java.util.List<?>) dataObj).isEmpty()) {
                    Object firstCharge = ((java.util.List<?>) dataObj).get(0);
                    if (firstCharge instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> chargeMap = (Map<String, Object>) firstCharge;
                        Object chargeId = chargeMap.get("id");
                        return chargeId != null ? chargeId.toString() : null;
                    }
                }
            }
        }
        return null;
    }

    public Long getAmount() {
        if (data != null && data.getObject() != null) {
            Object amount = data.getObject().get("amount");
            if (amount instanceof Number) {
                return ((Number) amount).longValue();
            }
        }
        return null;
    }

    public String getCurrency() {
        if (data != null && data.getObject() != null) {
            Object currency = data.getObject().get("currency");
            return currency != null ? currency.toString() : null;
        }
        return null;
    }

    public String getStatus() {
        if (data != null && data.getObject() != null) {
            Object status = data.getObject().get("status");
            return status != null ? status.toString() : null;
        }
        return null;
    }

    public Map<String, String> getMetadata() {
        if (data != null && data.getObject() != null) {
            Object metadata = data.getObject().get("metadata");
            if (metadata instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> metadataMap = (Map<String, String>) metadata;
                return metadataMap;
            }
        }
        return null;
    }

    public boolean isPaymentSucceeded() {
        return "payment_intent.succeeded".equals(type) ||
               "charge.succeeded".equals(type);
    }

    public boolean isPaymentFailed() {
        return "payment_intent.payment_failed".equals(type) ||
               "charge.failed".equals(type);
    }
}
