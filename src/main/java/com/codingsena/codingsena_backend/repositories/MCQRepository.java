package com.codingsena.codingsena_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.MCQ;

public interface MCQRepository extends JpaRepository<MCQ, Long> {

	List<MCQ> findByTestId(Long testId);
	
}
