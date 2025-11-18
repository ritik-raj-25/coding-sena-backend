package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDateTime;

import com.codingsena.codingsena_backend.utils.EnrollmentStatus;
import com.codingsena.codingsena_backend.utils.EnrollmentType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDto {
	private Long enrollmentId;
	private Long batchId;
	private String batchName;
	private Long userId;
	private String userEmail; // User-Name
	private EnrollmentType enrollmentType;
	private EnrollmentStatus status;
	private LocalDateTime createdAt;
	private String checkoutUrl;  // only for student enrollments
}
