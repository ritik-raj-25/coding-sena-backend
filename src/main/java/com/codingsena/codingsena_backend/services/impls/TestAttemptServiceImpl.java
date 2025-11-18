package com.codingsena.codingsena_backend.services.impls;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.MCQAttemptResponseDTO;
import com.codingsena.codingsena_backend.dtos.MCQResponseDto;
import com.codingsena.codingsena_backend.dtos.TestAttemptResponseDTO;
import com.codingsena.codingsena_backend.entities.MCQ;
import com.codingsena.codingsena_backend.entities.MCQAttempt;
import com.codingsena.codingsena_backend.entities.Test;
import com.codingsena.codingsena_backend.entities.TestAttempt;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.exceptions.TestAttemptNotAllowedException;
import com.codingsena.codingsena_backend.repositories.MCQAttemptRepository;
import com.codingsena.codingsena_backend.repositories.MCQRepository;
import com.codingsena.codingsena_backend.repositories.TestAttemptRepository;
import com.codingsena.codingsena_backend.repositories.TestRepository;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.codingsena.codingsena_backend.services.BlockchainService;
import com.codingsena.codingsena_backend.services.TestAttemptService;
import com.codingsena.codingsena_backend.utils.AttemptStatus;
import com.codingsena.codingsena_backend.utils.HashGenerator;
import com.codingsena.codingsena_backend.utils.RoleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TestAttemptServiceImpl implements TestAttemptService {

	@Value("${spring.ai.openai.chat.options.model}")
	private String genAiModel;

	private ChatModel chatModel;
	private TestAttemptRepository testAttemptRepository;
	private MCQAttemptRepository mcqAttemptRepository;
	private TestRepository testRepository;
	private MCQRepository mcqRepository;
	private UserRepository userRepository;
	private ModelMapper modelMapper;
	private ObjectMapper objectMapper;
	private BlockchainService blockchainService;

	public TestAttemptServiceImpl(TestAttemptRepository testAttemptRepository,
			MCQAttemptRepository mcqAttemptRepository, TestRepository testRepository, MCQRepository mcqRepository,
			UserRepository userRepository, ModelMapper modelMapper, ObjectMapper objectMapper, ChatModel chatModel, BlockchainService blockchainService) {
		super();
		this.testAttemptRepository = testAttemptRepository;
		this.mcqAttemptRepository = mcqAttemptRepository;
		this.testRepository = testRepository;
		this.mcqRepository = mcqRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.objectMapper = objectMapper;
		this.chatModel = chatModel;
		this.blockchainService = blockchainService;
	}

	@Override
	@Transactional
	public ApiResponse<TestAttemptResponseDTO> startTest(Long testId){
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));

		accessCheckHelper(test.getBatch().getId());

		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

		Integer attempts = testAttemptRepository.countByTestIdAndUserId(testId, user.getId());

		if (attempts >= test.getMaxAttempts()) {
			throw new TestAttemptNotAllowedException("Maximum number of attempts reached for this test.");
		}

		if (test.getStartTime() != null && LocalDateTime.now().isBefore(test.getStartTime())) {
			throw new TestAttemptNotAllowedException("Test cannot be started before the scheduled time.");
		}

		if (test.getEndTime() != null && LocalDateTime.now().isAfter(test.getEndTime())) {
			throw new TestAttemptNotAllowedException("Test cannot be started as the end time has passed.");
		}

		if (user.getLastTestAttemptId() != null) {
			TestAttempt lastAttempt = testAttemptRepository.findById(user.getLastTestAttemptId())
					.orElseThrow(() -> new ResourceNotFoundException("TestAttempt", "id", user.getLastTestAttemptId()));
			if (lastAttempt.getStatus() == AttemptStatus.IN_PROGRESS) {
				throw new TestAttemptNotAllowedException(
						"You have an ongoing test. Please submit it before starting a new one.");
			}
		}

		TestAttempt testAttempt = new TestAttempt();
		testAttempt.addTest(test);
		testAttempt.addUser(user);
		testAttempt.setStatus(AttemptStatus.IN_PROGRESS);
		testAttempt.setAttemptNumber(attempts + 1);
		testAttempt.setTotalMarks((double) test.getTotalMarks());

		TestAttempt savedAttempt = testAttemptRepository.save(testAttempt);
		
		user.setLastTestAttemptId(savedAttempt.getId());

		TestAttemptResponseDTO testAttemptResponseDto = new TestAttemptResponseDTO();
		testAttemptResponseDto.setId(savedAttempt.getId());
		testAttemptResponseDto.setTestId(test.getId());
		testAttemptResponseDto.setUserEmail(user.getEmail());
		testAttemptResponseDto.setAttemptNumber(savedAttempt.getAttemptNumber());
		testAttemptResponseDto.setStartedAt(savedAttempt.getStartedAt());

		return new ApiResponse<>(true, "Test started successfully", testAttemptResponseDto);
	}

	@Override
	@Transactional
	public ApiResponse<MCQAttemptResponseDTO> saveMCQ(Long attemptId, Long mcqId, String selectedOption) {
		TestAttempt testAttempt = testAttemptRepository.findById(attemptId)
				.orElseThrow(() -> new ResourceNotFoundException("TestAttempt", "id", attemptId));

		accessCheckHelper(testAttempt.getTest().getBatch().getId());

		if (testAttempt.getStatus() != AttemptStatus.IN_PROGRESS) {
			throw new TestAttemptNotAllowedException("Cannot save MCQ for a test that is not in progress.");
		}

		MCQ mcq = mcqRepository.findById(mcqId).orElseThrow(() -> new ResourceNotFoundException("MCQ", "id", mcqId));
		MCQAttempt mcqAttempt = mcqAttemptRepository.findByTestAttemptIdAndMcqId(attemptId, mcqId)
				.orElse(new MCQAttempt());
		mcqAttempt.setSelectedOption(selectedOption);
		mcqAttempt.addMcq(mcq);
		mcqAttempt.addTestAttempt(testAttempt);
		mcqAttempt
				.setIsCorrect(mcqAttempt.getSelectedOption() != null && mcq.getCorrectOption().equals(selectedOption));

		MCQAttempt savedMcqAttempt = mcqAttemptRepository.save(mcqAttempt);

		MCQAttemptResponseDTO mcqAttemptResponseDto = modelMapper.map(savedMcqAttempt, MCQAttemptResponseDTO.class);
		mcqAttemptResponseDto.setMcqResponseDto(modelMapper.map(mcq, MCQResponseDto.class));

		return new ApiResponse<>(true, "MCQ saved successfully", mcqAttemptResponseDto);
	}

	@Override
	@Transactional
	public ApiResponse<TestAttemptResponseDTO> submitTest(Long attemptId) throws Exception, NoSuchAlgorithmException {
		TestAttempt attempt = testAttemptRepository.findById(attemptId)
				.orElseThrow(() -> new ResourceNotFoundException("TestAttempt", "id", attemptId));

		accessCheckHelper(attempt.getTest().getBatch().getId());

		Test test = attempt.getTest();
		String message = "";
		TestAttemptResponseDTO testAttemptResponseDto = null;
		if (LocalDateTime.now().isAfter(attempt.getStartedAt().plusMinutes(test.getDuration()))) {
			if (attempt.getStatus() != AttemptStatus.AUTO_SUBMITTED) {
				attempt = autoSubmitTest(attempt);
			}
			testAttemptResponseDto = modelMapper.map(attempt, TestAttemptResponseDTO.class);
			message = "Test submitted automatically as the duration has elapsed.";
		} else {
			if (attempt.getStatus() == AttemptStatus.SUBMITTED_MANUALLY) {
				throw new TestAttemptNotAllowedException("Test has already been submitted.");
			}

			double score = mcqAttemptRepository.calculateScore(attemptId);

			attempt.setScore(score);
			attempt.setStatus(AttemptStatus.SUBMITTED_MANUALLY);
			attempt.setSubmittedAt(LocalDateTime.now());

			TestAttempt savedAttempt = testAttemptRepository.save(attempt);
			testAttemptResponseDto = modelMapper.map(savedAttempt, TestAttemptResponseDTO.class);
			testAttemptResponseDto.setTestId(savedAttempt.getTest().getId());
			testAttemptResponseDto.setUserEmail(savedAttempt.getUser().getEmail());
			Set<Long> attemptedMcqIds = new HashSet<>();
			List<MCQAttemptResponseDTO> mcqAttemptResponseDtos = savedAttempt.getMcqAttempts().stream()
					.map(mcqAttempt -> {
						MCQAttemptResponseDTO dto = modelMapper.map(mcqAttempt, MCQAttemptResponseDTO.class);
						MCQ mcq = mcqAttempt.getMcq();
						dto.setMcqResponseDto(modelMapper.map(mcq, MCQResponseDto.class));
						attemptedMcqIds.add(mcq.getId());
						return dto;
					}).collect(Collectors.toList());

			// Unattempted MCQs
			test.getMcqs().stream().forEach(mcq -> {
				if (!attemptedMcqIds.contains(mcq.getId())) {
					MCQResponseDto mcqDto = modelMapper.map(mcq, MCQResponseDto.class);
					MCQAttemptResponseDTO mcqAttemptDto = new MCQAttemptResponseDTO();
					mcqAttemptDto.setMcqResponseDto(mcqDto);
					mcqAttemptResponseDtos.add(mcqAttemptDto);
				}
			});
			
			// Store result on blockchain
			String data = savedAttempt.getUser().getId() + " - "+ savedAttempt.getTest().getId() + " - " + savedAttempt.getId() + " - " + savedAttempt.getScore() + " - " + savedAttempt.getStartedAt().toString();
			String hashOfData = HashGenerator.generateHash(data);
			blockchainService.storeResult(savedAttempt.getUser().getId(), savedAttempt.getTest().getId(), savedAttempt.getId(), savedAttempt.getScore(), hashOfData);
			
			testAttemptResponseDto.setMcqAttempts(mcqAttemptResponseDtos);
			message = "Test submitted successfully.";
		}

		return new ApiResponse<>(true, message, testAttemptResponseDto);
	}

	@Transactional
	public TestAttempt autoSubmitTest(TestAttempt attempt) throws Exception, NoSuchAlgorithmException {
		if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
			return attempt;
		}

		double score = mcqAttemptRepository.calculateScore(attempt.getId());

		attempt.setScore(score);
		attempt.setStatus(AttemptStatus.AUTO_SUBMITTED);
		attempt.setSubmittedAt(LocalDateTime.now());
		TestAttempt savedAttempt = testAttemptRepository.save(attempt);
		
		// Store result on blockchain with hash of data as user email
		String data = savedAttempt.getUser().getId() + " - "+ savedAttempt.getTest().getId() + " - " + savedAttempt.getId() + " - " + savedAttempt.getScore() + " - " + savedAttempt.getStartedAt().toString();
		String hashOfData = HashGenerator.generateHash(data);
		blockchainService.storeResult(savedAttempt.getUser().getId(), savedAttempt.getTest().getId(), savedAttempt.getId(), savedAttempt.getScore(), hashOfData);
		
		return savedAttempt;
	}

	private void accessCheckHelper(Long batchId) { // to ensure only enrolled learners can attempt tests
		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));

		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));

		if (!isAdmin) {
			boolean enrolled = user.getEnrollments().stream()
					.anyMatch(enrollment -> enrollment.getBatch().getId().equals(batchId));

			if (!enrolled) {
				throw new AuthorizationDeniedException("Only enrolled users can view test(s) of this batch.");
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<Integer> getRemainingAttempts(Long testId) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));
		Integer attempts = testAttemptRepository.countByTestIdAndUserId(testId, user.getId());
		Integer remainingAttempts = test.getMaxAttempts() - attempts;
		return new ApiResponse<>(true, "Remaining attempts fetched successfully", remainingAttempts);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<TestAttemptResponseDTO>> getAllAttemptsOfTest(Long testId) throws NoSuchAlgorithmException, Exception {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));
		List<TestAttempt> attempts = testAttemptRepository.findByTestIdAndUserId(testId, user.getId());

		Set<MCQ> mcqs = test.getMcqs();

		List<TestAttemptResponseDTO> attemptDtos = attempts.stream().map((attempt) -> {
			TestAttemptResponseDTO dto = modelMapper.map(attempt, TestAttemptResponseDTO.class);
			dto.setTestId(test.getId());
			dto.setUserEmail(userEmail);
			Set<Long> attemptedMcqIds = new HashSet<>();
			List<MCQAttemptResponseDTO> mcqAttemptResponseDtos = attempt.getMcqAttempts().stream().map(mcqAttempt -> {
				MCQAttemptResponseDTO mcqDto = modelMapper.map(mcqAttempt, MCQAttemptResponseDTO.class);
				MCQ mcq = mcqAttempt.getMcq();
				mcqDto.setMcqResponseDto(modelMapper.map(mcq, MCQResponseDto.class));
				attemptedMcqIds.add(mcq.getId());
				return mcqDto;
			}).collect(Collectors.toList());

			// Unattempted MCQs
			mcqs.stream().forEach(mcq -> {
				if (!attemptedMcqIds.contains(mcq.getId())) {
					MCQResponseDto mcqDto = modelMapper.map(mcq, MCQResponseDto.class);
					MCQAttemptResponseDTO mcqAttemptDto = new MCQAttemptResponseDTO();
					mcqAttemptDto.setMcqResponseDto(mcqDto);
					mcqAttemptResponseDtos.add(mcqAttemptDto);
				}
			});

			dto.setMcqAttempts(mcqAttemptResponseDtos);
			
			// Verify result on blockchain
			try {
				LocalDateTime cutoffTime = LocalDateTime.of(2025, 10, 29, 1, 45, 0);
				if(attempt.getStartedAt().isAfter(cutoffTime)) {
					String data = attempt.getUser().getId() + " - "+ attempt.getTest().getId() + " - " + attempt.getId() + " - " + attempt.getScore() + " - " + attempt.getStartedAt().toString();
					String hashOfData = HashGenerator.generateHash(data);
					dto.setIsTestAttemptTempered(!blockchainService.verifyResult(attempt.getId(), hashOfData));
				}
				else {
					dto.setIsTestAttemptTempered(false); // Skip verification for attempts before cutoff time
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return dto;
		}).collect(Collectors.toList());

		return new ApiResponse<>(true, "Test attempts fetched successfully", attemptDtos);
	}

	// Get AI suggestion for a test attempt
	@Override
	public ApiResponse<String> getAISuggestionForTestAttempt(Long attemptId) throws JsonProcessingException {
		// Attempted MCQs
		List<MCQAttempt> mcqAttempts = mcqAttemptRepository.findByTestAttemptId(attemptId);
		List<MCQAttemptResponseDTO> mcqAttemptResponseDtos = mcqAttempts.stream().map(mcqAttempt -> {
			MCQAttemptResponseDTO dto = modelMapper.map(mcqAttempt, MCQAttemptResponseDTO.class);
			MCQ mcq = mcqAttempt.getMcq();
			dto.setMcqResponseDto(modelMapper.map(mcq, MCQResponseDto.class));
			return dto;
		}).collect(Collectors.toList());

		String mcqAttemptsLLM = objectMapper.writeValueAsString(mcqAttemptResponseDtos);

		// All MCQs
		TestAttempt testAttempt = testAttemptRepository.findById(attemptId).orElseThrow(
				() -> new ResourceNotFoundException("TestAttempt", "id", attemptId));
		Long testId = testAttempt.getTest().getId();
		List<MCQ> allMcqs = mcqRepository.findByTestId(testId);
		List<MCQResponseDto> allMcqDtos = allMcqs.stream().map(mcq -> modelMapper.map(mcq, MCQResponseDto.class))
				.collect(Collectors.toList());
		String allMcqsLLM = objectMapper.writeValueAsString(allMcqDtos);
		
		String testDuration = testAttempt.getTest().getDuration().toString();
		
		SystemMessage systemMessage = new SystemMessage(
				"""
						    You are an expert exam evaluator. You have to respond to a student like a personal mentor.
						   
						   	Note: id fields in the data are only for reference and should not be included in your analysis at all (not even as question number).

						    Your task is to analyze a student's test attempt and respond to the student based on the following data:
						    1. The list of all questions with their difficulty levels and correct answers.
						    2. The student's selected options for each question.
						    3. The total duration of the test.
						    4. Time taken by the student to complete the test.

						    Evaluate the student's overall performance, identify their weak areas, and provide improvement suggestions.

						    Please follow this response format strictly:

						    {
						      "overallAnalysis": "<brief summary of student's overall performance>",
						      "strengths": ["<list of strengths>"],
						      "weaknesses": ["<list of weaknesses>"],
						      "suggestions": ["<specific and actionable improvement suggestions>"]
						    }

						    Respond only with the JSON string, without any extra explanation. You can use markdown formatting for readability.
						""");

		UserMessage userMessage = new UserMessage("""
				All Questions: %s

				Student's Attempted Questions: %s
				
				Test Duration (in minutes): %s

				Analyze the student's performance and provide feedback as per the specified format.
				""".formatted(allMcqsLLM, mcqAttemptsLLM, testDuration));

		ChatResponse chatResponse = chatModel.call(new Prompt(List.of(systemMessage, userMessage),
				OpenAiChatOptions.builder().model(genAiModel).build()));

		String aiResponse = chatResponse.getResult().getOutput().getText();
		String cleaned = aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();

		return new ApiResponse<>(true, "AI suggestion generated successfully", cleaned);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<TestAttemptResponseDTO>> getTestReport(Long testId) { // get all attempts for a test and also verify with blockchain data
		
		List<TestAttempt> attempts = testAttemptRepository.findByTestId(testId);
		
		List<TestAttemptResponseDTO> attemptDtos = attempts.stream().map((attempt) -> {
			TestAttemptResponseDTO dto = modelMapper.map(attempt, TestAttemptResponseDTO.class);
			dto.setTestId(attempt.getTest().getId());
			dto.setUserEmail(attempt.getUser().getEmail());
			
			// Verify result on blockchain
			try {
				LocalDateTime cutoffTime = LocalDateTime.of(2025, 10, 29, 1, 45, 0);
				if(attempt.getStartedAt().isAfter(cutoffTime)) {
					String data = attempt.getUser().getId() + " - "+ attempt.getTest().getId() + " - " + attempt.getId() + " - " + attempt.getScore() + " - " + attempt.getStartedAt().toString();
					String hashOfData = HashGenerator.generateHash(data);
					dto.setIsTestAttemptTempered(!blockchainService.verifyResult(attempt.getId(), hashOfData));
				}
				else {
					dto.setIsTestAttemptTempered(false); // Skip verification for attempts before cutoff time
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return dto;
		}).collect(Collectors.toList());
		
		
		return new ApiResponse<>(true, "Test report fetched successfully", attemptDtos);
	}
}
