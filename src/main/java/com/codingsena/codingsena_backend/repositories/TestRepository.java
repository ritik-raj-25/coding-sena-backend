package com.codingsena.codingsena_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.Test;

public interface TestRepository extends JpaRepository<Test, Long> {

	List<Test> findByBatchIdAndIsActiveTrue(Long batchId);

	List<Test> findByBatchId(Long batchId);

}
