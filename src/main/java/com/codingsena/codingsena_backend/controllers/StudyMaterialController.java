package com.codingsena.codingsena_backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.StudyMaterialRequestDto;
import com.codingsena.codingsena_backend.dtos.StudyMaterialResponseDto;
import com.codingsena.codingsena_backend.dtos.StudyMaterialUpdateDto;
import com.codingsena.codingsena_backend.services.StudyMaterialService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/study-materials")
@Validated
public class StudyMaterialController {
	
	private StudyMaterialService studyMaterialService;
	
	@Value("${base.url}")
	private String baseUrl;
	
	public StudyMaterialController(StudyMaterialService studyMaterialService) {
		this.studyMaterialService = studyMaterialService;
	}
	
	@PostMapping("/batches/{batchId}/topics/{topicId}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<StudyMaterialResponseDto>> createStudyMaterial(
				@Min(value = 1, message = "Batch id can't be negative") @PathVariable Long batchId,
				@Min(value = 1, message = "Topic id can't be negative") @PathVariable Long topicId,
				@Valid @RequestBody StudyMaterialRequestDto requestDto
			) {
		ApiResponse<StudyMaterialResponseDto> response = studyMaterialService.createStudyMaterial(requestDto, topicId, batchId);
		URI location = UriComponentsBuilder.fromPath(baseUrl  + "/api/study-materials/")
				.path("{studyMaterialId}")
				.buildAndExpand(response.getResource().getId())
				.toUri();
		return ResponseEntity.created(location ).body(response);
	}
	
	@GetMapping("/{studyMaterialId}/batches/{batchId}/topics/{topicId}")
	public ResponseEntity<ApiResponse<StudyMaterialResponseDto>> getStudyMaterialById(
				@Min(value = 1, message = "Study material id can't be negative") @PathVariable Long studyMaterialId,
				@Min(value = 1, message = "Batch id can't be negative") @PathVariable Long batchId,
				@Min(value = 1, message = "Topic id can't be negative") @PathVariable Long topicId
			) {
		ApiResponse<StudyMaterialResponseDto> response = studyMaterialService.getStudyMaterialById(batchId, topicId, studyMaterialId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/batches/{batchId}/topics/{topicId}")
	public ResponseEntity<ApiResponse<List<StudyMaterialResponseDto>>> getAllStudyMaterialsByTopicAndBatch(
			@Min(value = 1, message = "Batch id can't be negative") @PathVariable Long batchId,
			@Min(value = 1, message = "Topic id can't be negative") @PathVariable Long topicId) {
		ApiResponse<List<StudyMaterialResponseDto>> response = studyMaterialService.getAllStudyMaterialsByTopicAndBatch(topicId, batchId);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{studyMaterialId}/batches/{batchId}/remove")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<Void>> removeStudyMaterialFromBatch(
				@Min(value = 1, message = "Study material id can't be negative") @PathVariable Long studyMaterialId,
				@Min(value = 1, message = "Batch id can't be negative") @PathVariable Long batchId
			) {
		ApiResponse<Void> response = studyMaterialService.removeStudyMaterialFromBatch(studyMaterialId, batchId);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{studyMaterialId}/topics/{topicId}/remove")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<Void>> removeStudyMaterialFromTopic(
			@Min(value = 1, message = "Study material id can't be negative") @PathVariable Long studyMaterialId,
			@Min(value = 1, message = "Topic id can't be negative") @PathVariable Long topicId
		) {
		ApiResponse<Void> response = studyMaterialService.removeStudyMaterialFromTopic(studyMaterialId, topicId);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/{studyMaterialId}/update")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<StudyMaterialResponseDto>> updateStudyMaterial(
				@Min(value = 1, message = "Study material id can't be negative") @PathVariable Long studyMaterialId,
				@Valid @RequestBody StudyMaterialUpdateDto studyMaterialUpdateDto
			) {
		ApiResponse<StudyMaterialResponseDto> response = studyMaterialService.updateStudyMaterial(studyMaterialId, studyMaterialUpdateDto);
		return ResponseEntity.ok(response);
	}
}
