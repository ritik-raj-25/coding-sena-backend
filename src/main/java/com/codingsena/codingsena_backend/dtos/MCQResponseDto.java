package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;

import com.codingsena.codingsena_backend.utils.DifficultyLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MCQResponseDto {
	private Long id;
	private String question;
	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;
	private String correctOption;
	private DifficultyLevel difficultyLevel;
	private Float marks;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String createdBy;
	private String lastUpdatedBy;
}
