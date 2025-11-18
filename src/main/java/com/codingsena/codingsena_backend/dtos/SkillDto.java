package com.codingsena.codingsena_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class SkillDto {
	@NotNull(message = "Skill Id is required.")
	private Long id;
	private String title;
}
