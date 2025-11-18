package com.codingsena.codingsena_backend.services.impls;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.TopicRequestDto;
import com.codingsena.codingsena_backend.dtos.TopicResponseDto;
import com.codingsena.codingsena_backend.entities.Batch;
import com.codingsena.codingsena_backend.entities.Topic;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.exceptions.ResourceAlreadyExistException;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.repositories.BatchRepository;
import com.codingsena.codingsena_backend.repositories.TopicRepository;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.codingsena.codingsena_backend.services.TopicService;
import com.codingsena.codingsena_backend.utils.RoleType;

@Service
public class TopicServiceImpl implements TopicService {
	
	private TopicRepository topicRepository;
	private ModelMapper modelMapper;
	private BatchRepository batchRepository;
	private UserRepository userRepository;
	
	public TopicServiceImpl(TopicRepository topicRepository, ModelMapper modelMapper, BatchRepository batchRepository, UserRepository userRepository) {
		this.topicRepository = topicRepository;
		this.modelMapper = modelMapper;
		this.batchRepository = batchRepository;
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional
	public ApiResponse<TopicResponseDto> createTopic(TopicRequestDto topicRequestDto, Long batchId) {
		
		accessCheckHelper(batchId);
		
		Topic topic = topicRepository.findByName(topicRequestDto.getName()).orElse(null);
		Batch batch = batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", batchId));
		
		
		String message = null;
		if (topic != null) {
			if(batch.getTopics().contains(topic)) {
				throw new ResourceAlreadyExistException("Topic", "name", topicRequestDto.getName());
			}
			
			message = "Topic linked to batch successfully.";
		}
		else {
			Topic newTopic = modelMapper.map(topicRequestDto, Topic.class);
			topic = topicRepository.save(newTopic); // saved topic = topic
			message = "Topic created and linked to batch successfully.";
		}
		
		batch.addTopic(topic);
		TopicResponseDto topicResponseDto = modelMapper.map(topic, TopicResponseDto.class);
		
		return new ApiResponse<>(true, message, topicResponseDto);
		
	}

	private void accessCheckHelper(Long batchId) {
		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user =  userRepository.findByEmail(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));
		
		boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));
		
		if(!isAdmin) {
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
	public ApiResponse<TopicResponseDto> getTopicById(Long batchId ,Long topicId) {
		readAccessCheckHelper(batchId);
		Topic topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
		TopicResponseDto topicResponseDto = modelMapper.map(topic, TopicResponseDto.class);
		return new ApiResponse<>(true, "Topic fetched successfully.", topicResponseDto);
	}

	@Override
	@Transactional
	public ApiResponse<TopicResponseDto> updateTopic(Long topicId, Long batchId, TopicRequestDto topicRequestDto) {
		
		accessCheckHelper(batchId);
		
		Topic topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
		
		Batch batch = batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", batchId));
		
		// CASE 1: Topic is not linked to the batch
		if(!batch.getTopics().contains(topic)) {
			throw new ResourceNotFoundException("Topic and Batch", "topicId and batchId", topicId+ " and " + batchId + " respectively");
		}
		
		String message = null;
		
		// CASE 2: Topic name is same as before
		if(topic.getName().equals(topicRequestDto.getName())) {
			message = "Topic name is same as before. No changes made.";
			TopicResponseDto topicResponseDto = modelMapper.map(topic, TopicResponseDto.class);
		    return new ApiResponse<>(true, message, topicResponseDto);
		}
		
		// CASE 3: Topic name is different from before
		
		// CASE 3.1: New name already exists
		Topic newTopic = topicRepository.findByName(topicRequestDto.getName()).orElse(null);
		if(newTopic != null) {
			if(batch.getTopics().contains(newTopic)) {
				throw new ResourceAlreadyExistException("Topic", "name", topicRequestDto.getName());
			}
			batch.removeTopic(topic); // unlink old topic from batch
			batch.addTopic(newTopic); // link new topic to batch
			message = "Topic updated successfully by linking to existing topic.";
		}
		else { // CASE 3.2: New name does not exist
			newTopic = modelMapper.map(topicRequestDto, Topic.class);
			newTopic = topicRepository.save(newTopic);
			batch.removeTopic(topic); // unlink old topic from batch
			batch.addTopic(newTopic); // link new topic to batch
			message = "Topic updated successfully by creating and linking to new topic.";
		}
		
		TopicResponseDto topicResponseDto = modelMapper.map(newTopic, TopicResponseDto.class);
		
		return new ApiResponse<>(true, message, topicResponseDto);
	}

	@Override
	@Transactional
	public ApiResponse<Void> removeTopic(Long topicId, Long batchId) {
		
		accessCheckHelper(batchId);
		
		
		Topic topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
		Batch batch = batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", batchId));
		if(!batch.getTopics().contains(topic)) {
			throw new ResourceNotFoundException("Topic and Batch", "topicId and batchId", topicId+ " and " + batchId + " respectively");
		}
		batch.removeTopic(topic);
		return new ApiResponse<>(true, "Topic removed from batch successfully.", null);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<TopicResponseDto>> getAllTopicsByBatchId(Long batchId) {
		readAccessCheckHelper(batchId);
		List<Topic> topics = topicRepository.findAllTopicsByBatchId(batchId);
		List<TopicResponseDto> topicResponseDtos = fetchTopicsHelper(topics);
		return new ApiResponse<>(true, "Topics fetched successfully.", topicResponseDtos);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<TopicResponseDto>> searchTopicsByName(String name, Long batchId) {
		readAccessCheckHelper(batchId);
		List<Topic> topics = topicRepository.findAllTopicsByBatchIdContainingName(batchId, name);
		List<TopicResponseDto> topicResponseDtos = fetchTopicsHelper(topics);
		return new ApiResponse<>(true, "Topics fetched successfully.", topicResponseDtos);
	}

	private List<TopicResponseDto> fetchTopicsHelper(List<Topic> topics) {
		List<TopicResponseDto> topicResponseDtos = topics.stream().map( topic  -> {
			TopicResponseDto dto = modelMapper.map(topic, TopicResponseDto.class);
			return dto;
		}).collect(Collectors.toList());
		return topicResponseDtos;
	}
	
	private void readAccessCheckHelper(Long batchId) {
	    String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
	    User user = userRepository.findByEmail(loggedInUser)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));

	    boolean isAdmin = user.getRoles().stream()
	            .anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));

	    if (!isAdmin) {
	        boolean enrolled = user.getEnrollments().stream()
	                .anyMatch(enrollment -> enrollment.getBatch().getId().equals(batchId));

	        if (!enrolled) {
	            throw new AuthorizationDeniedException(
	                "Only enrolled users can view topics or study materials of this batch."
	            );
	        }
	    }
	}

	
}
