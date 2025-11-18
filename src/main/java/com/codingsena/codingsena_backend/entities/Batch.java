package com.codingsena.codingsena_backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codingsena.codingsena_backend.utils.BatchValidity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "batches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Batch {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private Long id;
	
	@Column(name = "cover_pic_name", nullable = false)
	private String coverPicName;
	
	@Column(name = "batch_name", nullable = false, unique = true)
	private String batchName;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BatchValidity validity;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive; // for soft delete
	
	@Column(nullable = false)
	private String curriculum; // pdf or word file
	
	@Column(nullable = false)
	private LocalDate startDate;
	
	@Column(nullable = false)
	private LocalDate endDate;
	
	@Column(nullable = false)
	private Long price; // for free batches price = 0
	
	@Column(nullable = false)
	private Long discount;
	
	@CreatedDate
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	private LocalDateTime lastUpdatedAt;
	
	@OneToMany(mappedBy = "batch")
	private Set<Enrollment> enrollments = new HashSet<>();
	
	@ManyToMany
	@JoinTable(
		name = "batch_topics",
		joinColumns = @JoinColumn(name = "batch_id"),
		inverseJoinColumns = @JoinColumn(name = "topic_id")
	)
	private Set<Topic> topics = new HashSet<>();
	
	@ManyToMany(mappedBy = "batches")
	private Set<StudyMaterial> studyMaterials = new HashSet<>();
	
	@OneToMany(mappedBy = "batch")
	private Set<Test> tests = new HashSet<>();
	
	public void addTest(Test test) {
		this.tests.add(test);
		test.setBatch(this);
	}
	
	public void removeTest(Test test) {
		this.tests.remove(test);
		test.setBatch(null);
	}
	
	public void addStudyMaterial(StudyMaterial sm) {
	    this.studyMaterials.add(sm);
	    sm.getBatches().add(this);
	}

	public void removeStudyMaterial(StudyMaterial sm) {
	    this.studyMaterials.remove(sm);
	    sm.getBatches().remove(this);
	}
	
	public void addTopic(Topic topic) {
		this.topics.add(topic);
		topic.getBatches().add(this);
	}
	
	public void removeTopic(Topic topic) {
		this.topics.remove(topic);
		topic.getBatches().remove(this);
	}
}
