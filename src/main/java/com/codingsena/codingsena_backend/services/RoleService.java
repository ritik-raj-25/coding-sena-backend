package com.codingsena.codingsena_backend.services;

import com.codingsena.codingsena_backend.entities.Role;

public interface RoleService {
	void createRoleIfNotExists(Role role);
}
