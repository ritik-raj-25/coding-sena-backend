package com.codingsena.codingsena_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codingsena.codingsena_backend.entities.Batch;
import com.codingsena.codingsena_backend.entities.Enrollment;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.utils.EnrollmentStatus;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{

	Boolean existsByUserAndBatch(User user, Batch batch);
	
	Optional<Enrollment> findByUserAndBatch(User user, Batch batch);

	Integer countByBatchIdAndStatus(Long id, EnrollmentStatus active);

	List<Enrollment> findByUserIdAndStatusAndIsTrainerEnrollmentByAdmin(Long userId, EnrollmentStatus active, boolean b);

	List<Enrollment> findByUserIdAndStatus(Long id, EnrollmentStatus active);

}
