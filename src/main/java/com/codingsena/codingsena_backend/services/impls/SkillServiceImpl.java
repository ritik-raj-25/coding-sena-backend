package com.codingsena.codingsena_backend.services.impls;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.SkillCreateDto;
import com.codingsena.codingsena_backend.dtos.SkillDto;
import com.codingsena.codingsena_backend.entities.Skill;
import com.codingsena.codingsena_backend.exceptions.ResourceAlreadyExistException;
import com.codingsena.codingsena_backend.repositories.SkillRepository;
import com.codingsena.codingsena_backend.services.SkillService;

@Service
public class SkillServiceImpl implements SkillService {
	
	private ModelMapper modelMapper;
	private SkillRepository skillRepository;
	
	public SkillServiceImpl(ModelMapper modelMapper, SkillRepository skillRepository) {
		super();
		this.modelMapper = modelMapper;
		this.skillRepository = skillRepository;
	}

	@Override
	@Transactional
	public ApiResponse<SkillDto> createSkill(SkillCreateDto skillCreateDto) {
		
		if(!skillRepository.existsByTitle(skillCreateDto.getTitle())) {
			Skill skill = modelMapper.map(skillCreateDto, Skill.class);
			Skill savedSkill = skillRepository.save(skill);
			SkillDto skillDto = modelMapper.map(savedSkill, SkillDto.class);
			return new ApiResponse<>(true, "Skill created successfully", skillDto);
		}
		else {
			throw new ResourceAlreadyExistException("Skill", "title", skillCreateDto.getTitle());
		}
		
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<SkillDto>> getSkills() {
		List<Skill> skills = skillRepository.findAll();
		List<SkillDto> skillDtos = new ArrayList<>();
		skills.forEach(skill -> {
			SkillDto skillDto = modelMapper.map(skill, SkillDto.class);
			skillDtos.add(skillDto);
		});
		
		return new ApiResponse<>(true, "Skills fetched successfully.", skillDtos);
	}

}
