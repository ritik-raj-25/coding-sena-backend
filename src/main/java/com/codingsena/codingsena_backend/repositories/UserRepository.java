package com.codingsena.codingsena_backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.codingsena.codingsena_backend.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
	Boolean existsByEmail(String email);
	
	@Query(
			value = "SELECT u FROM User u LEFT JOIN FETCH u.skills",
			countQuery = "SELECT COUNT(u) FROM User u"
	)
	Page<User> findAllUser(Pageable pageable);
}
