package com.codingsena.codingsena_backend.dtos;

import org.hibernate.validator.constraints.URL;

import com.codingsena.codingsena_backend.utils.MaterialType;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudyMaterialUpdateDto {
	@Pattern(regexp = "^(?!\\s)(?!\\s*$).+", message = "Title cannot be blank or only whitespace.")
	private String title;
	private MaterialType materialType;
	@URL(message = "Invalid URL format.")
	private String url;
}
