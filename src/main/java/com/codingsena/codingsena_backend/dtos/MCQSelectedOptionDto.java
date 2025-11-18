package com.codingsena.codingsena_backend.dtos;

import jakarta.validation.constraints.Pattern;

public class MCQSelectedOptionDto {
	@Pattern(regexp = "^(A|B|C|D)$", message = "Selected option must be one of A, B, C, or D")
	private String selectedOption;

	public MCQSelectedOptionDto() {
		super();
	}

	public MCQSelectedOptionDto(String selectedOption) {
		super();
		this.selectedOption = selectedOption;
	}

	public String getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(String selectedOption) {
		this.selectedOption = selectedOption;
	}
}
