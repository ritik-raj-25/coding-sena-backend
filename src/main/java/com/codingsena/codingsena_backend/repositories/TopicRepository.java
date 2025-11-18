package com.codingsena.codingsena_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codingsena.codingsena_backend.entities.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {
	Optional<Topic> findByName(String name);
	
	@Query("SELECT t FROM Topic t JOIN t.batches b WHERE b.id = :batchId")
	List<Topic> findAllTopicsByBatchId(@Param("batchId") Long batchId);
	
	@Query("SELECT t FROM Topic t JOIN t.batches b WHERE b.id = :batchId AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	List<Topic> findAllTopicsByBatchIdContainingName(@Param("batchId") Long batchId, @Param("name")String name);
}
