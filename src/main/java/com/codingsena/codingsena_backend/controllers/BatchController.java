package com.codingsena.codingsena_backend.controllers;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.BatchCreateDto;
import com.codingsena.codingsena_backend.dtos.BatchResponseDto;
import com.codingsena.codingsena_backend.dtos.BatchUpdateDto;
import com.codingsena.codingsena_backend.dtos.PagedResponse;
import com.codingsena.codingsena_backend.services.BatchService;
import com.codingsena.codingsena_backend.utils.AppConstant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@Validated
@RequestMapping("/api")
public class BatchController {
	
	private BatchService batchService;

	@Value("${base.url}")
	private String baseUrl;
	
	public BatchController(BatchService batchService) {
		super();
		this.batchService = batchService;
	}
	
	@PostMapping("/admin/batches")
	public ResponseEntity<ApiResponse<BatchResponseDto>> createBatch(
				@Valid @RequestPart("batchCreateDto") BatchCreateDto batchCreateDto,
				@RequestPart("coverPic") MultipartFile coverPic,
				@RequestPart("curriculum") MultipartFile curriculum
			) throws S3Exception, AwsServiceException, SdkClientException, IOException {
		ApiResponse<BatchResponseDto> response = batchService.createBatch(batchCreateDto, coverPic, curriculum);
		URI location = UriComponentsBuilder.fromUriString(baseUrl + "/api/admin/batches/")
				.path("{id}")
				.buildAndExpand(response.getResource().getId())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}
	
	@PatchMapping("/admin/batches/{id}")
	public ResponseEntity<ApiResponse<BatchResponseDto>> updateBatchById(
				@Min(value = 0, message = "Batch id can't be negative.") @PathVariable Long id,
				@Valid @RequestPart(name = "batchUpdateDto", required = false) BatchUpdateDto batchUpdateDto,
				@RequestPart(name = "coverPic", required = false) MultipartFile coverPic,
				@RequestPart(name = "curriculum", required = false) MultipartFile curriculum
			) throws S3Exception, AwsServiceException, SdkClientException, IOException {
		ApiResponse<BatchResponseDto> response = batchService.updateBatch(id, batchUpdateDto, coverPic, curriculum);
		return ResponseEntity.ok(response);
	}
	
	// Soft delete
	@PatchMapping("/admin/batches/de-activate/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteBatchById(@Min(value = 0, message = "Batch id can't be negative.") @PathVariable Long id) {
		ApiResponse<Void> response = batchService.deleteBatchById(id);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/admin/batches/activate/{id}")
	public ResponseEntity<ApiResponse<Void>> restoreBatchById(@Min(value = 0, message = "Batch id can't be negative.") @PathVariable Long id) {
		ApiResponse<Void> response = batchService.restoreBatchById(id);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/admin/batches")
	public ResponseEntity<ApiResponse<PagedResponse<BatchResponseDto>>> getAllBatches(
				@RequestParam(name="pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) @Min(value = 0, message = "Page number can't be negative.") Integer pageNumber,
				@RequestParam(name="pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) @Min(value = 1, message = "Page size should at least be 1.") Integer pageSize,
				@RequestParam(name="sortBy", defaultValue = AppConstant.SORT_BY, required = false) @NotBlank(message = "Sort by can't be blank.") String sortBy,
				@RequestParam(name="sortDir", defaultValue = AppConstant.SORT_DIR, required = false) @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Sort direction must be 'asc' or 'desc'") String sortDir
			) {
		ApiResponse<PagedResponse<BatchResponseDto>> response = batchService.getAllBatches(pageNumber, pageSize, sortBy, sortDir);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/batches/{id}")
	public ResponseEntity<ApiResponse<BatchResponseDto>> getBatchById(@Min(value = 0, message = "Batch id can't be negative.") @PathVariable Long id) {
		ApiResponse<BatchResponseDto> response = batchService.getBatchById(id);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/batches")
	public ResponseEntity<ApiResponse<PagedResponse<BatchResponseDto>>> getAllActiveBatches(
				@RequestParam(name="pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) @Min(value = 0, message = "Page number can't be negative.") Integer pageNumber,
				@RequestParam(name="pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) @Min(value = 1, message = "Page size should at least be 1.") Integer pageSize,
				@RequestParam(name="sortBy", defaultValue = AppConstant.SORT_BY, required = false) @NotBlank(message = "Sort by can't be blank.") String sortBy,
				@RequestParam(name="sortDir", defaultValue = AppConstant.SORT_DIR, required = false) @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Sort direction must be 'asc' or 'desc'") String sortDir
			) {
		ApiResponse<PagedResponse<BatchResponseDto>> response = batchService.getAllActiveBatches(pageNumber, pageSize, sortBy, sortDir);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/batches/search")
	public ResponseEntity<ApiResponse<PagedResponse<BatchResponseDto>>> searchBatch(
			@RequestParam(name="pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) @Min(value = 0, message = "Page number can't be negative.") Integer pageNumber,
			@RequestParam(name="pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) @Min(value = 1, message = "Page size should at least be 1.") Integer pageSize,
			@RequestParam(name="sortBy", defaultValue = AppConstant.SORT_BY, required = false) @NotBlank(message = "Sort by can't be blank.") String sortBy,
			@RequestParam(name="sortDir", defaultValue = AppConstant.SORT_DIR, required = false) @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Sort direction must be 'asc' or 'desc'") String sortDir,
			@RequestParam(name="keyword", required = true) @NotBlank(message = "Batch name can't be blank.") String keyword
		) {
		ApiResponse<PagedResponse<BatchResponseDto>> response = batchService.searchBatches(pageNumber, pageSize, sortBy, sortDir, keyword);
		return ResponseEntity.ok(response);
	}
}
