package com.bach.RoomRentalManagementSystem.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentLinkCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StripeService {
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private ServiceBillService serviceBillService;

    public StripeService(@Value("${stripe.secret.key}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }

    public PaymentIntent createPaymentIntent(Double amount, Long billId) throws StripeException {

        long amountInCent = (long) (amount * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCent)
                .setCurrency("vnd")
                .putMetadata("billId", billId.toString())
                .build();

        return PaymentIntent.create(params);
    }
}
