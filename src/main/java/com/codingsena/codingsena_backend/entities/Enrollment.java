package com.codingsena.codingsena_backend.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codingsena.codingsena_backend.utils.EnrollmentStatus;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enrollments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private Long id;
	
	private Boolean isTrainerEnrollmentByAdmin = false;
	
	@CreatedDate
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "cancelled_at")
	private LocalDateTime cancelledAt;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EnrollmentStatus status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "batch_id", nullable = false)
	private Batch batch;
	
	@OneToMany(mappedBy = "enrollment")
    private Set<Payment> payments = new HashSet<>();
	
	public void addUser(User user) {
		this.user = user;
		this.user.getEnrollments().add(this);
	}
	
	public void addBatch(Batch batch) {
		this.batch = batch;
		this.batch.getEnrollments().add(this);
	}
	
}
