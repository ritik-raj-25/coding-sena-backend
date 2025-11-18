package com.codingsena.codingsena_backend.exceptions;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.codingsena.codingsena_backend.dtos.ErrorResponse;
import com.stripe.exception.StripeException;

import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler{
	
	@ExceptionHandler(ResourceAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleResourceAlreadyExistException(ResourceAlreadyExistException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(TestAttemptNotAllowedException.class)
	public ResponseEntity<ErrorResponse> handleTestAttemptNotAllowedException(TestAttemptNotAllowedException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UserAlreadyVerifiedException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidFileTypeException.class)
	public ResponseEntity<ErrorResponse> handleInvalidFileTypeException(InvalidFileTypeException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<ErrorResponse> handleMessagingException(MessagingException ex) {
		ErrorResponse errorResponse = new ErrorResponse("Failed to send verification email. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
		ErrorResponse errorResponse = new ErrorResponse("File processing error occurred. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String message = ex.getFieldErrors().stream()
			    .map(error -> error.getField() + " : " + error.getDefaultMessage())
			    .collect(Collectors.joining("; "));
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		String message = ex.getConstraintViolations()
			    .stream()
			    .map(error -> error.getPropertyPath() + " : " + error.getMessage())
			    .collect(Collectors.joining("; "));
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
		String message = "Required request parameter " + ex.getParameterName() + " is missing.";
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		String message = "Invalid value: " + ex.getValue() + " for " + ex.getName() +  ".";
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MCQGenerationException.class)
	public ResponseEntity<ErrorResponse> handleMCQGenerationException(MCQGenerationException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "The endpoint you are trying to access does not exist.",
            HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
		String message = "One or more required fields are missing.";
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		String message = ex.getMessage();
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
		String message = ex.getMessage();
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
		String message = "File upload failed or exceeded max size.";
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleInvalidEnum(HttpMessageNotReadableException ex) {
		String message = ex.getMessage();
		if(message.contains("BatchValidity")) {
			message = "Invalid Batch Validity. Allowed values are: SIX_MONTHS, ONE_YEAR, TWO_YEAR, LIFE_TIME";
		}
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(LockedException.class)
	public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex) {
		ErrorResponse errorResponse = new ErrorResponse("User is blocked.", HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
		ErrorResponse errorResponse = new ErrorResponse("Invalid username or password.", HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ErrorResponse> handleDisabledException(DisabledException ex) {
		ErrorResponse errorResponse = new ErrorResponse("User not verified.", HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(AccountExpiredException.class)
	public ResponseEntity<ErrorResponse> handleAccountExpiredException(AccountExpiredException ex) {
		ErrorResponse errorResponse = new ErrorResponse("User has deleted his/her account. Activate account before login.", HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}
	
	 @ExceptionHandler(StripeException.class)
    public ResponseEntity<ErrorResponse> handleStripeException(StripeException ex) {
	 	ErrorResponse errorResponse = new ErrorResponse("Issue with payment, please try again.", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
	
	// Generic Exceptions
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
		ErrorResponse errorResponse = new ErrorResponse("Login failed. Try again.", HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		String message = "Something went wrong. Please try again.";
		ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
