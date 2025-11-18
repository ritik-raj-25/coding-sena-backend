package com.codingsena.codingsena_backend.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponseDto {
	private Long id;
	private String name;
	private String profilePicUrl;
	private String email;
	private String location;
	private String college;
	private LocalDate dob;
	private String nickName;
	private Boolean isDeleted;
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime lastUpdatedAt;
	private Set<SkillDto> skills;
	private Set<RoleResponseDto> roles;
}
