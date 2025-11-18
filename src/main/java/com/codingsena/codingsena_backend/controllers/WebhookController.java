package com.codingsena.codingsena_backend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codingsena.codingsena_backend.services.WebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

// A webhook is an automated, event-driven way for one application to send real-time data to another via an HTTP request when a specific event occurs. 

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        webhookService.processEvent(event);
        return ResponseEntity.ok("Processed");
        
    }
}