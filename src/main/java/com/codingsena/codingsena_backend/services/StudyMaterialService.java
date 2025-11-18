package com.codingsena.codingsena_backend.services;

import java.util.List;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.StudyMaterialRequestDto;
import com.codingsena.codingsena_backend.dtos.StudyMaterialResponseDto;
import com.codingsena.codingsena_backend.dtos.StudyMaterialUpdateDto;

public interface StudyMaterialService {
	ApiResponse<StudyMaterialResponseDto> createStudyMaterial(StudyMaterialRequestDto requestDto, Long topicId, Long batchId);
	ApiResponse<StudyMaterialResponseDto> getStudyMaterialById(Long batchId, Long topicId, Long studyMaterialId);
	ApiResponse<List<StudyMaterialResponseDto>> getAllStudyMaterialsByTopicAndBatch(Long topicId, Long batchId);
	ApiResponse<Void> removeStudyMaterialFromBatch(Long studyMaterialId, Long batchId);
	ApiResponse<Void> removeStudyMaterialFromTopic(Long studyMaterialId, Long topicId);
	ApiResponse<StudyMaterialResponseDto> updateStudyMaterial(Long id, StudyMaterialUpdateDto updateDto);
}
