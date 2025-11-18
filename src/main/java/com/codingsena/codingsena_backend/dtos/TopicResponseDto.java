package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponseDto {
	private Long id;
	private String name;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String createdBy;
}
