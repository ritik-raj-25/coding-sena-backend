package com.codingsena.codingsena_backend.services.impls;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.EmailRequest;
import com.codingsena.codingsena_backend.dtos.PagedResponse;
import com.codingsena.codingsena_backend.dtos.UserLoginDto;
import com.codingsena.codingsena_backend.dtos.UserRegisterDto;
import com.codingsena.codingsena_backend.dtos.UserResponseDto;
import com.codingsena.codingsena_backend.dtos.UserUpdateDto;
import com.codingsena.codingsena_backend.entities.Role;
import com.codingsena.codingsena_backend.entities.Skill;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.entities.VerificationToken;
import com.codingsena.codingsena_backend.exceptions.InvalidFileTypeException;
import com.codingsena.codingsena_backend.exceptions.ResourceAlreadyExistException;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.exceptions.TokenExpiredException;
import com.codingsena.codingsena_backend.exceptions.UserAlreadyVerifiedException;
import com.codingsena.codingsena_backend.repositories.RoleRepository;
import com.codingsena.codingsena_backend.repositories.SkillRepository;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.codingsena.codingsena_backend.repositories.VerificationTokenRepository;
import com.codingsena.codingsena_backend.services.EmailService;
import com.codingsena.codingsena_backend.services.FileService;
import com.codingsena.codingsena_backend.services.UserService;
import com.codingsena.codingsena_backend.utils.RoleType;
import com.codingsena.codingsena_backend.utils.StatusType;

