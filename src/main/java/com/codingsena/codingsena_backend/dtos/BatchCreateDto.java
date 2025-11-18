package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDate;

import com.codingsena.codingsena_backend.utils.BatchValidity;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchCreateDto {
	@NotBlank(message = "Batch name can't be empty.")
	private String batchName;
	
	@NotNull(message = "Batch validity can't be empty.")
	private BatchValidity validity;
	
	@FutureOrPresent(message = "Batch start date can't be a past date.")
	@NotNull(message = "Start date is required.")
	private LocalDate startDate;
	
	@FutureOrPresent(message = "Batch end date can't be a past date.")
	@NotNull(message = "End date is required.")
	private LocalDate endDate;
	
	@NotNull(message = "Price is required.")
	@Min(value=0, message = "Batch price can't be negative.")
	private Long price; // for free batches price = 0
	
	@NotNull(message = "Discount is required. For no discount, Put '0'.")
	@Min(value = 0, message = "Batch discount can't be nagative.")
	private Long discount;
}
