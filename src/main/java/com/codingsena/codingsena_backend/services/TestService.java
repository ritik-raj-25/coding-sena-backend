package com.codingsena.codingsena_backend.services;

import java.util.List;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.TestRequestDto;
import com.codingsena.codingsena_backend.dtos.TestResponseDto;

public interface TestService {
	ApiResponse<TestResponseDto> createTest(TestRequestDto testRequestDto, Long batchId);
	ApiResponse<TestResponseDto> getTestById(Long testId);
	ApiResponse<TestResponseDto> updateTest(Long testId, TestUpdateRequestDto testUpdateRequestDto);
	ApiResponse<Void> softDeleteTest(Long testId);
	ApiResponse<List<TestResponseDto>> getAllTestsOfBatch(Long batchId);
	ApiResponse<List<TestResponseDto>> getAllActiveTestsOfBatch(Long batchId);
}
