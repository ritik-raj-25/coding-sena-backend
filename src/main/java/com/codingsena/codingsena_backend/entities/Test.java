package com.codingsena.codingsena_backend.entities;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codingsena.codingsena_backend.utils.DifficultyLevel;

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
@Table(name = "tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Test {
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(length = 1000)
	private String description;
	
	@Column(nullable = false)
	private LocalDateTime startTime;
	
	@Column(nullable = false)
	private LocalDateTime endTime;
	
	@Column(nullable = false, name = "total_marks")
	private Integer totalMarks;
	
	@Column(nullable = false)
	private Integer duration; // in minutes
	
	@Column(name = "max_attempts", nullable = false)
	private Integer maxAttempts;
	
	@Column(name = "difficulty_level", nullable = false)
	@Enumerated(EnumType.STRING)
	private DifficultyLevel difficultyLevel;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;
	
	@Column(name = "created_at", updatable = false)
	@CreatedDate
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	@Column(name = "created_by", updatable = false, nullable = false)
	@CreatedBy
	private String createdBy;
	
	@Column(name = "updated_by", nullable = false)
	@LastModifiedBy
	private String updatedBy;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "batch_id", nullable = false)
	private Batch batch;
	
	@OneToMany(mappedBy = "test", fetch = FetchType.LAZY)
	private Set<MCQ> mcqs;
	
	@OneToMany(mappedBy = "test", fetch = FetchType.LAZY)
	private Set<TestAttempt> testAttempts;
	
	public void addTestAttempt(TestAttempt testAttempt) {
		this.testAttempts.add(testAttempt);
		testAttempt.setTest(this);
	}
	
	public void removeTestAttempt(TestAttempt testAttempt) {
		if (this.testAttempts != null && this.testAttempts.contains(testAttempt)) {
			this.testAttempts.remove(testAttempt);
			testAttempt.setTest(null);
		}
	}
	
	public void addMCQ(MCQ mcq) {
		this.mcqs.add(mcq);
		mcq.setTest(this);
	}
	
	public void removeMCQ(MCQ mcq) {
		if (this.mcqs != null && this.mcqs.contains(mcq)) {
			this.mcqs.remove(mcq);
			mcq.setTest(null);
		}
	}
	
	public void addBatch(Batch batch) {
		this.batch = batch;
		batch.getTests().add(this);
	}
	
	public void removeBatch() {
		if (this.batch != null) {
			this.batch.getTests().remove(this);
			this.batch = null;
		}
	}
}
