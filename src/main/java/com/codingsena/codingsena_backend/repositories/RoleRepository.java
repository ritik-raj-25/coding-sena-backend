package com.codingsena.codingsena_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.Role;
import com.codingsena.codingsena_backend.utils.RoleType;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Boolean existsByRoleName(RoleType roleName);
	Optional<Role> findByRoleName(RoleType roleName);
}
