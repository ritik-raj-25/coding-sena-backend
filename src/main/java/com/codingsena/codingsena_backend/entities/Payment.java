package com.codingsena.codingsena_backend.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codingsena.codingsena_backend.utils.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false, unique = true, name = "session_id")
    private String sessionId;
    
    // The actual intent to pay money (Stripe creates this behind the session).
    // Charge (chargeId) = the actual money movement attempt (when card/bank is charged).
    @Column(unique = true, name = "payment_intent_id")
    private String paymentIntentId; // Stripe’s record for this payment attempt.

    @Column(unique = true, name = "charge_id")
    private String chargeId; // A Charge is the actual money movement attempt.

    // amount stored as smallest currency unit (paise for INR, cents for USD)
    @Column(nullable = false)
    private Long amount;
    
    @Column(nullable = false)
    private String currency;

    // stripe status: "succeeded", "requires_payment_method", "requires_action", etc.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentStatus status;
    
    @Lob
    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;
    
    public void addPayment(Enrollment enrollment) {
        this.enrollment = enrollment;
        enrollment.getPayments().add(this);
    }
}
