package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;

import com.codingsena.codingsena_backend.utils.DifficultyLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestResponseDto {
	private Long id;
	private String title;
	private String description;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Integer totalMarks;
	private Integer duration; // in minutes
	private Integer maxAttempts;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private DifficultyLevel difficultyLevel;
}
