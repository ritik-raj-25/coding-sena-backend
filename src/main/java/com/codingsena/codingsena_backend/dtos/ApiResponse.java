package com.codingsena.codingsena_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ApiResponse<T> {
	private Boolean success;
	private String message;
	T resource;
	
	public ApiResponse(Boolean success, String message) {
		this.success = success;
		this.message = message;
		this.resource = null;
	}
}
