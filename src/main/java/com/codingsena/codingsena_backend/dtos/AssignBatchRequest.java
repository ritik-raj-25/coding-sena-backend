package com.codingsena.codingsena_backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignBatchRequest {
    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email")
    private String userEmail;

    @NotNull(message = "Batch ID is required")
    private Long batchId;
}
