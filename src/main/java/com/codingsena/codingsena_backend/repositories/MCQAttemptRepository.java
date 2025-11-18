package com.codingsena.codingsena_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codingsena.codingsena_backend.entities.MCQAttempt;

public interface MCQAttemptRepository extends JpaRepository<MCQAttempt, Long> {

	Optional<MCQAttempt> findByTestAttemptIdAndMcqId(Long attemptId, Long mcqId);

	@Query("SELECT COALESCE(SUM(m.marks), 0) " +
		       "FROM MCQAttempt a " +
		       "JOIN a.mcq m " +
		       "WHERE a.testAttempt.id = :attemptId " +
		       "AND a.selectedOption = m.correctOption")
	Double calculateScore(@Param("attemptId") Long attemptId);

	List<MCQAttempt> findByTestAttemptId(Long attemptId);
}
