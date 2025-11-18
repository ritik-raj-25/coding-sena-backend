package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;

import com.codingsena.codingsena_backend.utils.MaterialType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudyMaterialResponseDto {
	private Long id;
	private String title;
	private MaterialType materialType;
	private String url;
	private LocalDateTime uploadedAt;
	private LocalDateTime updatedAt;
	private String uploadedBy; 
}
