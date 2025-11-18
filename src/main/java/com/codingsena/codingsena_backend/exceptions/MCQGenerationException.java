package com.codingsena.codingsena_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MCQGenerationException extends RuntimeException {
    public MCQGenerationException(String message) {
        super(message);
    }
}
