package com.codingsena.codingsena_backend.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.UserLoginDto;
import com.codingsena.codingsena_backend.dtos.UserResponseDto;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class JwtAuthenticationController {
	
	private AuthenticationManager authenticationManager;
	private JwtService jwtService;
	
	public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@PostMapping("/auth/login")
	public ResponseEntity<ApiResponse<UserResponseDto>> login(@Valid @RequestBody UserLoginDto loginDto, HttpServletResponse response) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
		ApiResponse<UserResponseDto> apiResponse = jwtService.createLoginResponse(response, authentication);
		return ResponseEntity.ok(apiResponse);
	}
	
	@PatchMapping("auth/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
		ApiResponse<Void> apiResponse = jwtService.logout(response);
		return ResponseEntity.ok(apiResponse);
	}
}
