package com.codingsena.codingsena_backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {
	@NotBlank(message = "Email must not be empty")
	@Email(message = "Invalid email")
	private String email;
	
	@NotBlank(message = "Password must not be empty")
	@Size(min = 8, message = "Password must have at least 8 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
	private String password;
}
