package com.codingsena.codingsena_backend.utils;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.entities.TestAttempt;
import com.codingsena.codingsena_backend.repositories.TestAttemptRepository;
import com.codingsena.codingsena_backend.repositories.TestRepository;
import com.codingsena.codingsena_backend.services.TestAttemptService;

@Component
public class TestAutoSubmitScheduler {
	private TestAttemptService testAttemptService;
	private TestAttemptRepository testAttemptRepository;
	
	public TestAutoSubmitScheduler(TestAttemptService testAttemptService, TestAttemptRepository testAttemptRepository, TestRepository testRepository) {
		super();
		this.testAttemptService = testAttemptService;
		this.testAttemptRepository = testAttemptRepository;
	}
	
	@Scheduled(fixedRate = 60000)
	@Transactional
	public void autoSubmitTests() throws NoSuchAlgorithmException, Exception {
		List<TestAttempt> attempts = testAttemptRepository.findByStatus(AttemptStatus.IN_PROGRESS);
		for(TestAttempt attempt : attempts) {
			LocalDateTime startedAt = attempt.getStartedAt();
			Integer duration = attempt.getTest().getDuration();
			LocalDateTime endTime = startedAt.plusMinutes(duration);
			if(LocalDateTime.now().isAfter(endTime)) {
				testAttemptService.autoSubmitTest(attempt);
			}
		}
	}
}
