package com.codingsena.codingsena_backend.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.EmailRequest;
import com.codingsena.codingsena_backend.dtos.PagedResponse;
import com.codingsena.codingsena_backend.dtos.UserLoginDto;
import com.codingsena.codingsena_backend.dtos.UserRegisterDto;
import com.codingsena.codingsena_backend.dtos.UserResponseDto;
import com.codingsena.codingsena_backend.dtos.UserUpdateDto;

import jakarta.mail.MessagingException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

public interface UserService {
	ApiResponse<Void> registerUser(UserRegisterDto userDto, MultipartFile profilePic) throws MessagingException, IOException;
	ApiResponse<Void> reSendVerificationToken(EmailRequest email) throws MessagingException, IOException;
	ApiResponse<Void> verifyUserEmail(String token);
	ApiResponse<UserResponseDto> getLoggedInUser();
	ApiResponse<Void> deleteLoggedInUser();
	ApiResponse<UserResponseDto> updateUser(UserUpdateDto userDto, MultipartFile profilePic) throws S3Exception, AwsServiceException, SdkClientException, IOException, MessagingException;
	ApiResponse<Void> activateDeletedUser(UserLoginDto loginDto);
	
	// Trainer and Admin only
	ApiResponse<UserResponseDto> getUserById(Long id);
	
	// Admin Specific methods
	ApiResponse<Void> promoteUserToTrainer(EmailRequest email);
	ApiResponse<Void> demoteUserToOnlyLearner(EmailRequest email);
	ApiResponse<Void> blockUser(EmailRequest email);
	ApiResponse<Void> unblockUser(EmailRequest email);
	ApiResponse<PagedResponse<UserResponseDto>> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	
	//admin setup
	void createAdminIfNotExists();
	
}
