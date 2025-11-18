package com.codingsena.codingsena_backend.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.codingsena.codingsena_backend.dtos.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	
	private ObjectMapper objectMapper;
	
	public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	// Triggered when access is denied, i.e., RBAC failure.
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		ErrorResponse errorResponse = new ErrorResponse( "Access Denied: You don't have permission", HttpStatus.FORBIDDEN.value());
		
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json");
		objectMapper.writeValue(response.getWriter(), errorResponse);
		
	}

}
