package com.codingsena.codingsena_backend.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Topic {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private Long id;
	
	@Column(nullable = false, unique = true)
	@EqualsAndHashCode.Include
	private String name;
	
	@CreatedDate
	@Column(updatable = false, name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	@CreatedBy
	private String createdBy;
	
	@ManyToMany(mappedBy = "topics", fetch = FetchType.LAZY)
	private Set<Batch> batches = new HashSet<>();
	
	@OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
	private Set<StudyMaterial> studyMaterials = new HashSet<>();
	
	public void addBatch(Batch batch) {
		this.batches.add(batch);
		batch.getTopics().add(this);
	}
	
	public void removeBatch(Batch batch) {
		this.batches.remove(batch);
		batch.getTopics().remove(this);
	}
}
