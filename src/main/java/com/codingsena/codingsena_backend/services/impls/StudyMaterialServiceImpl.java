package com.codingsena.codingsena_backend.services.impls;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.StudyMaterialRequestDto;
import com.codingsena.codingsena_backend.dtos.StudyMaterialResponseDto;
import com.codingsena.codingsena_backend.dtos.StudyMaterialUpdateDto;
import com.codingsena.codingsena_backend.entities.Batch;
import com.codingsena.codingsena_backend.entities.StudyMaterial;
import com.codingsena.codingsena_backend.entities.Topic;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.repositories.BatchRepository;
import com.codingsena.codingsena_backend.repositories.StudyMaterialRepository;
import com.codingsena.codingsena_backend.repositories.TopicRepository;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.codingsena.codingsena_backend.services.StudyMaterialService;
import com.codingsena.codingsena_backend.utils.RoleType;

@Service
public class StudyMaterialServiceImpl implements StudyMaterialService {

	private StudyMaterialRepository studyMaterialRepository;
	private TopicRepository topicRepository;
	private BatchRepository batchRepository;
	private UserRepository userRepository;
	private ModelMapper modelMapper;

	public StudyMaterialServiceImpl(StudyMaterialRepository studyMaterialRepository, TopicRepository topicRepository,
			BatchRepository batchRepository, ModelMapper modelMapper, UserRepository userRepository) {
		this.studyMaterialRepository = studyMaterialRepository;
		this.topicRepository = topicRepository;
		this.batchRepository = batchRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	@Transactional
	public ApiResponse<StudyMaterialResponseDto> createStudyMaterial(StudyMaterialRequestDto requestDto, Long topicId,
			Long batchId) {

		accessCheckHelper(batchId);

		Batch batch = batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", batchId));
		Topic topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
		StudyMaterial studyMaterial = modelMapper.map(requestDto, StudyMaterial.class);
		studyMaterial.addBatch(batch);
		studyMaterial.addTopic(topic);
		studyMaterial = studyMaterialRepository.save(studyMaterial);
		StudyMaterialResponseDto responseDto = modelMapper.map(studyMaterial, StudyMaterialResponseDto.class);

		return new ApiResponse<>(true, "Study material created successfully", responseDto);
	}

	private void accessCheckHelper(Long batchId) {
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

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<StudyMaterialResponseDto> getStudyMaterialById(Long batchId, Long topicId,
			Long studyMaterialId) {
		readAccessCheckHelper(batchId);
		StudyMaterial studyMaterial = studyMaterialRepository.findById(studyMaterialId)
				.orElseThrow(() -> new ResourceNotFoundException("StudyMaterial", "id", studyMaterialId));
		StudyMaterialResponseDto responseDto = modelMapper.map(studyMaterial, StudyMaterialResponseDto.class);
		return new ApiResponse<>(true, "Study material fetched successfully", responseDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<StudyMaterialResponseDto>> getAllStudyMaterialsByTopicAndBatch(Long topicId, Long batchId) {
		readAccessCheckHelper(batchId);
		List<StudyMaterial> studyMaterials = studyMaterialRepository.findByBatchIdAndTopicId(batchId, topicId);
		List<StudyMaterialResponseDto> responseDtos = studyMaterials.stream()
				.map(sm -> modelMapper.map(sm, StudyMaterialResponseDto.class)).toList();
		return new ApiResponse<>(true, "Study materials fetched successfully", responseDtos);
	}

	@Override
	@Transactional
	public ApiResponse<StudyMaterialResponseDto> updateStudyMaterial(Long id, StudyMaterialUpdateDto updateDto) {

		StudyMaterial studyMaterial = accessCheckHelper2(id);

		String message = "";

		if (updateDto.getTitle() != null) {
			if (studyMaterial.getTitle().equals(updateDto.getTitle())) {
				message = "Title is the same as the current one, no changes made.";
			} else {
				studyMaterial.setTitle(updateDto.getTitle());
				message = "Title updated successfully.";
			}
		}
		if (updateDto.getMaterialType() != null) {
			if (studyMaterial.getMaterialType().equals(updateDto.getMaterialType())) {
				message += " Material type is the same as the current one, no changes made.";
			} else {
				studyMaterial.setMaterialType(updateDto.getMaterialType());
				message += " Material type updated successfully.";
			}
		}
		if (updateDto.getUrl() != null) {
			if (studyMaterial.getUrl().equals(updateDto.getUrl())) {
				message += " URL is the same as the current one, no changes made.";
			} else {
				studyMaterial.setUrl(updateDto.getUrl());
				message += " URL updated successfully.";
			}
		}

		message = message != null ? message.trim() : "No changes made.";

		StudyMaterialResponseDto responseDto = modelMapper.map(studyMaterial, StudyMaterialResponseDto.class);

		return new ApiResponse<>(true, message, responseDto);
	}

	@Override
	@Transactional
	public ApiResponse<Void> removeStudyMaterialFromBatch(Long studyMaterialId, Long batchId) {

		accessCheckHelper(batchId);

		StudyMaterial studyMaterial = studyMaterialRepository.findById(studyMaterialId)
				.orElseThrow(() -> new ResourceNotFoundException("StudyMaterial", "id", studyMaterialId));
		Batch batch = batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", batchId));
		if (!studyMaterial.getBatches().contains(batch)) {
			throw new ResourceNotFoundException("StudyMaterial", "id",
					studyMaterialId + " not associated with Batch id " + batchId);
		}
		studyMaterial.removeBatch(batch);
		if (studyMaterial.getBatches().isEmpty()) {
			studyMaterialRepository.delete(studyMaterial);
		}
		return new ApiResponse<>(true, "Study material removed from batch successfully", null);
	}

	@Override
	@Transactional
	public ApiResponse<Void> removeStudyMaterialFromTopic(Long studyMaterialId, Long topicId) {

		StudyMaterial studyMaterial = accessCheckHelper2(studyMaterialId);

		Topic topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId + "."));

		if (!studyMaterial.getTopic().equals(topic)) {
			throw new ResourceNotFoundException("StudyMaterial", "id",
					studyMaterialId + " not associated with Topic id " + topicId + ".");
		}
		studyMaterial.removeTopic(topic);
		studyMaterialRepository.delete(studyMaterial);
		return new ApiResponse<>(true, "Study material removed from topic successfully", null);
	}

	private StudyMaterial accessCheckHelper2(Long studyMaterialId) {
		StudyMaterial studyMaterial = studyMaterialRepository.findById(studyMaterialId)
				.orElseThrow(() -> new ResourceNotFoundException("StudyMaterial", "id", studyMaterialId));

		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));
		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));

		if (!isAdmin) {
			Set<Batch> batches = studyMaterial.getBatches();

			boolean unauthorized = batches.stream().noneMatch(batch -> user.getEnrollments().stream().anyMatch(
					enrollment -> enrollment.getBatch().equals(batch) && enrollment.getIsTrainerEnrollmentByAdmin()));
			if (unauthorized) {
				throw new AuthorizationDeniedException(
						"Only trainers assigned to one of the batches can update this study material.");
			}

		}
		return studyMaterial;
	}

	private void readAccessCheckHelper(Long batchId) {
		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));

		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));

		if (!isAdmin) {
			boolean enrolled = user.getEnrollments().stream()
					.anyMatch(enrollment -> enrollment.getBatch().getId().equals(batchId));

			if (!enrolled) {
				throw new AuthorizationDeniedException(
						"Only enrolled users can view topics or study materials of this batch.");
			}
		}
	}

}
