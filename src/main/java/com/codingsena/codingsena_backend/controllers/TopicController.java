package com.codingsena.codingsena_backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.TopicRequestDto;
import com.codingsena.codingsena_backend.dtos.TopicResponseDto;
import com.codingsena.codingsena_backend.services.TopicService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/topics")
@Validated
public class TopicController {
	
	private TopicService topicService;
	
	@Value("${base.url}")	
	private String baseUrl;
	
	public TopicController(TopicService topicService) {
		this.topicService = topicService;
	}
	
	@PostMapping("/batches/{batchId}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<TopicResponseDto>> createTopic(
			@PathVariable Long batchId,
			@Valid @RequestBody TopicRequestDto topicRequestDto) {
		ApiResponse<TopicResponseDto> response = topicService.createTopic(topicRequestDto, batchId);
		
		URI location = UriComponentsBuilder.fromUriString(baseUrl + "/api/topics/")
				.path("{topicId}")
				.buildAndExpand(response.getResource().getId())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}
	
	@GetMapping("/{topicId}/batches/{batchId}")
	public ResponseEntity<ApiResponse<TopicResponseDto>> getTopicById(
				@Min(value = 1, message = "Topic id can't be negative.") @PathVariable Long topicId,
				@Min(value = 1, message = "Batch id can't be negative.") @PathVariable Long batchId
			) {
		ApiResponse<TopicResponseDto> response = topicService.getTopicById(batchId, topicId);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{topicId}/batches/{batchId}/remove")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<Void>> removeTopic(
			@Min(value = 1, message = "Topic id can't be negative.") @PathVariable Long topicId,
			@Min(value = 1, message = "Batch id can't be negative.") @PathVariable Long batchId) {
		ApiResponse<Void> response = topicService.removeTopic(topicId, batchId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/batches/{batchId}")
	public ResponseEntity<ApiResponse<List<TopicResponseDto>>> getAllTopicsByBatchId(
			@Min(value = 1, message = "Batch id can't be negative.") @PathVariable Long batchId) {
		ApiResponse<List<TopicResponseDto>> response = topicService.getAllTopicsByBatchId(batchId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/search/batches/{batchId}")
	public ResponseEntity<ApiResponse<List<TopicResponseDto>>> searchTopicsByName(
			@Min(value = 1, message = "Batch id can't be negative.") @PathVariable Long batchId,
		 	@NotBlank(message = "Topic name is required.") @RequestParam(required = true) String name) {
		ApiResponse<List<TopicResponseDto>> response = topicService.searchTopicsByName(name, batchId);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{topicId}/batches/{batchId}/update")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<TopicResponseDto>> updateTopic(
			@Min(value = 1, message = "Topic id can't be negative.") @PathVariable Long topicId,
			@Min(value = 1, message = "Batch id can't be negative.") @PathVariable Long batchId,
			@Valid @RequestBody TopicRequestDto topicRequestDto) {
		ApiResponse<TopicResponseDto> response = topicService.updateTopic(topicId, batchId, topicRequestDto);
		return ResponseEntity.ok(response);
	}
	
}
