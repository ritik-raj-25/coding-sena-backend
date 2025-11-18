package com.codingsena.codingsena_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codingsena.codingsena_backend.entities.StudyMaterial;

public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
	
	@Query("SELECT sm FROM StudyMaterial sm JOIN sm.batches b JOIN sm.topic t WHERE b.id = :batchId AND t.id = :topicId")
	List<StudyMaterial> findByBatchIdAndTopicId(@Param("batchId") Long batchId, @Param("topicId") Long topicId);

}
