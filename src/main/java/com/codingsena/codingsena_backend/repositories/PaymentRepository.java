package com.codingsena.codingsena_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findBySessionId(String sessionId);

	Optional<Payment> findByPaymentIntentId(String paymentIntentId);
}
