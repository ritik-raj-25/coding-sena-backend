package com.codingsena.codingsena_backend.services.impls;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.entities.Role;
import com.codingsena.codingsena_backend.repositories.RoleRepository;
import com.codingsena.codingsena_backend.services.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
	
	private RoleRepository roleRepository;
	
	public RoleServiceImpl(RoleRepository roleRepository) {
		super();
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional
	public void createRoleIfNotExists(Role role) {
	    boolean exists = roleRepository.existsByRoleName(role.getRoleName());
	    if (!exists) {
	        roleRepository.save(role);
	    }
	}

}
