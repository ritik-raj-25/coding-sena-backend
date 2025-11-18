package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;

import com.codingsena.codingsena_backend.utils.DifficultyLevel;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestRequestDto {
	@NotBlank(message = "Title is mandatory.")
	private String title;
	
	@Pattern(regexp = "^(?!\\s)(?!\\s*$).+", message = "Description must not be blank or only whitespace.")
	private String description;
	
	@NotNull(message = "Start time is mandatory.")
	@Future(message = "Start time must be in the future.")
	private LocalDateTime startTime;
	
	@Future(message = "End time must be in the future.")
	@NotNull(message = "End time is mandatory.")
	private LocalDateTime endTime; 
	
	@NotNull(message = "Total marks is mandatory.")
	@Min(value = 1, message = "Total marks must be at least 1.")
	private Integer totalMarks;
	
	@NotNull(message = "Duration is mandatory.")
	@Min(value = 1, message = "Duration must be at least 1 minute.")
	private Integer duration; // in minutes
	
	@NotNull(message = "Max attempts is mandatory.")
	@Min(value = 1, message = "Max attempts must be at least 1.")
	private Integer maxAttempts;
	
	@NotNull(message = "Difficulty level is mandatory.")
	private DifficultyLevel difficultyLevel;
}