import jakarta.mail.MessagingException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;
	private ModelMapper modelMapper;
	private FileService fileService;
	private PasswordEncoder passwordEncoder;
	private RoleRepository roleRepository;
	private EmailService emailService;
	private SkillRepository skillRepository;
	private VerificationTokenRepository verificationTokenRepository;

	@Value("${base.url}")
	private String baseUrl;
	
	@Value("${frontend.base.url}")
	private String frontendBaseUrl;
	
	
	@Value("${aws.s3.profilePic.subBucketName}")
	private String profilePicSubBucketName;

	public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, FileService fileService,
			PasswordEncoder passwordEncoder, RoleRepository roleRepository, EmailService emailService,
			VerificationTokenRepository verificationTokenRepository, SkillRepository skillRepository) {
		super();
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.fileService = fileService;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.emailService = emailService;
		this.skillRepository = skillRepository;
		this.verificationTokenRepository = verificationTokenRepository;
	}

	@Transactional
	@Override
	public ApiResponse<Void> registerUser(UserRegisterDto userDto, MultipartFile profilePic)
			throws MessagingException, IOException {

		Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());

		if (!existingUser.isPresent()) { // new (fresh) registration
			User user = modelMapper.map(userDto, User.class);
			user.setIsVerified(false); // by default user is 'not verified'
			user.setStatus(StatusType.ACTIVE); // by default user is active

			// Saving the profile pic
			String profilePicName = null;
			if (profilePic != null) {
				String contentType = profilePic.getContentType();
				if (contentType.equals("image/png") || contentType.equals("image/jpg")
						|| contentType.equals("image/jpeg")) {
					profilePicName = fileService.saveFile(profilePicSubBucketName, profilePic);
				} else {
					throw new InvalidFileTypeException(
							"Invalid file. Only image files(.png, .jpg, and .jpeg) are allowed.");
				}
			}
			user.setProfilePicName(profilePicName);

			// Hashing password before saving to db
			String hashedPassword = passwordEncoder.encode(userDto.getPassword());
			user.setPassword(hashedPassword);

			// Assigning role to user
			Role role = roleRepository.findByRoleName(RoleType.ROLE_LEARNER)
					.orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleType.ROLE_LEARNER.name()));
			user.addRole(role);
			
			// mark by default user is active
			user.setIsDeleted(false);
			
			// Saving skills to user
			if (userDto.getSkills() != null) {
				user.getSkills().clear();
				userDto.getSkills().forEach(skillDto -> {
					Skill skill = skillRepository.findById(skillDto.getId())
							.orElseThrow(() -> new ResourceNotFoundException("Skill", "id", skillDto.getId()));
					user.addSkill(skill); // add the existing skill
				});
			}
			userRepository.save(user); // Saving user to db
			// genetate token and send verification email
			generateTokenAndSendVerificationEmail(user);
		} else { // User already exist
			User user = existingUser.get();
			if (user.getIsVerified()) {
				throw new ResourceAlreadyExistException("User", "email", userDto.getEmail());
			} else {
				// genetate token and send verification email
				user.removeVerificationToken(); // delete old token
				generateTokenAndSendVerificationEmail(user);
			}
		}


		return new ApiResponse<>(true, "Registration successful. Please check your email for verification.");
	}
	
	@Override
	@Transactional
	public ApiResponse<Void> activateDeletedUser(UserLoginDto loginDto) {
		User user = userRepository.findByEmail(loginDto.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", loginDto.getEmail()));
		String message = "";
		if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
			message = "Wrong password.";
			throw new BadCredentialsException(message);
		}
		else if(user.getIsDeleted()) {
			user.setIsDeleted(false); // account activated
			emailService.sendAccountActivationEmail(user.getEmail(), user.getName());
			message = "Your account has been activated successfully, please login.";
		}
		else {
			message = "You are already active, please login.";
		}
		return new ApiResponse<>(true, message);
	}

	@Override
	@Transactional
	public ApiResponse<UserResponseDto> updateUser(UserUpdateDto userDto, MultipartFile profilePic)
			throws S3Exception, AwsServiceException, SdkClientException, IOException, MessagingException {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
		String message = "Requested field(s) updated successfully.";
		if (userDto != null) {
			if (userDto.getName() != null) {
				user.setName(userDto.getName());
			}
			if (userDto.getLocation() != null) {
				user.setLocation(userDto.getLocation());
			}
			if (userDto.getCollege() != null) {
				user.setCollege(userDto.getCollege());
			}
			if (userDto.getDob() != null) {
				user.setDob(userDto.getDob());
			}
			if (userDto.getPassword() != null) {
				String hashedPassword = passwordEncoder.encode(userDto.getPassword());
				user.setPassword(hashedPassword);
				user.setTokenVersion(user.getTokenVersion()+1);
				message = "Password and other requested field(s) (if any) updated successfully. Please log in again.";
			}
			if (userDto.getNickName() != null) {
				user.setNickName(userDto.getNickName());
			}
			if (userDto.getSkills() != null) {
				user.getSkills().clear();
				userDto.getSkills().forEach(skillDto -> {
					Skill skill = skillRepository.findById(skillDto.getId())
							.orElseThrow(() -> new ResourceNotFoundException("Skill", "id", skillDto.getId()));
					user.addSkill(skill); // add the existing skill
				});
			}
		}
		if (profilePic != null) {
			String contentType = profilePic.getContentType();
			if (contentType.equals("image/png") || contentType.equals("image/jpg")
					|| contentType.equals("image/jpeg")) {
				if(user.getProfilePicName() != null) {
					fileService.deleteFile(user.getProfilePicName());
				}
				String profilePicName = fileService.saveFile(profilePicSubBucketName, profilePic);
				user.setProfilePicName(profilePicName);
			} else {
				throw new InvalidFileTypeException(
						"Invalid file. Only image files(.png, .jpg, and .jpeg) are allowed.");
			}
		}

		UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
		
		if (user.getProfilePicName() != null) {
			String profilePicUrl = fileService.getFileUrl(user.getProfilePicName());
			userResponseDto.setProfilePicUrl(profilePicUrl);
		}

		return new ApiResponse<UserResponseDto>(true, message, userResponseDto);
	}

	@Transactional
	@Override
	public ApiResponse<Void> reSendVerificationToken(EmailRequest email) throws MessagingException, IOException {
		User existingUser = userRepository.findByEmail(email.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email.getEmail()));

		if (existingUser.getIsVerified()) {
			throw new UserAlreadyVerifiedException("User already verified");
		}

		generateTokenAndSendVerificationEmail(existingUser);

		return new ApiResponse<>(true, "Verification email sent.");
	}

	@Transactional
	@Override
	public ApiResponse<Void> verifyUserEmail(String token) {
		VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
				.orElseThrow(() -> new ResourceNotFoundException("VerificationToken", "token", token));

		if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new TokenExpiredException(
					"Verification link expired. Please re-register or re-request verification link");
		}

		User user = verificationToken.getUser();

		if (user.getIsVerified()) {
			throw new UserAlreadyVerifiedException("User already verified");
		}

		verificationToken.getUser().setIsVerified(true);

		emailService.sendConfirmEmailVerificationEmail(verificationToken.getUser().getEmail(),
				verificationToken.getUser().getName());

		return new ApiResponse<>(true, "Email verified successfully! Redirecting to login...");
	}

	private void generateTokenAndSendVerificationEmail(User user) throws MessagingException, IOException {
		// Generate Token
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24L));
		user.addVerificationToken(verificationToken);
		verificationTokenRepository.save(verificationToken);

		// send verification email
		String link = frontendBaseUrl + "/email-verification?token=" + token;
		emailService.sendEmailVerificationEmail(user.getEmail(), user.getName(), link);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<UserResponseDto> getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
		UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);

		if (user.getProfilePicName() != null) {
			String profilePicUrl = fileService.getFileUrl(user.getProfilePicName());
			userResponseDto.setProfilePicUrl(profilePicUrl);
		}

		return new ApiResponse<>(true, "User fetched successfully.", userResponseDto);
	}

	@Override
	@Transactional
	public ApiResponse<Void> deleteLoggedInUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
		user.setIsDeleted(true);
		user.setTokenVersion(user.getTokenVersion() + 1);
		emailService.sendAccountDeactivationEmail(email, user.getName());
		return new ApiResponse<>(true, "User de-activated successfully.");
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<UserResponseDto> getLoggedInUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
		UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);

		if (user.getProfilePicName() != null) {
			String profilePicUrl = fileService.getFileUrl(user.getProfilePicName());
			userResponseDto.setProfilePicUrl(profilePicUrl);
		}

		return new ApiResponse<>(true, "User fetched successfully.", userResponseDto);
	}

	// Admin only (admin specific) service methods

	@Override
	@Transactional(readOnly = true)
	public ApiResponse<PagedResponse<UserResponseDto>> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy,
			String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<User> userPage = userRepository.findAllUser(pageable);
		List<User> users = userPage.getContent();
		List<UserResponseDto> userResponseDtos = new ArrayList<>();
		users.forEach(user -> {
			UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);

			if (user.getProfilePicName() != null) {
				String profilePicUrl = fileService.getFileUrl(user.getProfilePicName());
				userResponseDto.setProfilePicUrl(profilePicUrl);
			}

			userResponseDtos.add(userResponseDto);
		});

		PagedResponse<UserResponseDto> pagedResponse = new PagedResponse<>();
		pagedResponse.setContent(userResponseDtos);
		pagedResponse.setPageSize(userPage.getSize());
		pagedResponse.setIsLastPage(userPage.isLast());
		pagedResponse.setTotalElements(userPage.getTotalElements());
		pagedResponse.setTotalPages(userPage.getTotalPages());
		pagedResponse.setPageNumber(userPage.getNumber());

		return new ApiResponse<>(true, "Users fetched successfully.", pagedResponse);
	}

	@Transactional
	public ApiResponse<Void> promoteUserToTrainer(EmailRequest email) {
		User existingUser = userRepository.findByEmail(email.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email.getEmail()));
		
		Role roleTrainer = roleRepository.findByRoleName(RoleType.ROLE_TRAINER)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleType.ROLE_TRAINER.name()));

		if (!existingUser.getRoles().contains(roleTrainer)) {
			existingUser.addRole(roleTrainer);
			emailService.sendPromoteUserEmail(email.getEmail(), existingUser.getName());
			return new ApiResponse<>(true, existingUser.getName() + " successfully promoted to trainer.");
		} else {
			return new ApiResponse<>(true, existingUser.getName() + " is already a trainer.");
		}
	}

	@Transactional
	public ApiResponse<Void> demoteUserToOnlyLearner(EmailRequest email) {
		User existingUser = userRepository.findByEmail(email.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email.getEmail()));
		
		Role roleTrainer = roleRepository.findByRoleName(RoleType.ROLE_TRAINER)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleType.ROLE_TRAINER.name()));

		if (existingUser.getRoles().contains(roleTrainer)) {
			existingUser.removeRole(roleTrainer);
			emailService.sendDemoteUserEmail(email.getEmail(), existingUser.getName());
			return new ApiResponse<>(true, existingUser.getName() + " has been successfully demoted");
		} else {
			return new ApiResponse<>(true, existingUser.getName() + " didn’t have the trainer role.");
		}
	}

	@Transactional
	@Override
	public ApiResponse<Void> blockUser(EmailRequest email) {
		User existingUser = userRepository.findByEmail(email.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email.getEmail()));

		if (existingUser.getStatus() != StatusType.BLOCKED) {
			existingUser.setStatus(StatusType.BLOCKED);
			emailService.sendBlockUserEmail(email.getEmail(), existingUser.getName());
			return new ApiResponse<>(true, existingUser.getName() + " has been successfully blocked.");
		} else {
			return new ApiResponse<>(true, existingUser.getName() + " is already blocked.");
		}

	}

	@Transactional
	@Override
	public ApiResponse<Void> unblockUser(EmailRequest email) {
		User existingUser = userRepository.findByEmail(email.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email.getEmail()));
		if (existingUser.getStatus() == StatusType.BLOCKED) {
			existingUser.setStatus(StatusType.ACTIVE);
			emailService.sendUnblockUserEmail(email.getEmail(), existingUser.getName());
			return new ApiResponse<>(true, existingUser.getName() + " has been successfully unblocked.");
		} else {
			return new ApiResponse<>(true, existingUser.getName() + " is already active.");
		}
	}

	// Admin Setup
	@Override
	@Transactional
	public void createAdminIfNotExists() {
		if (!userRepository.existsByEmail("rajritik2511@gmail.com")) {
			User admin = new User();
			admin.setName("Ritik Raj");
			admin.setEmail("rajritik2511@gmail.com");
			admin.setDob(LocalDate.of(2003, 11, 25));
			admin.setLocation("Samastipur, Bihar, India");
			admin.setCollege("Vellore Institute of Technology, Vellore");

			String hashedPassword = passwordEncoder.encode("Admin@123");
			admin.setPassword(hashedPassword);

			admin.setNickName("Hijack");

			admin.setIsVerified(true);
			admin.setStatus(StatusType.ACTIVE);

			Role roleAdmin = roleRepository.findByRoleName(RoleType.ROLE_ADMIN)
					.orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleType.ROLE_ADMIN.name()));
			Role roleTrainer = roleRepository.findByRoleName(RoleType.ROLE_TRAINER)
					.orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleType.ROLE_TRAINER.name()));
			Role roleLearner = roleRepository.findByRoleName(RoleType.ROLE_LEARNER)
					.orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleType.ROLE_LEARNER.name()));
			admin.addRole(roleAdmin);
			admin.addRole(roleTrainer);
			admin.addRole(roleLearner);
			admin.setIsDeleted(false);
			userRepository.save(admin);
		}
	}
}
