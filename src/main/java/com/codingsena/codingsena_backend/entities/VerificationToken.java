package com.codingsena.codingsena_backend.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "verification_token")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode
public class VerificationToken {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, unique = true, updatable = false)
	private String token;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime expiryDate;
	
	@CreatedDate
	private LocalDateTime createdAt;
	
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "user_id", nullable = false)
	@OneToOne(fetch = FetchType.LAZY)
	private User user;
}
