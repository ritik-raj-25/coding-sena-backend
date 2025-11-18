package com.codingsena.codingsena_backend.services;

import java.util.List;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.AssignBatchRequest;
import com.codingsena.codingsena_backend.dtos.BatchResponseDto;
import com.codingsena.codingsena_backend.dtos.EnrollmentResponseDto;
import com.stripe.exception.StripeException;

public interface EnrollmentService {
	ApiResponse<EnrollmentResponseDto> assignBatchToTrainer(AssignBatchRequest assignBatchRequest); // It is for enrollment
	ApiResponse<List<BatchResponseDto>> getAllBatchOfUser(); // enrollment status = ACTIVE
	ApiResponse<EnrollmentResponseDto> enrollLearner(Long batchId) throws StripeException;
	ApiResponse<List<BatchResponseDto>> getAllBatchesOfTrainer(); // isTrainerEnrollmentByAdmin = true
	ApiResponse<EnrollmentResponseDto> revokeBatchFromTrainer(AssignBatchRequest assignBatchRequest);
	ApiResponse<Boolean> isTrainerOfBatch(Long batchId);
}
