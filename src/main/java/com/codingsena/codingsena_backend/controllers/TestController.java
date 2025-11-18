package com.codingsena.codingsena_backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.TestRequestDto;
import com.codingsena.codingsena_backend.dtos.TestResponseDto;
import com.codingsena.codingsena_backend.services.TestService;
import com.codingsena.codingsena_backend.services.TestUpdateRequestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/tests")
@Validated
public class TestController {
	
	@Value("${base.url}")
	private String baseUrl;
	
	private TestService testService;
	
	public TestController(TestService testService) {
		this.testService = testService;
	}
	
	@PostMapping("/batches/{batchId}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<TestResponseDto>> createTest(
				@Min(value = 1, message = "Batch id can't be negative") @PathVariable Long batchId, 
				@Valid @RequestBody TestRequestDto testRequestDto
			) {
		ApiResponse<TestResponseDto> response = testService.createTest(testRequestDto, batchId);
		URI location = UriComponentsBuilder.fromUriString(baseUrl + "/api/tests/")
				.path("{testId}")
				.buildAndExpand(response.getResource().getId())
				.toUri();
		return ResponseEntity.created(location ).body(response);
	}
	
	@GetMapping("/{testId}")
	public ResponseEntity<ApiResponse<TestResponseDto>> getTestById(
				@Min(value = 1, message = "Test id can't be negative") @PathVariable Long testId
			) {
		ApiResponse<TestResponseDto> response = testService.getTestById(testId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/batches/{batchId}")
	public ResponseEntity<ApiResponse<List<TestResponseDto>>> getAllTestsOfBatch(
				@Min(value = 1, message = "Batch id can't be negative") @PathVariable Long batchId
			) {
		ApiResponse<List<TestResponseDto>> response = testService.getAllTestsOfBatch(batchId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/batches/{batchId}/active" )
	public ResponseEntity<ApiResponse<List<TestResponseDto>>> getAllActiveTestsOfBatch(
				@Min(value = 1, message = "Batch id can't be negative") @PathVariable Long batchId
			) {
		ApiResponse<List<TestResponseDto>> response = testService.getAllActiveTestsOfBatch(batchId);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/{testId}/soft-delete")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<Void>> softDeleteTest(
				@Min(value = 1, message = "Test id can't be negative") @PathVariable Long testId
			) {
		ApiResponse<Void> response = testService.softDeleteTest(testId);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/{testId}/update" )
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<TestResponseDto>> updateTest(
				@Min(value = 1, message = "Test id can't be negative") @PathVariable Long testId,
				@Valid @RequestBody TestUpdateRequestDto testUpdateRequestDto
			) {
		ApiResponse<TestResponseDto> response = testService.updateTest(testId, testUpdateRequestDto);
		return ResponseEntity.ok(response);
	}
	
}
