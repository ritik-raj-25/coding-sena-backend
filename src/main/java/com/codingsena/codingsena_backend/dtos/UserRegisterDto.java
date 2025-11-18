package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {
	@NotBlank(message = "Name must not be empty")
	@Size(min = 3, message = "Name must have at least 3 characters")
	@Pattern(regexp = "^[A-Za-z]{3,}( [A-Za-z]+){0,2}$", message = "The name must contain only English letters and spaces")
	private String name;
	
	@NotBlank(message = "Email must not be empty")
	@Email(message = "Invalid email")
	private String email;
	
	@NotNull(message = "DOB must not be empty")
	@Past(message = "DOB must be a past date")
	private LocalDate dob;
	
	private String location;
	
	private String college;
	
	@NotBlank(message = "Password must not be empty")
	@Size(min = 8, message = "Password must have at least 8 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
	private String password;
	
	@NotBlank(message = "Nick name must not be empty")
	@Size(min = 3, message = "Nick name must have at least 3 characters")
	@Pattern(regexp = "^[A-Za-z0-9._-]{3,30}$", message = "Nick name must be 3–30 characters long and can contain letters, digits, dot (.), underscore (_), or hyphen (-) with no spaces")
	private String nickName;
	
	private Set<SkillDto> skills;
}
