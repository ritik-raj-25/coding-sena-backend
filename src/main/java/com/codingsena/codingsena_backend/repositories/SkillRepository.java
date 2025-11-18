package com.codingsena.codingsena_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>{

	boolean existsByTitle(String title);
	
}
