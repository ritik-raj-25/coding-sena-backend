package com.codingsena.codingsena_backend.services.impls;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.AssignBatchRequest;
import com.codingsena.codingsena_backend.dtos.BatchResponseDto;
import com.codingsena.codingsena_backend.dtos.EnrollmentResponseDto;
import com.codingsena.codingsena_backend.entities.Batch;
import com.codingsena.codingsena_backend.entities.Enrollment;
import com.codingsena.codingsena_backend.entities.Payment;
import com.codingsena.codingsena_backend.entities.Role;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.repositories.BatchRepository;
import com.codingsena.codingsena_backend.repositories.EnrollmentRepository;
import com.codingsena.codingsena_backend.repositories.PaymentRepository;
import com.codingsena.codingsena_backend.repositories.RoleRepository;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.codingsena.codingsena_backend.services.EnrollmentService;
import com.codingsena.codingsena_backend.services.FileService;
import com.codingsena.codingsena_backend.utils.EnrollmentStatus;
import com.codingsena.codingsena_backend.utils.EnrollmentType;
import com.codingsena.codingsena_backend.utils.PaymentStatus;
import com.codingsena.codingsena_backend.utils.RoleType;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.Mode;

@Service
public class EnrollmentServiceImpl implements EnrollmentService{
	
	private EnrollmentRepository enrollmentRepository;
	private UserRepository userRepository;
	private BatchRepository batchRepository;
	private RoleRepository roleRepository;
	private PaymentRepository paymentRepository;
	private ModelMapper modelMapper;
	private FileService fileService;
	
	@Value("${stripe.secret.key}")
    private String stripeSecretKey;
	
	@Value("${base.url}")
	private String baseUrl;
	
	@Value("${frontend.base.url}")
	private String frontendBaseUrl;
	
