package com.codingsena.codingsena_backend.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codingsena.codingsena_backend.utils.MaterialType;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "study_materials")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StudyMaterial {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
	private Long id;
	
	@Column(nullable = false)
	private String title;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "material_type", nullable = false)
	private MaterialType materialType;
	
	@Column
	private String url; // can be null for files
	
	@Column(name = "uploaded_at", nullable = false, updatable = false)
	@CreatedDate
	private LocalDateTime uploadedAt;
	
	@Column(name = "updated_at")
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	@CreatedBy
	private String uploadedBy; 
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
	    name = "batch_study_materials",
	    joinColumns = @JoinColumn(name = "study_material_id"),
	    inverseJoinColumns = @JoinColumn(name = "batch_id")
	)
	private Set<Batch> batches = new HashSet<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_id", nullable = false)
	private Topic topic;
	
	public void addTopic(Topic topic) {
		this.topic = topic;
		topic.getStudyMaterials().add(this);
	}
	
	public void removeTopic(Topic topic) {
		topic.getStudyMaterials().remove(this);
		this.topic = null;
	}
	
	public void addBatch(Batch batch) {
	    this.batches.add(batch);
	    batch.getStudyMaterials().add(this);
	}

	public void removeBatch(Batch batch) {
	    this.batches.remove(batch);
	    batch.getStudyMaterials().remove(this);
	}

}
