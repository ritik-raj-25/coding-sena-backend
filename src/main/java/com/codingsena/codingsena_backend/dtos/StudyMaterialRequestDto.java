package com.codingsena.codingsena_backend.dtos;

import org.hibernate.validator.constraints.URL;

import com.codingsena.codingsena_backend.utils.MaterialType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudyMaterialRequestDto {
	@NotBlank(message = "Title is required.")
	private String title;
	
	private MaterialType materialType;
	
	@NotBlank(message = "Study material URL is required.")
	@URL(message = "Invalid URL format.")
	private String url;
}
