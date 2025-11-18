package com.codingsena.codingsena_backend.services.impls;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.TestRequestDto;
import com.codingsena.codingsena_backend.dtos.TestResponseDto;
import com.codingsena.codingsena_backend.entities.Batch;
import com.codingsena.codingsena_backend.entities.Test;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.repositories.BatchRepository;
import com.codingsena.codingsena_backend.repositories.TestRepository;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.codingsena.codingsena_backend.services.TestService;
import com.codingsena.codingsena_backend.services.TestUpdateRequestDto;
import com.codingsena.codingsena_backend.utils.RoleType;

@Service
public class TestServiceImpl implements TestService {
	
	private BatchRepository batchRepository;
	private TestRepository testRepository;
	private ModelMapper modelMapper;
	private UserRepository userRepository;
	
	public TestServiceImpl(BatchRepository batchRepository, TestRepository testRepository, ModelMapper modelMapper, UserRepository userRepository) {
		this.batchRepository = batchRepository;
		this.testRepository = testRepository;
		this.modelMapper = modelMapper;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public ApiResponse<TestResponseDto> createTest(TestRequestDto testRequestDto, Long batchId) {
		accessCheckHelper(batchId);
		if(testRequestDto.getEndTime().isBefore(testRequestDto.getStartTime())) {
			throw new IllegalArgumentException("End time cannot be before start time");
		}
		Batch batch = batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", batchId));
		Test test = modelMapper.map(testRequestDto, Test.class);
		test.addBatch(batch);
		test.setIsActive(true);
		test = testRepository.save(test);
		TestResponseDto testResponseDto = modelMapper.map(test, TestResponseDto.class);
		return new ApiResponse<>(true, "Test created successfully", testResponseDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<TestResponseDto> getTestById(Long testId) {
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));
		readAccessCheckHelper(test.getBatch().getId());
		TestResponseDto testResponseDto = modelMapper.map(test, TestResponseDto.class);
		return new ApiResponse<>(true, "Test fetched successfully", testResponseDto);
	}

	@Override
	@Transactional
	public ApiResponse<TestResponseDto> updateTest(Long testId, TestUpdateRequestDto testUpdateRequestDto) {
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));
		accessCheckHelper(test.getBatch().getId());
		
		if(testUpdateRequestDto.getEndTime() != null && testUpdateRequestDto.getStartTime() != null) {
			if(testUpdateRequestDto.getEndTime().isBefore(testUpdateRequestDto.getStartTime())) {
				throw new IllegalArgumentException("End time cannot be before start time");
			}
		}
		
		if(testUpdateRequestDto.getTitle() != null && !testUpdateRequestDto.getTitle().equals(test.getTitle())) {
			test.setTitle(testUpdateRequestDto.getTitle());
		}
		if(testUpdateRequestDto.getDescription() != null && !testUpdateRequestDto.getDescription().equals(test.getDescription())) {
			test.setDescription(testUpdateRequestDto.getDescription());
		}
		if(testUpdateRequestDto.getStartTime() != null && !testUpdateRequestDto.getStartTime().equals(test.getStartTime())) {
			test.setStartTime(testUpdateRequestDto.getStartTime());
		}
		if(testUpdateRequestDto.getEndTime() != null && !testUpdateRequestDto.getEndTime().equals(test.getEndTime())) {
			if(test.getStartTime() != null && testUpdateRequestDto.getEndTime().isBefore(test.getStartTime())) {
				throw new IllegalArgumentException("End time cannot be before start time");
			}
			test.setEndTime(testUpdateRequestDto.getEndTime());
		}
		if(testUpdateRequestDto.getTotalMarks() != null && !testUpdateRequestDto.getTotalMarks().equals(test.getTotalMarks())) {
			test.setTotalMarks(testUpdateRequestDto.getTotalMarks());
		}
		if(testUpdateRequestDto.getDuration() != null && !testUpdateRequestDto.getDuration().equals(test.getDuration())) {
			test.setDuration(testUpdateRequestDto.getDuration());
		}
		if(testUpdateRequestDto.getMaxAttempts() != null && !testUpdateRequestDto.getMaxAttempts().equals(test.getMaxAttempts())) {
			test.setMaxAttempts(testUpdateRequestDto.getMaxAttempts());
		}
		if(testUpdateRequestDto.getDifficultyLevel() != null && !testUpdateRequestDto.getDifficultyLevel().equals(test.getDifficultyLevel())) {
			test.setDifficultyLevel(testUpdateRequestDto.getDifficultyLevel());
		}
		if(testUpdateRequestDto.getIsActive() != null && !testUpdateRequestDto.getIsActive().equals(test.getIsActive())) {
			test.setIsActive(testUpdateRequestDto.getIsActive());
		}
		
		test = testRepository.save(test);
		
		TestResponseDto responseDto = modelMapper.map(test, TestResponseDto.class);
		
		return new ApiResponse<>(true, "Test updated successfully", responseDto);
	}

	@Override
	@Transactional
	public ApiResponse<Void> softDeleteTest(Long testId) {
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));
		accessCheckHelper(test.getBatch().getId());
		
		String message = null;
		if(!test.getIsActive()) {
			message = "Test is already inactive.";
		} 
		else {
			test.setIsActive(false);
			message = "Test soft-deleted successfully.";
		}
		
		return new ApiResponse<>(true, message, null);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<TestResponseDto>> getAllActiveTestsOfBatch(Long batchId) {
		readAccessCheckHelper(batchId);
		List<Test> tests = testRepository.findByBatchIdAndIsActiveTrue(batchId);
		List<TestResponseDto> responseDtos = tests.stream()
				.map(test -> modelMapper.map(test, TestResponseDto.class)).toList();
		return new ApiResponse<>(true, "Active tests fetched successfully", responseDtos);
	}
	
	@Override
	public ApiResponse<List<TestResponseDto>> getAllTestsOfBatch(Long batchId) {
		readAccessCheckHelper(batchId);
		List<Test> tests = testRepository.findByBatchId(batchId);
		List<TestResponseDto> responseDtos = tests.stream()
				.map(test -> modelMapper.map(test, TestResponseDto.class)).toList();
		return new ApiResponse<>(true, "All tests fetched successfully", responseDtos);
	}
	
	private void accessCheckHelper(Long batchId) { // to ensure only admin or assigned trainer can create test
		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));

		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));

		if (!isAdmin) {
			boolean unauthorized = user.getEnrollments().stream()
					.noneMatch(enrollment -> enrollment.getBatch().getId().equals(batchId)
							&& enrollment.getIsTrainerEnrollmentByAdmin());
			if (unauthorized) {
				throw new AuthorizationDeniedException(
						"Only trainers assigned to the batch by admin can perform this action.");
			}

		}
	}
	
	private void readAccessCheckHelper(Long batchId) { // to ensure only enrolled learners can view tests
		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));

		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));

		if (!isAdmin) {
			boolean enrolled = user.getEnrollments().stream()
					.anyMatch(enrollment -> enrollment.getBatch().getId().equals(batchId));

			if (!enrolled) {
				throw new AuthorizationDeniedException(
						"Only enrolled users can view test(s) of this batch.");
			}
		}
	}
	
}
