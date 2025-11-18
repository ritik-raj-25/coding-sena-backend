package com.codingsena.codingsena_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.CONFLICT)
public class ResourceAlreadyExistException extends RuntimeException{
	
	public ResourceAlreadyExistException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s already exist with %s: '%s'", resourceName, fieldName, fieldValue));
	}
	
}
