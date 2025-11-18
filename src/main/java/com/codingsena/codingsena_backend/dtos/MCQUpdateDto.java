package com.codingsena.codingsena_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MCQUpdateDto {
	@NotBlank(message = "Question cannot be blank.")
	private String question;
	
	@NotBlank(message = "Option A cannot be blank.")
	private String optionA;
	
	@NotBlank(message = "Option B cannot be blank.")
	private String optionB;
	
	@NotBlank(message = "Option C cannot be blank.")
	private String optionC;
	
	@NotBlank(message = "Option D cannot be blank.")
	private String optionD;
	
	@NotBlank(message = "Correct option cannot be blank.")
	@Pattern(regexp = "^(A|B|C|D)$", message = "Correct option must be one of A, B, C, or D.")	
	private String correctOption;
}
