package com.yourcompany.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for M-Pesa callback/webhook requests
 * Supports both STK Push callback and C2B validation/confirmation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MpesaCallbackRequest {
    
    // STK Push Callback fields
    @JsonProperty("Body")
    private StkCallbackBody body;
    
    // C2B Callback fields (validation/confirmation)
    @JsonProperty("TransactionType")
    private String transactionType;
    
    @JsonProperty("TransID")
    private String transId;
    
    @JsonProperty("TransTime")
    private String transTime;
    
    @JsonProperty("TransAmount")
    private BigDecimal transAmount;
    
    @JsonProperty("BusinessShortCode")
    private String businessShortCode;
    
    @JsonProperty("BillRefNumber")
    private String billRefNumber;
    
    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("OrgAccountBalance")
    private BigDecimal orgAccountBalance;
    
    @JsonProperty("ThirdPartyTransID")
    private String thirdPartyTransId;
    
    @JsonProperty("MSISDN")
    private String msisdn;
    
    @JsonProperty("FirstName")
    private String firstName;
    
    @JsonProperty("MiddleName")
    private String middleName;
    
    @JsonProperty("LastName")
    private String lastName;
    
    // Raw callback data for logging/audit
    private String rawCallbackData;
    
    /**
     * STK Push Callback Body
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StkCallbackBody {
        
        @JsonProperty("stkCallback")
        private StkCallback stkCallback;
    }
    
    /**
     * STK Callback details
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StkCallback {
        
        @JsonProperty("MerchantRequestID")
        private String merchantRequestId;
        
        @JsonProperty("CheckoutRequestID")
        private String checkoutRequestId;
        
        @JsonProperty("ResultCode")
        private Integer resultCode;
        
        @JsonProperty("ResultDesc")
        private String resultDesc;
        
        @JsonProperty("CallbackMetadata")
        private CallbackMetadata callbackMetadata;
    }
    
    /**
     * Callback Metadata containing transaction details
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallbackMetadata {
        
        @JsonProperty("Item")
        private List<CallbackItem> item;
    }
    
    /**
     * Individual callback item
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallbackItem {
        
        @JsonProperty("Name")
        private String name;
        
        @JsonProperty("Value")
        private Object value;
    }
    
    /**
     * Helper method to extract specific callback item
     */
    public Object getCallbackValue(String name) {
        if (body != null && body.getStkCallback() != null 
            && body.getStkCallback().getCallbackMetadata() != null
            && body.getStkCallback().getCallbackMetadata().getItem() != null) {
            
            return body.getStkCallback().getCallbackMetadata().getItem().stream()
                .filter(item -> name.equals(item.getName()))
                .map(CallbackItem::getValue)
                .findFirst()
                .orElse(null);
        }
        return null;
    }
    
    /**
     * Extract amount from callback
     */
    public BigDecimal getAmount() {
        Object value = getCallbackValue("Amount");
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return transAmount;
    }
    
    /**
     * Extract M-Pesa receipt number
     */
    public String getMpesaReceiptNumber() {
        Object value = getCallbackValue("MpesaReceiptNumber");
        return value != null ? value.toString() : null;
    }
    
    /**
     * Extract phone number
     */
    public String getPhoneNumber() {
        Object value = getCallbackValue("PhoneNumber");
        if (value != null) {
            return value.toString();
        }
        return msisdn;
    }
    
    /**
     * Check if transaction was successful
     */
    public boolean isSuccessful() {
        if (body != null && body.getStkCallback() != null) {
            return body.getStkCallback().getResultCode() != null 
                && body.getStkCallback().getResultCode() == 0;
        }
        return false;
    }
}