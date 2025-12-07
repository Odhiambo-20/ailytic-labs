package com.allyticlabs.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MpesaCallbackRequest {
    
    @JsonProperty("Body")
    private StkCallbackBody body;
    
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
    
    private String rawCallbackData;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StkCallbackBody {
        @JsonProperty("stkCallback")
        private StkCallback stkCallback;
    }
    
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
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallbackMetadata {
        @JsonProperty("Item")
        private List<CallbackItem> item;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallbackItem {
        @JsonProperty("Name")
        private String name;
        
        @JsonProperty("Value")
        private Object value;
    }
    
    // Top-level convenience methods
    public String getCheckoutRequestId() {
        if (body != null && body.getStkCallback() != null) {
            return body.getStkCallback().getCheckoutRequestId();
        }
        return null;
    }
    
    public Integer getResultCode() {
        if (body != null && body.getStkCallback() != null) {
            return body.getStkCallback().getResultCode();
        }
        return null;
    }
    
    public String getResultDesc() {
        if (body != null && body.getStkCallback() != null) {
            return body.getStkCallback().getResultDesc();
        }
        return null;
    }
    
    public String getTransactionDate() {
        Object value = getCallbackValue("TransactionDate");
        if (value != null) {
            return value.toString();
        }
        return transTime;
    }
    
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
    
    public BigDecimal getAmount() {
        Object value = getCallbackValue("Amount");
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return transAmount;
    }
    
    public String getMpesaReceiptNumber() {
        Object value = getCallbackValue("MpesaReceiptNumber");
        return value != null ? value.toString() : null;
    }
    
    public String getPhoneNumber() {
        Object value = getCallbackValue("PhoneNumber");
        if (value != null) {
            return value.toString();
        }
        return msisdn;
    }
    
    public boolean isSuccessful() {
        if (body != null && body.getStkCallback() != null) {
            return body.getStkCallback().getResultCode() != null 
                && body.getStkCallback().getResultCode() == 0;
        }
        return false;
    }
}
