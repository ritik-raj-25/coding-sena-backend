package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TestAttemptResponseDTO {
	@EqualsAndHashCode.Include
	private Long id;
	private Long testId;
	private String userEmail;
	private Double score;
	private Double totalMarks;
	private LocalDateTime startedAt;
	private LocalDateTime submittedAt;
	private Integer attemptNumber;
	private Boolean isTestAttemptTempered;
	List<MCQAttemptResponseDTO> mcqAttempts;
}
