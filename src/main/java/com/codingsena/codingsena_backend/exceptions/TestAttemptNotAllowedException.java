package com.codingsena.codingsena_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class TestAttemptNotAllowedException extends RuntimeException {
	public TestAttemptNotAllowedException(String message) {
		super(message);
	}
}
