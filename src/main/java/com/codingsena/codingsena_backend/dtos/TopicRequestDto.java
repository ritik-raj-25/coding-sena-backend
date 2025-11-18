package com.codingsena.codingsena_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopicRequestDto {
	@NotBlank(message = "Topic name is required")
	@Size(max = 100, message = "Topic name must not exceed 100 characters")
	private String name;
}
