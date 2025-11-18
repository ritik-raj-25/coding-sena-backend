package com.codingsena.codingsena_backend.services.impls;

import java.time.LocalDateTime;
import java.util.List;

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
import com.codingsena.codingsena_backend.dtos.MCQCreateDto;
import com.codingsena.codingsena_backend.dtos.MCQResponseDto;
import com.codingsena.codingsena_backend.dtos.MCQUpdateDto;
import com.codingsena.codingsena_backend.entities.MCQ;
import com.codingsena.codingsena_backend.entities.Test;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.exceptions.MCQGenerationException;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.repositories.MCQRepository;
import com.codingsena.codingsena_backend.repositories.TestRepository;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.codingsena.codingsena_backend.services.MCQService;
import com.codingsena.codingsena_backend.utils.RoleType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MCQServiceImpl implements MCQService {

	@Value("${spring.ai.openai.chat.options.model}")
	private String genAiModel;

	private ChatModel chatModel;
	private MCQRepository mcqRepository;
	private TestRepository testRepository;
	private UserRepository userRepository;
	private ModelMapper modelMapper;
	private ObjectMapper objectMapper;

	public MCQServiceImpl(MCQRepository mcqRepository, TestRepository testRepository, ModelMapper modelMapper,
			ChatModel chatModel, ObjectMapper objectMapper, UserRepository userRepository) {
		this.mcqRepository = mcqRepository;
		this.testRepository = testRepository;
		this.modelMapper = modelMapper;
		this.chatModel = chatModel;
		this.objectMapper = objectMapper;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public ApiResponse<List<MCQResponseDto>> createMCQs(Long testId, Integer noOfMCQs, List<String> topics, String userInstructionMessage) {
		
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));
		
		accessCheckHelper(test.getBatch().getId());
		
		if(test.getMcqs() != null && !test.getMcqs().isEmpty()) {
			throw new IllegalStateException("MCQs have already been generated for this test.");
		}
		
		List<MCQCreateDto> mcqDtos = getMCQsAsJson(topics, noOfMCQs, test.getDifficultyLevel().name(), userInstructionMessage);
		
		List<MCQ> mcqs = mcqRepository.saveAll(
				mcqDtos.stream().map(dto -> {
					MCQ mcq = new MCQ();
					mcq.setQuestion(dto.getQuestion());
					mcq.setOptionA(dto.getOptions().get("A"));
					mcq.setOptionB(dto.getOptions().get("B"));
					mcq.setOptionC(dto.getOptions().get("C"));
					mcq.setOptionD(dto.getOptions().get("D"));
					mcq.setCorrectOption(dto.getAnswer());
					mcq.addTest(test);
					mcq.setDifficultyLevel(test.getDifficultyLevel());
					mcq.setMarks((float)test.getTotalMarks() / noOfMCQs); // equal marks distribution
					return mcq;
				}).toList()
			);
		
		List<MCQResponseDto> responseDtos = mcqs.stream()
				.map(mcq -> modelMapper.map(mcq, MCQResponseDto.class))
				.toList();
		
		return new ApiResponse<>(true, "MCQs created successfully", responseDtos);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<MCQResponseDto> getMCQById(Long mcqId) {
		MCQ mcq = mcqRepository.findById(mcqId)
				.orElseThrow(() -> new ResourceNotFoundException("MCQ", "id", mcqId));
		
		Test test = testRepository.findById(mcq.getTest().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", mcq.getTest().getId()));
		if(test.getStartTime().isAfter(LocalDateTime.now()) || test.getEndTime().isBefore(LocalDateTime.now())) {
			if(!accessCheckHelper(test.getBatch().getId())) {
				throw new AuthorizationDeniedException("You can view the MCQ of during the test time.");
			}
		}
		
		readAccessCheckHelper(mcq.getTest().getBatch().getId());
		MCQResponseDto responseDto = modelMapper.map(mcq, MCQResponseDto.class);
		return new ApiResponse<>(true, "MCQ fetched successfully", responseDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<MCQResponseDto>> getAllMCQsOfTest(Long testId) {
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));
		
		if(test.getStartTime().isAfter(LocalDateTime.now()) || test.getEndTime().isBefore(LocalDateTime.now())) {
			if(!accessCheckHelper(test.getBatch().getId())) {
				throw new AuthorizationDeniedException("You can view MCQs of this test only during the test time.");
			}
		}
		
		readAccessCheckHelper(test.getBatch().getId());
		List<MCQ> mcqs = mcqRepository.findByTestId(testId);
		List<MCQResponseDto> responseDtos = mcqs.stream()
				.map(mcq -> modelMapper.map(mcq, MCQResponseDto.class))
				.toList();
		return new ApiResponse<>(true, "MCQs fetched successfully", responseDtos);
	}

	@Override
	@Transactional
	public ApiResponse<MCQResponseDto> updateMCQ(Long mcqId, MCQUpdateDto mcqUpdateDto) {
		MCQ mcq = mcqRepository.findById(mcqId)
				.orElseThrow(() -> new ResourceNotFoundException("MCQ", "id", mcqId));
		accessCheckHelper(mcq.getTest().getBatch().getId());
		if(mcqUpdateDto.getQuestion() != null) {
			mcq.setQuestion(mcqUpdateDto.getQuestion());
		}
			
		if(mcqUpdateDto.getOptionA() != null) {
			mcq.setOptionA(mcqUpdateDto.getOptionA());
		}
		
		if(mcqUpdateDto.getOptionB() != null) {
			mcq.setOptionB(mcqUpdateDto.getOptionB());
		}
		if(mcqUpdateDto.getOptionC() != null) {
			mcq.setOptionC(mcqUpdateDto.getOptionC());
		}
		if(mcqUpdateDto.getOptionD() != null) {
			mcq.setOptionD(mcqUpdateDto.getOptionD());
		}
		if(mcqUpdateDto.getCorrectOption() != null) {
			mcq.setCorrectOption(mcqUpdateDto.getCorrectOption());
		}
		
		MCQ updatedMCQ = mcqRepository.save(mcq);
		MCQResponseDto responseDto = modelMapper.map(updatedMCQ, MCQResponseDto.class);
			
		return new ApiResponse<>(true, "MCQ updated successfully", responseDto);
	}
	
	private void readAccessCheckHelper(Long batchId) { // to ensure only enrolled learners can view MCQs
		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));

		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));

		if (!isAdmin) {
			boolean enrolled = user.getEnrollments().stream()
					.anyMatch(enrollment -> enrollment.getBatch().getId().equals(batchId));

			if (!enrolled) {
				throw new AuthorizationDeniedException(
						"Only enrolled users can view MCQ(s) of this batch.");
			}
		}
	}
	
	private Boolean accessCheckHelper(Long batchId) { // to ensure only admin or assigned trainer can create/ update/ delete / view MCQs
		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(loggedInUser)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loggedInUser));

		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleName().equals(RoleType.ROLE_ADMIN));

		if (!isAdmin) {
			boolean unauthorized = user.getEnrollments().stream()
					.noneMatch(enrollment -> enrollment.getBatch().getId().equals(batchId)
							&& enrollment.getIsTrainerEnrollmentByAdmin());
			if (unauthorized) {
				throw new AuthorizationDeniedException(
						"Only trainers assigned to the batch by admin can perform this action.");
			}

		}
		return true;
	}

	private List<MCQCreateDto> getMCQsAsJson(List<String> topics, Integer numberOfMCQs, String difficultyLevel, String userInstructionMessage) {
		// System message for behavior and JSON format rules
		SystemMessage formatMessage = new SystemMessage("""
				
				Important Rules:
				
				- You are an expert in creating multiple-choice questions (MCQs) for technical interviews.
				- Generate the MCQs strictly in JSON format as specified below.
				- Generate questions based on the provided topics and difficulty level.
				- Do not keep the correct option same (like A, B, C or D) for all the questions.
				- Do not include language tags in any code snippets. Just include the raw code.
				- Try to include new, unique questions that are not commonly found online.
				- Do not include any explanations, notes, or additional text outside the JSON structure.
				- Ensure the JSON is well-formed and valid.
				
				Each question must have:  

				- "question": The question text  

				- "options": A list of 4 options labeled A, B, C, D  

				- "answer": The correct option label (A, B, C, or D)  



				Return **ONLY** JSON in the following format:



				[

				  {

				    "question": "Question text here",

				    "options": {

				      "A": "Option A text",

				      "B": "Option B text",

				      "C": "Option C text",

				      "D": "Option D text"

				    },

				    "answer": "Correct option letter"

				  }

				]
				"""

		);
		
		LocalDateTime now = LocalDateTime.now(); // to make prompt dynamic

		// User message for topic and number of MCQs
		UserMessage topicMessage = new UserMessage("Topics: " + String.join(", ", topics));

		UserMessage numberMessage = new UserMessage("Number of MCQs: " + numberOfMCQs);

		UserMessage difficultyMessage = new UserMessage("Difficulty Level: " + difficultyLevel);
		
		UserMessage timeMessage = new UserMessage("Current Time: " + now.toString());
		
		UserMessage instructionMessage = new UserMessage(userInstructionMessage);
		
		// Call the chat model
		ChatResponse response = chatModel
				.call(new Prompt(List.of(formatMessage, topicMessage, timeMessage, instructionMessage, numberMessage, difficultyMessage),
						OpenAiChatOptions.builder().model(genAiModel).temperature(0.9).topP(Math.random()).build()));

		String rawJson = response.getResult().getOutput().getText();

		String cleaned = rawJson.replaceAll("```json", "").replaceAll("```", "").trim();
		
		List<MCQCreateDto> mcqDtos;
		
		try {
			mcqDtos = objectMapper.readValue(
			        cleaned,
			        new TypeReference<List<MCQCreateDto>>() {}
			    );
		}catch (Exception e) {
			e.printStackTrace();
			throw new MCQGenerationException("Failed to generate MCQs. Please try again.");
		}
		
		return mcqDtos;
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<MCQResponseDto>> getAllMCQsOfTestAdminAndTrainer(Long testId) {
		Test test = testRepository.findById(testId)
				.orElseThrow(() -> new ResourceNotFoundException("Test", "id", testId));
		
		
		accessCheckHelper(test.getBatch().getId());
		
		List<MCQ> mcqs = mcqRepository.findByTestId(testId);
		List<MCQResponseDto> responseDtos = mcqs.stream()
				.map(mcq -> modelMapper.map(mcq, MCQResponseDto.class))
				.toList();
		return new ApiResponse<>(true, "MCQs fetched successfully", responseDtos);
	}

}
