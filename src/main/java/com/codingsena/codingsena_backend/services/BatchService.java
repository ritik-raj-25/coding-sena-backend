package com.codingsena.codingsena_backend.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.BatchCreateDto;
import com.codingsena.codingsena_backend.dtos.BatchResponseDto;
import com.codingsena.codingsena_backend.dtos.BatchUpdateDto;
import com.codingsena.codingsena_backend.dtos.PagedResponse;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

public interface BatchService {
	
	ApiResponse<BatchResponseDto> createBatch(BatchCreateDto batchCreateDto, MultipartFile coverPic, MultipartFile curriculum) throws S3Exception, AwsServiceException, SdkClientException, IOException;
	ApiResponse<BatchResponseDto> updateBatch(Long id, BatchUpdateDto batchUpdateDto, MultipartFile coverPic, MultipartFile curriculum) throws S3Exception, AwsServiceException, SdkClientException, IOException;
	ApiResponse<Void> deleteBatchById(Long id); //soft delete
	ApiResponse<Void> restoreBatchById(Long id);
	ApiResponse<BatchResponseDto> getBatchById(Long id);
	ApiResponse<PagedResponse<BatchResponseDto>> getAllBatches(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	ApiResponse<PagedResponse<BatchResponseDto>> getAllActiveBatches(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	ApiResponse<PagedResponse<BatchResponseDto>> searchBatches(Integer pageNumber, Integer pageSize, String sortBy, String sortDir, String keyword); // by name
	
}
