package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/auth/create-payment-intent")
    public Map<String, String> createPaymentIntent(@RequestParam Double amount, @RequestParam Long billId) throws StripeException {

        PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, billId);

        return Map.of("clientSecret", paymentIntent.getClientSecret());
    }
}
