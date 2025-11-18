package com.codingsena.codingsena_backend.services;

import java.util.List;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.TopicRequestDto;
import com.codingsena.codingsena_backend.dtos.TopicResponseDto;

public interface TopicService {
	ApiResponse<TopicResponseDto> createTopic(TopicRequestDto topicRequestDto, Long batchId);
	ApiResponse<TopicResponseDto> getTopicById(Long batchId, Long topicId);
	ApiResponse<TopicResponseDto> updateTopic(Long topicId, Long batchId, TopicRequestDto topicRequestDto);
	ApiResponse<Void> removeTopic(Long topicId, Long batchId);
	ApiResponse<List<TopicResponseDto>> getAllTopicsByBatchId(Long batchId);
	ApiResponse<List<TopicResponseDto>> searchTopicsByName(String name, Long batchId);
}
