package com.codingsena.codingsena_backend.services;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.MCQAttemptResponseDTO;
import com.codingsena.codingsena_backend.dtos.TestAttemptResponseDTO;
import com.codingsena.codingsena_backend.entities.TestAttempt;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface TestAttemptService {
	ApiResponse<TestAttemptResponseDTO> startTest(Long testId);
	ApiResponse<MCQAttemptResponseDTO> saveMCQ(Long attemptId, Long mcqId, String selectedOption);
	ApiResponse<TestAttemptResponseDTO> submitTest(Long attemptId) throws NoSuchAlgorithmException, Exception;
	TestAttempt autoSubmitTest(TestAttempt attempt) throws Exception, NoSuchAlgorithmException;
	ApiResponse<Integer> getRemainingAttempts(Long testId);
	ApiResponse<List<TestAttemptResponseDTO>> getAllAttemptsOfTest(Long testId) throws NoSuchAlgorithmException, Exception;
	ApiResponse<String> getAISuggestionForTestAttempt(Long attemptId) throws JsonProcessingException;
	ApiResponse<List<TestAttemptResponseDTO>> getTestReport(Long testId);
}
