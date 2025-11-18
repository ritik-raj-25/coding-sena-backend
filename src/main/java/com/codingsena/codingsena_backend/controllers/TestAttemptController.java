package com.codingsena.codingsena_backend.controllers;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.MCQAttemptResponseDTO;
import com.codingsena.codingsena_backend.dtos.MCQSelectedOptionDto;
import com.codingsena.codingsena_backend.dtos.TestAttemptResponseDTO;
import com.codingsena.codingsena_backend.services.TestAttemptService;

import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/attempts")
@Validated
public class TestAttemptController {
	
	private TestAttemptService testAttemptService;
	
	@Value("${base.url}")
	private String baseUrl;
	
	public TestAttemptController(TestAttemptService testAttemptService) {
		super();
		this.testAttemptService = testAttemptService;
	}
	
	@PostMapping("/start/tests/{testId}")
	public ResponseEntity<ApiResponse<TestAttemptResponseDTO>> startTest(
				@Min(value = 1, message="Test id can't be negative") @PathVariable Long testId
			) {
		ApiResponse<TestAttemptResponseDTO> response = testAttemptService.startTest(testId);
		URI location = UriComponentsBuilder.fromUriString(baseUrl + "/api/attempts/")
				.path("{attemptId}")
				.buildAndExpand(response.getResource().getId())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}
	
	@PostMapping("/submit/tests/{attemptId}")
	public ResponseEntity<ApiResponse<TestAttemptResponseDTO>> submitTest(
				@Min(value = 1, message="Attempt id can't be negative") @PathVariable Long attemptId
			) throws NoSuchAlgorithmException, Exception {
		ApiResponse<TestAttemptResponseDTO> response = testAttemptService.submitTest(attemptId);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/{attemptId}/mcqs/{mcqId}/save")
	public ResponseEntity<ApiResponse<MCQAttemptResponseDTO>> saveMCQ(
				@Min(value = 1, message="Attempt id can't be negative") @PathVariable Long attemptId,
				@Min(value = 1, message="MCQ id can't be negative") @PathVariable Long mcqId,
				@RequestBody MCQSelectedOptionDto selectedOption
			) {
		ApiResponse<MCQAttemptResponseDTO> response = testAttemptService.saveMCQ(attemptId, mcqId, selectedOption.getSelectedOption());
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/tests/{testId}/remaining-attempts")
	public ResponseEntity<ApiResponse<Integer>> getRemainingAttempts(
				@Min(value = 1, message="Test id can't be negative") @PathVariable Long testId
			) {
		ApiResponse<Integer> response = testAttemptService.getRemainingAttempts(testId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/tests/{testId}")
	public ResponseEntity<ApiResponse<List<TestAttemptResponseDTO>>> getAllAttemptsOfTest(
				@Min(value = 1, message="Test id can't be negative") @PathVariable Long testId
			) throws NoSuchAlgorithmException, Exception {
		ApiResponse<List<TestAttemptResponseDTO>> response = testAttemptService.getAllAttemptsOfTest(testId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{attemptId}/ai-suggestion")
	public ResponseEntity<ApiResponse<String>> getAISuggestionForTestAttempt(
				@Min(value = 1, message="Attempt id can't be negative") @PathVariable Long attemptId
			) throws Exception {
		ApiResponse<String> response = testAttemptService.getAISuggestionForTestAttempt(attemptId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/tests/{testId}/report")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<List<TestAttemptResponseDTO>>> getTestReport(
				@Min(value = 1, message="Attempt id can't be negative") @PathVariable Long testId
			) throws Exception {
		ApiResponse<List<TestAttemptResponseDTO>> response = testAttemptService.getTestReport(testId);
		return ResponseEntity.ok(response);
	}
	
}
