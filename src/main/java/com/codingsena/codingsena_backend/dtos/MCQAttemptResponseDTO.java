package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;

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
public class MCQAttemptResponseDTO {
	@EqualsAndHashCode.Include
	private Long id;
	private String selectedOption;
	private Boolean isCorrect;
	private LocalDateTime answeredAt;
	private LocalDateTime updatedAt;
	private MCQResponseDto mcqResponseDto;
}
