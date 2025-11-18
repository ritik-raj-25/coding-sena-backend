package com.codingsena.codingsena_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SkillCreateDto {
	@NotBlank(message = "Title must not be empty.")
	private String title;
}
