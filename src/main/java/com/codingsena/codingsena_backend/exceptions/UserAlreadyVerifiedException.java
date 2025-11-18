package com.codingsena.codingsena_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserAlreadyVerifiedException extends RuntimeException {
	
	public UserAlreadyVerifiedException(String message) {
		super(message);
	}
	
}
