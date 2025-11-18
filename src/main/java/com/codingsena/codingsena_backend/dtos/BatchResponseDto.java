package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.codingsena.codingsena_backend.utils.BatchValidity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchResponseDto {
	private Long id;
	private String coverPicUrl;
	private String batchName;
	private BatchValidity validity;
	private String curriculumUrl;
	private LocalDate startDate;
	private LocalDate endDate;
	private Long price;
	private LocalDateTime createdAt;
	private LocalDateTime lastUpdatedAt;
	private Long discount;
	private Integer noOfStudentsEnrolled;
}
