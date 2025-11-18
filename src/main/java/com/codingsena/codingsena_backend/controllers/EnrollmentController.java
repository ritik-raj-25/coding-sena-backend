package com.codingsena.codingsena_backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import com.codingsena.codingsena_backend.dtos.AssignBatchRequest;
import com.codingsena.codingsena_backend.dtos.BatchResponseDto;
import com.codingsena.codingsena_backend.dtos.EnrollmentResponseDto;
import com.codingsena.codingsena_backend.services.EnrollmentService;
import com.stripe.exception.StripeException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api")
@Validated
public class EnrollmentController {
	
	@Value("${base.url}")
	private String baseUrl;
	
	private EnrollmentService enrollmentService;

	public EnrollmentController(EnrollmentService enrollmentService) {
		super();
		this.enrollmentService = enrollmentService;
	}
	
	@PostMapping("/admin/enrollments/trainers")
	public ResponseEntity<ApiResponse<EnrollmentResponseDto>> assignBatchToTrainer(@Valid @RequestBody AssignBatchRequest assignBatchRequest) {
		ApiResponse<EnrollmentResponseDto> response = enrollmentService.assignBatchToTrainer(assignBatchRequest);
		URI location = UriComponentsBuilder.fromUriString(baseUrl + "/api/enrollments/")
				.path("{enrollment-id}")
				.buildAndExpand(response.getResource().getEnrollmentId())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}
	
	@PatchMapping("/admin/enrollments/trainers")
	public ResponseEntity<ApiResponse<EnrollmentResponseDto>> revokeBatchFromTrainer(@Valid @RequestBody AssignBatchRequest assignBatchRequest) {
		ApiResponse<EnrollmentResponseDto> response = enrollmentService.revokeBatchFromTrainer(assignBatchRequest);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/enrollments/learners")
	public ResponseEntity<ApiResponse<List<BatchResponseDto>>> getAllBatchOfUser() {
		ApiResponse<List<BatchResponseDto>> response = enrollmentService.getAllBatchOfUser();
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("enrollments/trainers")
	public ResponseEntity<ApiResponse<List<BatchResponseDto>>> getAllBatchesOfTrainer(){
		ApiResponse<List<BatchResponseDto>> response = enrollmentService.getAllBatchesOfTrainer();
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/enrollments/learners/initiate/{batchId}")
	public ResponseEntity<ApiResponse<EnrollmentResponseDto>> initiateLearnerEnrollment(@Min(value = 1, message = "Batch Id Can't be negative.") @PathVariable Long batchId) throws StripeException {
		ApiResponse<EnrollmentResponseDto> response = enrollmentService.enrollLearner(batchId);
		URI location = UriComponentsBuilder.fromUriString(baseUrl + "/api/enrollments/")
				.path("{enrollment-id}")
				.buildAndExpand(response.getResource().getEnrollmentId())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}
	
	@GetMapping("/enrollments/trainers/batches/{batchId}/is-trainer")
	public ResponseEntity<ApiResponse<Boolean>> isTrainerOfBatch(
			@Min(value = 1, message = "Batch Id Can't be negative.") @PathVariable Long batchId
	) {
		ApiResponse<Boolean> isTrainer = enrollmentService.isTrainerOfBatch(batchId);
		return ResponseEntity.ok(isTrainer);
	}
}
