package com.codingsena.codingsena_backend.services.impls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.BatchCreateDto;
import com.codingsena.codingsena_backend.dtos.BatchResponseDto;
import com.codingsena.codingsena_backend.dtos.BatchUpdateDto;
import com.codingsena.codingsena_backend.dtos.PagedResponse;
import com.codingsena.codingsena_backend.entities.Batch;
import com.codingsena.codingsena_backend.exceptions.InvalidFileTypeException;
import com.codingsena.codingsena_backend.exceptions.ResourceAlreadyExistException;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.repositories.BatchRepository;
import com.codingsena.codingsena_backend.repositories.EnrollmentRepository;
import com.codingsena.codingsena_backend.services.BatchService;
import com.codingsena.codingsena_backend.services.FileService;
import com.codingsena.codingsena_backend.utils.EnrollmentStatus;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class BatchServiceImpl implements BatchService {
	
	@Value("${AWS_BATCH_COVER_PIC_SUB_BUCKET_NAME}")
	private String batchCoverPicFolder;
	
	@Value("${AWS_BATCH_CURRICULUM_SUB_BUCKET_NAME}")
	private String batchCurriculumFolder;
	
	private BatchRepository batchRepository;
	private FileService fileService;
	private ModelMapper modelMapper;
	private EnrollmentRepository enrollmentRepository;
	
	public BatchServiceImpl(BatchRepository batchRepository, FileService fileService, ModelMapper modelMapper, EnrollmentRepository enrollmentRepository) {
		super();
		this.batchRepository = batchRepository;
		this.fileService = fileService;
		this.modelMapper = modelMapper;
		this.enrollmentRepository = enrollmentRepository;
	}

	@Override
	@Transactional
	public ApiResponse<BatchResponseDto> createBatch(BatchCreateDto batchCreateDto, MultipartFile coverPic,
			MultipartFile curriculum) throws S3Exception, AwsServiceException, SdkClientException, IOException {
		
		if(batchRepository.existsByBatchNameIgnoreCase(batchCreateDto.getBatchName()) ) {
			throw new ResourceAlreadyExistException("Batch", "batch name", batchCreateDto.getBatchName());
		}
		
		Batch batch = modelMapper.map(batchCreateDto, Batch.class);
		
		String coverPicName = null;
		if (coverPic != null) {
			String contentType = coverPic.getContentType();
			if (contentType.startsWith("image/")) {
				coverPicName = fileService.saveFile(batchCoverPicFolder, coverPic);
			} 
			else {
				throw new InvalidFileTypeException(
						"Invalid file. Only image files(.png, .jpg, and .jpeg) are allowed.");
			}
		}
		batch.setCoverPicName(coverPicName);
		
		
		String curriculumFileName = null;
		if (curriculum != null) {
			String contentType = curriculum.getContentType();
			if (contentType.equals("application/pdf")) {
				curriculumFileName = fileService.saveFile(batchCurriculumFolder, curriculum);
			} 
			else {
				throw new InvalidFileTypeException(
						"Invalid file. Only pdf file(.pdf) is allowed.");
			}
		}
		batch.setCurriculum(curriculumFileName);
		
		batch.setIsActive(true);
		
		Batch savedBatch = batchRepository.save(batch);
		
		BatchResponseDto batchResponseDto = batchResponseHelper(savedBatch);
		
		return new ApiResponse<>(true, "Batch created successfully.", batchResponseDto);
	}

	private BatchResponseDto batchResponseHelper(Batch batch) {
		BatchResponseDto batchResponseDto = modelMapper.map(batch, BatchResponseDto.class);
		String coverPicUrl = batch.getCoverPicName() != null ? fileService.getFileUrl(batch.getCoverPicName()) : null;
		String curriculumUrl = batch.getCurriculum() != null ? fileService.getFileUrl(batch.getCurriculum()) : null;
		batchResponseDto.setCoverPicUrl(coverPicUrl);
		batchResponseDto.setCurriculumUrl(curriculumUrl);
		batchResponseDto.setNoOfStudentsEnrolled(enrollmentRepository.countByBatchIdAndStatus(batch.getId(), EnrollmentStatus.ACTIVE));
		return batchResponseDto;
	}

	@Override
	@Transactional
	public ApiResponse<BatchResponseDto> updateBatch(Long id, BatchUpdateDto batchUpdateDto, MultipartFile coverPic,
			MultipartFile curriculum) throws S3Exception, AwsServiceException, SdkClientException, IOException {
		
		Batch batch = batchRepository.findById(id).orElseThrow(() ->
			new ResourceNotFoundException("Batch", "id", id)
		);
		
		if(batchUpdateDto != null) {
			if(batchUpdateDto.getBatchName() != null && !batchUpdateDto.getBatchName().trim().equals("")) {
				
				if(!batchUpdateDto.getBatchName().equals(batch.getBatchName()) && batchRepository.existsByBatchNameIgnoreCase(batchUpdateDto.getBatchName())) {
					throw new ResourceAlreadyExistException("Batch", "batch name", batchUpdateDto.getBatchName());
				}
				
				batch.setBatchName(batchUpdateDto.getBatchName());
			}
			if(batchUpdateDto.getDiscount() != null) {
				batch.setDiscount(batchUpdateDto.getDiscount());
			}
			if(batchUpdateDto.getEndDate() != null) {
				batch.setEndDate(batchUpdateDto.getEndDate());
			}
			if(batchUpdateDto.getPrice() != null) {
				batch.setPrice(batchUpdateDto.getPrice());
			}
			if(batchUpdateDto.getStartDate() != null) {
				batch.setStartDate(batchUpdateDto.getStartDate());
			}
			if(batchUpdateDto.getValidity() != null) {
				batch.setValidity(batchUpdateDto.getValidity());
			}
		}
		
		String coverPicName = null;
		if (coverPic != null) {
			String contentType = coverPic.getContentType();
			if (contentType.startsWith("image/")) {
				coverPicName = fileService.saveFile(batchCoverPicFolder, coverPic);
		        if(batch.getCoverPicName() != null) {
		            fileService.deleteFile(batch.getCoverPicName());
		        }
			} 
			else {
				throw new InvalidFileTypeException(
						"Invalid file. Only image files(.png, .jpg, and .jpeg) are allowed.");
			}
			batch.setCoverPicName(coverPicName);
		}
		
		String curriculumFileName = null;
		if (curriculum != null) {
			String contentType = curriculum.getContentType();
			if (contentType.equals("application/pdf")) {
				curriculumFileName = fileService.saveFile(batchCurriculumFolder, curriculum);
				if(batch.getCurriculum() != null) {
					fileService.deleteFile(batch.getCurriculum());
				}
			} 
			else {
				throw new InvalidFileTypeException(
						"Invalid file. Only pdf file(.pdf) is allowed.");
			}
			batch.setCurriculum(curriculumFileName);
		}
		
		BatchResponseDto batchResponseDto = batchResponseHelper(batch);
		
		return new ApiResponse<>(true, "Batch updated successfully.", batchResponseDto);
	}

	@Override
	@Transactional
	public ApiResponse<Void> deleteBatchById(Long id) { // 'soft delete'
		Batch batch = null;
		String message = null;
		if(batchRepository.existsById(id)) {
			batch = batchRepository.findById(id).get();
			if(batch.getIsActive()) {
				batch.setIsActive(false);
				message = "Batch de-activated successfully.";
			}
			else {
				message = "Batch: " + batch.getBatchName() + " is already de-active.";
			}
		}
		else {
			throw new ResourceNotFoundException("Batch", "id", id);
		}
		return new ApiResponse<>(true, message);
	}

	@Override
	@Transactional
	public ApiResponse<Void> restoreBatchById(Long id) {
		Batch batch = null;
		String message = null;
		if(batchRepository.existsById(id)) {
			batch = batchRepository.findById(id).get();
			if(!batch.getIsActive()) {
				batch.setIsActive(true);
				message = "Batch activated successfully.";
			}
			else {
				message = "Batch: " + batch.getBatchName() + " is already active.";
			}
		}
		else {
			throw new ResourceNotFoundException("Batch", "id", id);
		}
		return new ApiResponse<>(true, message);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<BatchResponseDto> getBatchById(Long id) {
		Batch batch = batchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Batch", "id", id));
		BatchResponseDto batchResponseDto = batchResponseHelper(batch);
		return new ApiResponse<BatchResponseDto>(true, "Batch fetched successfully.", batchResponseDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<PagedResponse<BatchResponseDto>> getAllBatches(Integer pageNumber, Integer pageSize,
			String sortBy, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Batch> batches = batchRepository.findAll(pageable);
		return pagedResponseHelper(batches);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<PagedResponse<BatchResponseDto>> getAllActiveBatches(Integer pageNumber, Integer pageSize,
			String sortBy, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Batch> batches = batchRepository.findByIsActiveTrue(pageable);
		return pagedResponseHelper(batches);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<PagedResponse<BatchResponseDto>> searchBatches(Integer pageNumber, Integer pageSize,
			String sortBy, String sortDir, String keyword) {
		Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Batch> batches = batchRepository.findByBatchNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable);
		return pagedResponseHelper(batches);
	}

	private ApiResponse<PagedResponse<BatchResponseDto>> pagedResponseHelper(Page<Batch> batches) {
		PagedResponse<BatchResponseDto> pagedResponse = new PagedResponse<>();
		List<BatchResponseDto> batchResponseDtos = new ArrayList<>();
		batches.stream().forEach(batch -> {
			BatchResponseDto batchResponseDto = batchResponseHelper(batch);
			batchResponseDtos.add(batchResponseDto);
		});
		pagedResponse.setContent(batchResponseDtos);
		pagedResponse.setIsLastPage(batches.isLast());
		pagedResponse.setPageNumber(batches.getNumber());
		pagedResponse.setPageSize(batches.getSize());
		pagedResponse.setTotalElements(batches.getTotalElements());
		pagedResponse.setTotalPages(batches.getTotalPages());
		return new ApiResponse<>(true, "Batches fetched successfully.", pagedResponse);
	}

}
