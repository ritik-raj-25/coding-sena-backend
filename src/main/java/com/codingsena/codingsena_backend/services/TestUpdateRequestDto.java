package com.codingsena.codingsena_backend.services;

import java.time.LocalDateTime;

import com.codingsena.codingsena_backend.utils.DifficultyLevel;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestUpdateRequestDto {
	
	@Pattern(regexp = "^(?!\\s)(?!\\s*$).+", message = "Title must not be blank or only whitespace.")
	private String title;
	
	@Pattern(regexp = "^(?!\\s)(?!\\s*$).+", message = "Description must not be blank or only whitespace.")
	private String description;
	
	@Future(message = "Start time must be in the future.")
	private LocalDateTime startTime;
	
	@Future(message = "End time must be in the future.")
	private LocalDateTime endTime; 
	
	@Min(value = 1, message = "Total marks must be at least 1.")
	private Integer totalMarks;
	
	@Min(value = 1, message = "Duration must be at least 1 minute.")
	private Integer duration; // in minutes
	
	@Min(value = 1, message = "Max attempts must be at least 1.")
	private Integer maxAttempts;
	
	private DifficultyLevel difficultyLevel;
	
	private Boolean isActive;
}
