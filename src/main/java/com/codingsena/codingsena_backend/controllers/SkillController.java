package com.codingsena.codingsena_backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.SkillCreateDto;
import com.codingsena.codingsena_backend.dtos.SkillDto;
import com.codingsena.codingsena_backend.services.SkillService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class SkillController {
	
	private SkillService skillService;
	
	@Value("${base.url}")
	private String baseUrl;

	public SkillController(SkillService skillService) {
		super();
		this.skillService = skillService;
	}
	
	@PostMapping("/admin/skills")
	public ResponseEntity<ApiResponse<SkillDto>> createSkill(@Valid @RequestBody SkillCreateDto skillDto) {
		ApiResponse<SkillDto> response = skillService.createSkill(skillDto);
		URI location = UriComponentsBuilder.fromUriString(baseUrl + "/api/skills/")
				.path("{id}")
				.buildAndExpand(response.getResource().getId())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@GetMapping("/skills")
	public ResponseEntity<ApiResponse<List<SkillDto>>> getSkills() {
		ApiResponse<List<SkillDto>> response = skillService.getSkills();
		return ResponseEntity.ok(response);
	}
}
