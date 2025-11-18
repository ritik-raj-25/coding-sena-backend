package com.codingsena.codingsena_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.TestAttempt;
import com.codingsena.codingsena_backend.utils.AttemptStatus;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

	Integer countByTestIdAndUserId(Long testId, Long userId);

	List<TestAttempt> findByStatus(AttemptStatus inProgress);

	List<TestAttempt> findByTestIdAndUserId(Long testId, Long id);

	List<TestAttempt> findByTestId(Long testId);
	
}
