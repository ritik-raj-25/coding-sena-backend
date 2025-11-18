package com.codingsena.codingsena_backend.dtos;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MCQCreateDto {
	private String question;
	private Map<String, String> options;
	private String answer;
}
