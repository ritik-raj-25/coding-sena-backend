package com.codingsena.codingsena_backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
	@NotBlank(message = "Email must not be empty")
	@Email(message = "Invalid email")
	private String email;
}
