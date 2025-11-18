package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
	private Boolean success;
	private String message;
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime timeStamp;
	private Integer status;
	
	public ErrorResponse(String message, Integer status) {
		success = false;
		this.message = message;
		timeStamp = LocalDateTime.now();
		this.status = status;
	}
}
