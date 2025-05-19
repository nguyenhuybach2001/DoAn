package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.service.ServiceBillService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/stripe/webhook")
public class StripeWebhookController {
    @Value("${stripe.webhook.key}")
    private String WEBHOOK_SECRET;

    @Autowired
    private ServiceBillService serviceBillService;

    private final Logger logger = Logger.getLogger(StripeWebhookController.class.getName());

    @PostMapping
    public void handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, WEBHOOK_SECRET);
            System.out.println("Received event: " + event.getType()); // Log sự kiện

            if ("payment_intent.succeeded".equals(event.getType())) {
                // Log thông tin chi tiết sự kiện nhận được
                System.out.println("Event Data: " + event.getData());
                System.out.println(event.getApiVersion());
                // Truy xuất PaymentIntent từ sự kiện
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);


                // Kiểm tra xem PaymentIntent có thực sự được deserialized không
                if (paymentIntent != null) {
                    System.out.println("PaymentIntent found: " + paymentIntent.getId());
                    String billId = paymentIntent.getMetadata().get("billId");

                    if (billId != null) {
                        serviceBillService.updateBillStatus(Long.parseLong(billId));
                        System.out.println("Bill updated successfully.");
                    } else {
                        System.out.println("No billId found in PaymentIntent metadata.");
                    }
                } else {
                    // Nếu không thể deserialize PaymentIntent, log thêm chi tiết
                    System.out.println("Failed to deserialize PaymentIntent: " + event.getDataObjectDeserializer().getObject().orElse(null));
                }
            }
        } catch (Exception e) {
            logger.severe("Webhook error: " + e.getMessage());
        }
    }

}
