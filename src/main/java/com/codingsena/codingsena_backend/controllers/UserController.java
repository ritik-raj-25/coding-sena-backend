package com.codingsena.codingsena_backend.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.EmailRequest;
import com.codingsena.codingsena_backend.dtos.PagedResponse;
import com.codingsena.codingsena_backend.dtos.UserLoginDto;
import com.codingsena.codingsena_backend.dtos.UserRegisterDto;
import com.codingsena.codingsena_backend.dtos.UserResponseDto;
import com.codingsena.codingsena_backend.dtos.UserUpdateDto;
import com.codingsena.codingsena_backend.services.UserService;
import com.codingsena.codingsena_backend.utils.AppConstant;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@Validated
@RequestMapping("/api")
public class UserController {
	
	private UserService userService;
	
	@Value("${base.url}")
	private String baseUrl;

	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	// User Registration and Verification APIs
	@PostMapping("users/register")
	public ResponseEntity<ApiResponse<Void>> registerUser(
				@Valid @RequestPart("userDetails") UserRegisterDto userDto,
				@RequestPart(name = "profilePic", required = false) MultipartFile profilePic
			) throws MessagingException, IOException {
		ApiResponse<Void> response = userService.registerUser(userDto, profilePic);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PatchMapping("/users")
	public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
				@Valid @RequestPart(name = "userDetails", required = false) UserUpdateDto userDto,
				@RequestPart(name = "profilePic", required = false) MultipartFile profilePic
			) throws S3Exception, AwsServiceException, SdkClientException, IOException, MessagingException{
		ApiResponse<UserResponseDto> response = userService.updateUser(userDto, profilePic);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/auth/verify")
	public ResponseEntity<ApiResponse<Void>> verifyUser(@RequestParam @NotBlank(message = "Token Missing. Please re-register or re-request verification link.") String token) {
		ApiResponse<Void> response = userService.verifyUserEmail(token);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/auth/token")
	public ResponseEntity<ApiResponse<Void>> reSendEmailVerificationToken(@RequestBody @Valid EmailRequest email) throws MessagingException, IOException {
		ApiResponse<Void> response = userService.reSendVerificationToken(email);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/auth/users/activate")
	public ResponseEntity<ApiResponse<Void>> activateDeletedUser(@Valid @RequestBody UserLoginDto loginDto) {
		ApiResponse<Void> response = userService.activateDeletedUser(loginDto);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/users/me")
	public ResponseEntity<ApiResponse<UserResponseDto>> getUser() {
		ApiResponse<UserResponseDto> response = userService.getLoggedInUser();
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/users/de-activate") // for soft delete
	public ResponseEntity<ApiResponse<Void>> deleteUser() {
		ApiResponse<Void> response = userService.deleteLoggedInUser();
		return ResponseEntity.ok(response);
	}
	
	//Admin and Trainer Only APIs
	
	@GetMapping("/users/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRAINER')")
	public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@Min(value = 1, message = "User id can't be 0 or negative.") @PathVariable Long id) {
		ApiResponse<UserResponseDto> response = userService.getUserById(id);
		return ResponseEntity.ok(response);
	}
	
	// Admin Only APIs
	
	@PutMapping("/admin/promote-user")
	public ResponseEntity<ApiResponse<Void>> promoteUserToTrainer(@RequestBody @Valid EmailRequest email) {
		ApiResponse<Void> response = userService.promoteUserToTrainer(email);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/admin/demote-user")
	public ResponseEntity<ApiResponse<Void>> demoteUserToOnlyLearner(@RequestBody @Valid EmailRequest email) {
		ApiResponse<Void> response = userService.demoteUserToOnlyLearner(email);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/admin/block-user")
	public ResponseEntity<ApiResponse<Void>> blockUser(@RequestBody @Valid EmailRequest email) {
		ApiResponse<Void> response = userService.blockUser(email);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/admin/unblock-user")
	public ResponseEntity<ApiResponse<Void>> unblockUser(@RequestBody @Valid EmailRequest email) {
		ApiResponse<Void> response = userService.unblockUser(email);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/admin/users")
	public ResponseEntity<ApiResponse<PagedResponse<UserResponseDto>>> getAllUsers(
				@RequestParam(name="pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) @Min(value = 0, message = "Page number can't be negative.") Integer pageNumber,
				@RequestParam(name="pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) @Min(value = 1, message = "Page size should at least be 1.") Integer pageSize,
				@RequestParam(name="sortBy", defaultValue = AppConstant.SORT_BY, required = false) @NotBlank(message = "Sort by can't be blank.") String sortBy,
				@RequestParam(name="sortDir", defaultValue = AppConstant.SORT_DIR, required = false) @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Sort direction must be 'asc' or 'desc'") String sortDir
			) {
		ApiResponse<PagedResponse<UserResponseDto>> userResponseDtosPage = userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir);
		return ResponseEntity.ok(userResponseDtosPage);
	}
}
