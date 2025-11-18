package com.codingsena.codingsena_backend.dtos;

import com.codingsena.codingsena_backend.utils.RoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class RoleResponseDto {
	private Long id;
	private RoleType roleName;
}
