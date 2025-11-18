package com.codingsena.codingsena_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
	Optional<VerificationToken> findByToken(String token);
}
