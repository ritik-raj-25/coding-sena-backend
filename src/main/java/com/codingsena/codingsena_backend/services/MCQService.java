package com.codingsena.codingsena_backend.services;

import java.util.List;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.MCQResponseDto;
import com.codingsena.codingsena_backend.dtos.MCQUpdateDto;

public interface MCQService {
	ApiResponse<List<MCQResponseDto>> createMCQs(Long testId, Integer noOfMCQs, List<String> topics, String userInstructionMessage);
	ApiResponse<MCQResponseDto> getMCQById(Long mcqId);
	ApiResponse<List<MCQResponseDto>> getAllMCQsOfTest(Long testId);
	ApiResponse<MCQResponseDto> updateMCQ(Long mcqId, MCQUpdateDto mcqUpdateDto);
	ApiResponse<List<MCQResponseDto>> getAllMCQsOfTestAdminAndTrainer(Long testId);
}
