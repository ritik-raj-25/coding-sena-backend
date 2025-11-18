package com.codingsena.codingsena_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.MCQResponseDto;
import com.codingsena.codingsena_backend.dtos.MCQUpdateDto;
import com.codingsena.codingsena_backend.services.MCQService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/mcqs")
@Validated
public class MCQController {
	
	@Value("${base.url}")
	private String baseUrl;
	
	private MCQService mcqService;
	
	public MCQController(MCQService mcqService) {
		this.mcqService = mcqService;
	}
	
	@PostMapping("/generate-mcqs/tests/{testId}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<List<MCQResponseDto>>> generateMCQs(
				@Min(value = 1, message = "Test id can't be negative") @PathVariable Long testId,
				@RequestParam(name = "noOfMCQs", defaultValue = "5") @Min(value = 1, message = "Number of MCQs must be at least 1") Integer noOfMCQs,
				@RequestParam(name = "topics", required = false) @Size(max = 10, message = "You can provide up to 10 topics") List<@NotBlank @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Topics can only contain alphanumeric characters and spaces") String> topics,
				@RequestParam(name = "userInstructionMessage", required = false, defaultValue = "Generate MCQs based on the provided topics.") @Size(max = 500, message = "User instruction message can be up to 500 characters long") String userInstructionMessage
			) {
		ApiResponse<List<MCQResponseDto>> response = mcqService.createMCQs(testId, noOfMCQs, topics, userInstructionMessage);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PatchMapping("/{mcqId}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<MCQResponseDto>> updateMCQ(
				@PathVariable @Min(value = 1, message = "MCQ id can't be negative") Long mcqId,
				@Valid @RequestBody MCQUpdateDto mcqUpdateDto
			) {
		
		ApiResponse<MCQResponseDto> response = mcqService.updateMCQ(mcqId, mcqUpdateDto);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{mcqId}")
	public ResponseEntity<ApiResponse<MCQResponseDto>> getMCQById(
				@PathVariable @Min(value = 1, message = "MCQ id can't be negative") Long mcqId
			) {
		
		ApiResponse<MCQResponseDto> response = mcqService.getMCQById(mcqId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/tests/{testId}")
	public ResponseEntity<ApiResponse<List<MCQResponseDto>>> getAllMCQsOfTest(
				@PathVariable @Min(value = 1, message = "Test id can't be negative") Long testId
			) {
		
		ApiResponse<List<MCQResponseDto>> response = mcqService.getAllMCQsOfTest(testId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/tests/{testId}/admin-trainer")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<List<MCQResponseDto>>> getAllMCQsOfTestAdminAndTrainer(
				@PathVariable @Min(value = 1, message = "Test id can't be negative") Long testId
			) {
		ApiResponse<List<MCQResponseDto>> response = mcqService.getAllMCQsOfTestAdminAndTrainer(testId);
		return ResponseEntity.ok(response);
	}
	
}