	public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, UserRepository userRepository,
			BatchRepository batchRepository, RoleRepository roleRepository, PaymentRepository paymentRepository, ModelMapper modelMapper, FileService fileService) {
		super();
		this.enrollmentRepository = enrollmentRepository;
		this.userRepository = userRepository;
		this.batchRepository = batchRepository;
		this.roleRepository = roleRepository;
		this.paymentRepository = paymentRepository;
		this.modelMapper = modelMapper;
		this.fileService = fileService;
	}

	@Override
	@Transactional
	public ApiResponse<EnrollmentResponseDto> assignBatchToTrainer(AssignBatchRequest assignBatchRequest) {
		
		Batch batch = batchRepository.findById(assignBatchRequest.getBatchId())
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", assignBatchRequest.getBatchId()));
		
		User user = userRepository.findByEmail(assignBatchRequest.getUserEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", assignBatchRequest.getUserEmail()));
		
		Role role = roleRepository.findByRoleName(RoleType.ROLE_TRAINER)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleType.ROLE_TRAINER));
		
		if(!user.getRoles().contains(role)) {
			throw new ResourceNotFoundException("Trainer", "email", assignBatchRequest.getUserEmail());
		}
		
		String[] message = new String[1];
		Enrollment enrollment = createEnrollmentHelper(user, batch, message, EnrollmentStatus.ACTIVE);
		
		if(!enrollment.getIsTrainerEnrollmentByAdmin()) {
			enrollment.setIsTrainerEnrollmentByAdmin(true);
			message[0] = "Trainer assigned to the batch successfully.";
		}
		else {
			message[0] = assignBatchRequest.getUserEmail() + " is already a trainer for this batch.";
		}
		
		EnrollmentResponseDto enrollmentResponseDto = enrollmentResponseHelper(batch, user, enrollment, EnrollmentType.TRAINER);
		
		return new ApiResponse<>(true, message[0], enrollmentResponseDto);
	}

	private EnrollmentResponseDto enrollmentResponseHelper(Batch batch, User user, Enrollment enrollment, EnrollmentType enrollmentType) {
		EnrollmentResponseDto enrollmentResponseDto = new EnrollmentResponseDto();
		enrollmentResponseDto.setBatchId(batch.getId());
		enrollmentResponseDto.setBatchName(batch.getBatchName());
		enrollmentResponseDto.setCreatedAt(enrollment.getCreatedAt());
		enrollmentResponseDto.setStatus(enrollment.getStatus());
		enrollmentResponseDto.setEnrollmentType(enrollmentType);
		enrollmentResponseDto.setUserEmail(user.getEmail());
		enrollmentResponseDto.setUserId(user.getId());
		enrollmentResponseDto.setEnrollmentId(enrollment.getId());
		return enrollmentResponseDto;
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<BatchResponseDto>> getAllBatchOfUser() { // enrollment status = ACTIVE
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
		
		List<Enrollment> enrollments = enrollmentRepository.findByUserIdAndStatus(user.getId(), EnrollmentStatus.ACTIVE);
		List<BatchResponseDto> batchResponseDtos = new ArrayList<>();
		enrollments.stream().forEach(enrollment -> {
			Batch batch = enrollment.getBatch();
			BatchResponseDto batchResponseDto = batchResponseHelper(batch);
			batchResponseDtos.add(batchResponseDto);
		});
		return new ApiResponse<>(true, "Batches fetched successfully.", batchResponseDtos);
	}

	@Override
	@Transactional
	public ApiResponse<EnrollmentResponseDto> enrollLearner(Long batchId) throws StripeException {
		
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
		
		Batch batch = batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", batchId));
		
		String[] message = new String[1];
		Enrollment enrollment = createEnrollmentHelper(user, batch, message, EnrollmentStatus.PENDING_PAYMENT);
		
		String sessionUrl = null;
		
		if(enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
			message[0] = "You are already enrolled.";
		}
		else {
			// Create Stripe Checkout Session
			Stripe.apiKey = stripeSecretKey;
			Long amount = (batch.getPrice() - batch.getDiscount()) * 100; // paisa
			
			SessionCreateParams params = SessionCreateParams.builder()
					.setMode(Mode.PAYMENT)
					.setSuccessUrl(frontendBaseUrl + "/payment-success?session_id={CHECKOUT_SESSION_ID}")
					.setCancelUrl(frontendBaseUrl + "/payment-cancel")
					.addLineItem(
							SessionCreateParams.LineItem.builder()
								.setQuantity(1L)
								.setPriceData(
										SessionCreateParams.LineItem.PriceData.builder()
											.setCurrency("inr")
											.setUnitAmount(amount)
											.setProductData(
													SessionCreateParams.LineItem.PriceData.ProductData
														.builder()
														.setName(batch.getBatchName())
														.build()
													)
											.build()
										)
								.build()
							)
					.addExpand("payment_intent")
					.build();
			
			Session session = Session.create(params);
			
			Payment payment = new Payment();
			payment.setStatus(PaymentStatus.PENDING);
			payment.setAmount(amount);
			payment.setCurrency("inr");
			payment.setSessionId(session.getId());
			payment.setPaymentIntentId(session.getPaymentIntent());
			
			payment.addPayment(enrollment);
			
			paymentRepository.save(payment);
			
			sessionUrl = session.getUrl();
			message[0] += ", proceed to payment.";
		}
		
		EnrollmentResponseDto enrollmentResponseDto = enrollmentResponseHelper(batch, user, enrollment, EnrollmentType.LEARNER);
		enrollmentResponseDto.setCheckoutUrl(sessionUrl);
		
		return new ApiResponse<>(true, message[0], enrollmentResponseDto);
	}

	private Enrollment createEnrollmentHelper(User user, Batch batch, String[] message, EnrollmentStatus enrollmentStatus) {
		Enrollment enrollment = null;
		if(enrollmentRepository.existsByUserAndBatch(user, batch)) {
			enrollment = enrollmentRepository.findByUserAndBatch(user, batch)
					.orElseThrow(() -> new ResourceNotFoundException("Enrollment", "userEmail and batchId", user.getEmail() + " " + batch.getId()));
			message[0] = user.getEmail() + " is already enrolled in the batch.";
		}
		else {
			enrollment = new Enrollment();
			enrollment.addBatch(batch);
			enrollment.addUser(user);
			enrollment.setStatus(enrollmentStatus);
			
			enrollment = enrollmentRepository.save(enrollment);
			message[0] = "Enrollment created successfully.";
		}
		return enrollment;
	}
	
	private BatchResponseDto batchResponseHelper(Batch batch) {
		BatchResponseDto batchResponseDto = modelMapper.map(batch, BatchResponseDto.class);
		String coverPicUrl = batch.getCoverPicName() != null ? fileService.getFileUrl(batch.getCoverPicName()) : null;
		String curriculumUrl = batch.getCurriculum() != null ? fileService.getFileUrl(batch.getCurriculum()) : null;
		batchResponseDto.setCoverPicUrl(coverPicUrl);
		batchResponseDto.setCurriculumUrl(curriculumUrl);
		batchResponseDto.setNoOfStudentsEnrolled(enrollmentRepository.countByBatchIdAndStatus(batch.getId(), EnrollmentStatus.ACTIVE));
		return batchResponseDto;
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<List<BatchResponseDto>> getAllBatchesOfTrainer() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
		List<Enrollment> enrollments = enrollmentRepository.findByUserIdAndStatusAndIsTrainerEnrollmentByAdmin(user.getId(), EnrollmentStatus.ACTIVE, true);
		List<BatchResponseDto> batchResponseDtos = new ArrayList<>();
		enrollments.stream().forEach(enrollment -> {
			Batch batch = enrollment.getBatch();
			BatchResponseDto batchResponseDto = batchResponseHelper(batch);
			batchResponseDtos.add(batchResponseDto);
		});
		return new ApiResponse<>(true, "Batches fetched successfully.", batchResponseDtos);
	}

	@Override
	@Transactional
	public ApiResponse<EnrollmentResponseDto> revokeBatchFromTrainer(AssignBatchRequest assignBatchRequest) {
		User user = userRepository.findByEmail(assignBatchRequest.getUserEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", assignBatchRequest.getUserEmail()));
		Batch batch = batchRepository.findById(assignBatchRequest.getBatchId())
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", assignBatchRequest.getBatchId()));
		Enrollment enrollment = enrollmentRepository.findByUserAndBatch(user, batch)
				.orElseThrow(() -> new ResourceNotFoundException("Enrollment", "userEmail and batchId", assignBatchRequest.getUserEmail() + " " + assignBatchRequest.getBatchId()));
		
		String message = "";
		if(enrollment.getIsTrainerEnrollmentByAdmin()) {
			enrollment.setIsTrainerEnrollmentByAdmin(false);
			enrollment.setStatus(EnrollmentStatus.CANCELLED);
			message = "Trainer removed from the batch successfully.";
		}
		else {
			message = user.getEmail() + " is not a trainer for this batch.";
		}
		
		EnrollmentResponseDto enrollmentResponseDto = enrollmentResponseHelper(batch, user, enrollment, EnrollmentType.TRAINER);
		return new ApiResponse<>(true, message, enrollmentResponseDto);
	}

	@Override
	public ApiResponse<Boolean> isTrainerOfBatch(Long batchId) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
		Batch batch = batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "id", batchId));
		Enrollment enrollment = enrollmentRepository.findByUserAndBatch(user, batch)
				.orElseThrow(() -> new ResourceNotFoundException("Enrollment", "user email and batch id", email + " and " + batchId));
		return new ApiResponse<>(true, "Trainer status fetched successfully.", enrollment.getIsTrainerEnrollmentByAdmin());
	}

}
