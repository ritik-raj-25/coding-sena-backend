package com.codingsena.codingsena_backend.entities;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codingsena.codingsena_backend.utils.AttemptStatus;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity	
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TestAttempt {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private Long id;
	
	@CreatedDate
	@Column(nullable = false, updatable = false, name = "created_at")
	private LocalDateTime startedAt;
	
	@Column(name = "submitted_at")
	private LocalDateTime submittedAt;
	
	@Enumerated(EnumType.STRING)
	private AttemptStatus status = AttemptStatus.IN_PROGRESS;
	
	@Column(nullable = false)
	private Double score = 0.0;
	
	@Column(name = "total_marks", nullable = false)
	private Double totalMarks;
	
	@Column(name = "attempt_number", nullable = false)
	private Integer attemptNumber;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id", nullable = false)
	private Test test;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@OneToMany(mappedBy = "testAttempt", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<MCQAttempt> mcqAttempts;
	
	public void addMcqAttempt(MCQAttempt mcqAttempt) {
		this.mcqAttempts.add(mcqAttempt);
		mcqAttempt.setTestAttempt(this);
	}
	
	public void removeMcqAttempt(MCQAttempt mcqAttempt) {
		this.mcqAttempts.remove(mcqAttempt);
		mcqAttempt.setTestAttempt(null);
	}
	
	public void addTest(Test test) {
		this.test = test;
		if (!test.getTestAttempts().contains(this)) {
			test.getTestAttempts().add(this);
		}
	}
	
	public void removeTest(Test test) {
		this.test = null;
		if (test.getTestAttempts().contains(this)) {
			test.getTestAttempts().remove(this);
		}
	}
	
	public void addUser(User user) {
		this.user = user;
		if (!user.getTestAttempts().contains(this)) {
			user.getTestAttempts().add(this);
		}
	}
	
	public void removeUser(User user) {
		this.user = null;
		if (user.getTestAttempts().contains(this)) {
			user.getTestAttempts().remove(this);
		}
	}
}
