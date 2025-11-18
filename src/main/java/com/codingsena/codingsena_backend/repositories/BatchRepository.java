package com.codingsena.codingsena_backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.Batch;

public interface BatchRepository extends JpaRepository<Batch, Long> {
	Boolean existsByBatchNameIgnoreCase(String batchName);

	Page<Batch> findByIsActiveTrue(Pageable pageable);

	Page<Batch> findByBatchNameContainingIgnoreCaseAndIsActiveTrue(String keyword, Pageable pageable);
}
