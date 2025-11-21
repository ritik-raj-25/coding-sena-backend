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
@Table(name = "mcqs")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class MCQ {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private Long id;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String question;
	
	@Column(nullable = false, name = "option_a", columnDefinition = "TEXT")
	private String optionA;
	
	@Column(nullable = false, name = "option_b", columnDefinition = "TEXT")
	private String optionB;
	
	@Column(nullable = false, name = "option_c", columnDefinition = "TEXT")
	private String optionC;
	
	@Column(nullable = false, name = "option_d", columnDefinition = "TEXT")
	private String optionD;
	
	@Column(name = "correct_option", nullable = false)
	private String correctOption;
	
	@Column(name = "difficulty_level", nullable = false)
	private DifficultyLevel difficultyLevel;
	
	@Column(nullable = false)
	private Float marks;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	@CreatedDate
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	@Column(name = "created_by", nullable = false, updatable = false)
	@CreatedBy
	private String createdBy;
	
	@Column(name = "last_updated_by", nullable = false)
	@LastModifiedBy
	private String lastUpdatedBy;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id", nullable = false)
	private Test test;
	
	@OneToMany(mappedBy = "mcq", fetch = FetchType.LAZY)
	private Set<MCQAttempt> mcqAttempts;
	
	public void addMcqAttempt(MCQAttempt mcqAttempt) {
		this.mcqAttempts.add(mcqAttempt);
		mcqAttempt.setMcq(this);
	}
	
	public void removeMcqAttempt(MCQAttempt mcqAttempt) {
		this.mcqAttempts.remove(mcqAttempt);
		mcqAttempt.setMcq(null);
	}
	
	
	public void addTest(Test test) {
		this.test = test;
		test.getMcqs().add(this);
	}
	
	public void removeTest() {
		if (this.test != null) {
			this.test.getMcqs().remove(this);
			this.test = null;
		}
	}
}
