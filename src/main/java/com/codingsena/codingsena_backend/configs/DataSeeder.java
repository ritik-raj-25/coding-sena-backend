package com.codingsena.codingsena_backend.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.codingsena.codingsena_backend.entities.Role;
import com.codingsena.codingsena_backend.services.RoleService;
import com.codingsena.codingsena_backend.services.UserService;
import com.codingsena.codingsena_backend.utils.RoleType;

@Component
public class DataSeeder implements CommandLineRunner{
	
	private RoleService roleService;
	private UserService userService;

	public DataSeeder(RoleService roleService, UserService userService) {
		super();
		this.roleService = roleService;
		this.userService = userService;
	}



	@Override
	public void run(String... args) throws Exception {
		
		// seed roles
		Role roleAdmin = new Role();
		roleAdmin.setRoleName(RoleType.ROLE_ADMIN);
		
		Role roleTrainer = new Role();
		roleTrainer.setRoleName(RoleType.ROLE_TRAINER);
		
		Role roleLearner = new Role();
		roleLearner.setRoleName(RoleType.ROLE_LEARNER);
		
		roleService.createRoleIfNotExists(roleAdmin);
		roleService.createRoleIfNotExists(roleTrainer);
		roleService.createRoleIfNotExists(roleLearner);
		
		// seed admin
		userService.createAdminIfNotExists();
	}
}
