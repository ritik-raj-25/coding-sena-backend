package com.codingsena.codingsena_backend.services.impls;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.entities.Enrollment;
import com.codingsena.codingsena_backend.repositories.EnrollmentRepository;
import com.codingsena.codingsena_backend.repositories.PaymentRepository;
import com.codingsena.codingsena_backend.services.WebhookService;
import com.codingsena.codingsena_backend.utils.EnrollmentStatus;
import com.codingsena.codingsena_backend.utils.PaymentStatus;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

@Service
public class StripeWebhookService implements WebhookService{
	
	private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StripeWebhookService(PaymentRepository paymentRepository,
                                EnrollmentRepository enrollmentRepository) {
        this.paymentRepository = paymentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }
	
    @Override
    @Transactional
    public void processEvent(Event event) {
        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            case "payment_intent.succeeded":
                handlePaymentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentFailed(event);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
    }
	
    private void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (session == null) return;

        String sessionId = session.getId();
        String paymentIntentId = session.getPaymentIntent();

        try {
            // Fetch PaymentIntent from Stripe
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            paymentRepository.findBySessionId(sessionId).ifPresent(payment -> {
                payment.setPaymentIntentId(paymentIntentId);
                
                String chargeId = intent.getLatestCharge();

                if (chargeId != null) {
                    payment.setChargeId(chargeId);
                }

                // Decide payment status based on intent
                switch (intent.getStatus()) {
                    case "succeeded":
                        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
                            payment.setStatus(PaymentStatus.SUCCEEDED);
                            Enrollment enrollment = payment.getEnrollment();
                            enrollment.setStatus(EnrollmentStatus.ACTIVE);
                            enrollmentRepository.save(enrollment);
                        }
                        break;

                    case "requires_payment_method":
                    case "canceled":
                        payment.setStatus(PaymentStatus.FAILED);
                        break;

                    case "processing":
                    default:
                        payment.setStatus(PaymentStatus.PENDING);
                        break;
                }

                payment.setRawResponse(intent.toJson());
                paymentRepository.save(payment);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
    private void handlePaymentSucceeded(Event event) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (intent == null) return;

        String paymentIntentId = intent.getId();
        String chargeId = intent.getLatestCharge();

        paymentRepository.findByPaymentIntentId(paymentIntentId).ifPresent(payment -> {
            // Only update if not already marked as SUCCEEDED
            if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
                payment.setStatus(PaymentStatus.SUCCEEDED);
                payment.setChargeId(chargeId);
                payment.setRawResponse(intent.toJson());
                paymentRepository.save(payment);

                Enrollment enrollment = payment.getEnrollment();
                if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
                    enrollment.setStatus(EnrollmentStatus.ACTIVE);
                    enrollmentRepository.save(enrollment);
                }
            }
        });
    }

	
    private void handlePaymentFailed(Event event) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (intent == null) return;

        String paymentIntentId = intent.getId();

        paymentRepository.findByPaymentIntentId(paymentIntentId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setRawResponse(intent.toJson());
                paymentRepository.save(payment);
            } 
        });
    }

}
