package com.codingsena.codingsena_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TokenExpiredException extends RuntimeException {
	
	public TokenExpiredException(String message) {
		super(message);
	}
	
}
