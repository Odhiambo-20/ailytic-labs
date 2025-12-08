package com.allyticlabs.backend.service;

import com.allyticlabs.backend.config.StripeConfig;
import com.allyticlabs.backend.dto.PaymentRequest;
import com.allyticlabs.backend.dto.PaymentResponse;
import com.allyticlabs.backend.exception.PaymentException;
import com.allyticlabs.backend.model.PaymentStatus;
import com.allyticlabs.backend.model.StripePayment;
import com.allyticlabs.backend.repository.StripePaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StripeService {

    private final StripeConfig stripeConfig;
    private final StripePaymentRepository stripePaymentRepository;
    private final PaymentService paymentService;
    private final PaymentEncryptionService encryptionService;

    // Constructor with @Lazy annotation to break circular dependency
    public StripeService(
            StripeConfig stripeConfig,
            StripePaymentRepository stripePaymentRepository,
            @Lazy PaymentService paymentService,  // Add @Lazy here
            PaymentEncryptionService encryptionService) {
        this.stripeConfig = stripeConfig;
        this.stripePaymentRepository = stripePaymentRepository;
        this.paymentService = paymentService;
        this.encryptionService = encryptionService;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeConfig.getSecretKey();
        log.info("Stripe API initialized");
    }

    @Transactional
    public PaymentResponse createPaymentIntent(PaymentRequest request) {
        log.info("Creating Stripe Payment Intent for amount: {} {}",
                request.getAmount(), request.getCurrency());

        try {
            long amountInCents = request.getAmount().multiply(new BigDecimal("100")).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setDescription(request.getDescription())
                    .putMetadata("paymentId", request.getPaymentId())
                    .putMetadata("merchantId", request.getMerchantId())
                    .putMetadata("userId", request.getUserId())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            StripePayment stripePayment = StripePayment.builder()
                    .stripePaymentIntentId(intent.getId())
                    .paymentId(request.getPaymentId())
                    .amount(amountInCents / 100.0)
                    .currency(request.getCurrency())
                    .status(PaymentStatus.PENDING.name())
                    .stripeStatus(intent.getStatus())
                    .build();

            stripePayment.setCreatedAt(Instant.now());

            stripePaymentRepository.save(stripePayment);

            log.info("Stripe Payment Intent created: {}", intent.getId());

            return PaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .status(PaymentStatus.PENDING)
                    .providerTransactionId(intent.getId())
                    .message("Payment Intent created successfully")
                    .metadata(Map.of("clientSecret", intent.getClientSecret()))
                    .build();

        } catch (Exception e) {
            log.error("Error creating Stripe Payment Intent", e);
            throw new PaymentException("Failed to create payment intent: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentResponse confirmPaymentIntent(String paymentIntentId, String paymentMethodId) {
        log.info("Confirming Stripe Payment Intent: {}", paymentIntentId);

        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            if (paymentMethodId != null) {
                PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                        .setPaymentMethod(paymentMethodId)
                        .build();
                intent = intent.confirm(params);
            } else {
                intent = intent.confirm();
            }

            StripePayment stripePayment = stripePaymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new PaymentException("Stripe payment not found"));

            stripePayment.setStripeStatus(intent.getStatus());
            stripePayment.setUpdatedAt(Instant.now());

            PaymentStatus newStatus = convertStripeStatusToPaymentStatus(intent.getStatus());
            stripePayment.setStatus(newStatus);
            
            if (newStatus == PaymentStatus.SUCCESS) {
                paymentService.completePayment(
                        stripePayment.getPaymentId(),
                        PaymentStatus.SUCCESS,
                        "Stripe payment completed"
                );
            }

            stripePaymentRepository.save(stripePayment);

            return PaymentResponse.builder()
                    .paymentId(stripePayment.getPaymentId())
                    .status(stripePayment.getStatusEnum())
                    .providerTransactionId(paymentIntentId)
                    .build();

        } catch (Exception e) {
            log.error("Error confirming Payment Intent", e);
            throw new PaymentException("Failed to confirm payment: " + e.getMessage());
        }
    }

    public Map<String, String> createCustomer(String email, String name, String phone) {
        log.info("Creating Stripe customer for email: {}", email);

        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(name)
                    .setPhone(phone)
                    .build();

            Customer customer = Customer.create(params);

            return Map.of(
                    "customerId", customer.getId(),
                    "email", customer.getEmail(),
                    "created", String.valueOf(customer.getCreated())
            );

        } catch (Exception e) {
            log.error("Error creating Stripe customer", e);
            throw new PaymentException("Failed to create customer: " + e.getMessage());
        }
    }

    public Map<String, String> attachPaymentMethod(String customerId, String paymentMethodId) {
        log.info("Attaching payment method to customer: {}", customerId);

        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

            PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build();

            paymentMethod.attach(params);

            return Map.of(
                    "paymentMethodId", paymentMethod.getId(),
                    "customerId", customerId,
                    "type", paymentMethod.getType()
            );

        } catch (Exception e) {
            log.error("Error attaching payment method", e);
            throw new PaymentException("Failed to attach payment method: " + e.getMessage());
        }
    }

    public Map<String, Object> getCustomerPaymentMethods(String customerId) {
        log.info("Fetching payment methods for customer: {}", customerId);

        try {
            PaymentMethodListParams params = PaymentMethodListParams.builder()
                    .setCustomer(customerId)
                    .setType(PaymentMethodListParams.Type.CARD)
                    .build();

            List<PaymentMethod> paymentMethods = PaymentMethod.list(params).getData();

            List<Map<String, String>> methods = paymentMethods.stream()
                    .map(pm -> Map.of(
                            "id", pm.getId(),
                            "type", pm.getType(),
                            "cardBrand", pm.getCard().getBrand(),
                            "cardLast4", pm.getCard().getLast4()
                    ))
                    .collect(Collectors.toList());

            return Map.of(
                    "customerId", customerId,
                    "paymentMethods", methods
            );

        } catch (Exception e) {
            log.error("Error fetching payment methods", e);
            throw new PaymentException("Failed to fetch payment methods: " + e.getMessage());
        }
    }

    public Map<String, String> detachPaymentMethod(String paymentMethodId) {
        log.info("Detaching payment method: {}", paymentMethodId);

        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.detach();

            return Map.of(
                    "paymentMethodId", paymentMethodId,
                    "status", "detached"
            );

        } catch (Exception e) {
            log.error("Error detaching payment method", e);
            throw new PaymentException("Failed to detach payment method: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentResponse createRefund(String paymentIntentId, Long amount, String reason) {
        log.info("Creating refund for Payment Intent: {}", paymentIntentId);

        try {
            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId);

            if (amount != null) {
                paramsBuilder.setAmount(amount);
            }

            if (reason != null) {
                paramsBuilder.setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER);
            }

            Refund refund = Refund.create(paramsBuilder.build());

            StripePayment stripePayment = stripePaymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new PaymentException("Stripe payment not found"));

            stripePayment.setRefundId(refund.getId());
            stripePayment.setStatus(PaymentStatus.REFUNDED);
            stripePayment.setUpdatedAt(Instant.now());

            stripePaymentRepository.save(stripePayment);

            paymentService.completePayment(
                    stripePayment.getPaymentId(),
                    PaymentStatus.REFUNDED,
                    "Refund processed: " + refund.getId()
            );

            return PaymentResponse.builder()
                    .paymentId(stripePayment.getPaymentId())
                    .status(PaymentStatus.REFUNDED)
                    .providerTransactionId(refund.getId())
                    .build();

        } catch (Exception e) {
            log.error("Error creating refund", e);
            throw new PaymentException("Failed to create refund: " + e.getMessage());
        }
    }

    public PaymentResponse getPaymentIntent(String paymentIntentId) {
        log.info("Fetching Payment Intent: {}", paymentIntentId);

        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            StripePayment stripePayment = stripePaymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new PaymentException("Stripe payment not found"));

            return PaymentResponse.builder()
                    .paymentId(stripePayment.getPaymentId())
                    .amount(BigDecimal.valueOf(stripePayment.getAmount()))
                    .currency(stripePayment.getCurrency())
                    .status(stripePayment.getStatusEnum())
                    .providerTransactionId(intent.getId())
                    .build();

        } catch (Exception e) {
            log.error("Error fetching Payment Intent", e);
            throw new PaymentException("Failed to fetch payment intent: " + e.getMessage());
        }
    }

    @Transactional
    public void processWebhookEvent(String payload, String signature) {
        log.info("Processing Stripe webhook event");

        try {
            Event event = com.stripe.net.Webhook.constructEvent(
                    payload,
                    signature,
                    stripeConfig.getWebhookSecret()
            );

            String eventType = event.getType();

            switch (eventType) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                case "charge.refunded":
                    handleChargeRefunded(event);
                    break;
                default:
                    log.info("Unhandled event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing Stripe webhook", e);
            throw new PaymentException("Failed to process webhook: " + e.getMessage());
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (intent != null) {
            log.info("Payment succeeded: {}", intent.getId());
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (intent != null) {
            log.warn("Payment failed: {}", intent.getId());
        }
    }

    private void handleChargeRefunded(Event event) {
        Charge charge = (Charge) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (charge != null) {
            log.info("Charge refunded: {}", charge.getId());
        }
    }

    private PaymentStatus convertStripeStatusToPaymentStatus(String stripeStatus) {
        switch (stripeStatus) {
            case "succeeded":
                return PaymentStatus.SUCCESS;
            case "processing":
            case "requires_action":
            case "requires_payment_method":
                return PaymentStatus.PROCESSING;
            case "canceled":
                return PaymentStatus.CANCELLED;
            case "requires_capture":
                return PaymentStatus.PENDING;
            default:
                return PaymentStatus.FAILED;
        }
    }
}