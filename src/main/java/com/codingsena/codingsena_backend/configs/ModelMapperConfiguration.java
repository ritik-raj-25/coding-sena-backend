package com.codingsena.codingsena_backend.configs;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codingsena.codingsena_backend.dtos.SkillDto;
import com.codingsena.codingsena_backend.entities.Skill;

@Configuration
public class ModelMapperConfiguration {
	
	@Bean
	public ModelMapper modelMapper() {
	    ModelMapper modelMapper = new ModelMapper();
	    modelMapper.getConfiguration()
	               .setMatchingStrategy(MatchingStrategies.STRICT);
	    
	    // Simple Nested Type Maps
	    modelMapper.createTypeMap(Skill.class, SkillDto.class);
	    
	    return modelMapper;
	}
	
}
