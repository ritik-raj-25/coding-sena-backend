package com.codingsena.codingsena_backend.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mcq_attempts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class MCQAttempt {
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "selected_option", nullable = false)
	private String selectedOption;
	
	@Column(name = "is_correct", nullable = false)
	private Boolean isCorrect = false;
	
	@Column(name = "answered_at", nullable = false)
	@CreatedDate
	private LocalDateTime answeredAt;
	
	@Column(name = "updated_at")
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mcq_id", nullable = false)
	private MCQ mcq;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_attempt_id", nullable = false)
	private TestAttempt testAttempt;
	
	public void addMcq(MCQ mcq) {
		this.mcq = mcq;
		if (!mcq.getMcqAttempts().contains(this)) {
			mcq.getMcqAttempts().add(this);
		}
	}
	
	public void removeMcq(MCQ mcq) {
		this.mcq = null;
		if (mcq.getMcqAttempts().contains(this)) {
			mcq.getMcqAttempts().remove(this);
		}
	}
	
	public void addTestAttempt(TestAttempt testAttempt) {
		this.testAttempt = testAttempt;
		if (!testAttempt.getMcqAttempts().contains(this)) {
			testAttempt.getMcqAttempts().add(this);
		}
	}
	
	public void removeTestAttempt(TestAttempt testAttempt) {
		this.testAttempt = null;
		if (testAttempt.getMcqAttempts().contains(this)) {
			testAttempt.getMcqAttempts().remove(this);
		}
	}
}
