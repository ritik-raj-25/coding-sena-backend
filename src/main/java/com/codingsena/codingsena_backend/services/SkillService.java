package com.codingsena.codingsena_backend.services;

import java.util.List;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.SkillCreateDto;
import com.codingsena.codingsena_backend.dtos.SkillDto;

public interface SkillService {
	ApiResponse<SkillDto> createSkill(SkillCreateDto skillCreateDto);
	ApiResponse<List<SkillDto>> getSkills();
}
