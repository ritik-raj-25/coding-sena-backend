package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDate;

import com.codingsena.codingsena_backend.utils.BatchValidity;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchUpdateDto {
	@Size(min = 1, message = "Batch name must have at least one character.")
	private String batchName;
	
	private BatchValidity validity;
	
	@FutureOrPresent(message = "Batch start date can't be a past date.")
	private LocalDate startDate;
	
	@FutureOrPresent(message = "Batch end date can't be a past date.")
	private LocalDate endDate;
	
	@Min(value=0, message = "Batch price can't be negative.")
	private Long price; // for free batches price = 0
	
	@Min(value = 0, message = "Batch discount can't be nagative.")
	private Long discount;
}
