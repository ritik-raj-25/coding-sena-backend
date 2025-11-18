package com.codingsena.codingsena_backend.security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.dtos.ApiResponse;
import com.codingsena.codingsena_backend.dtos.UserResponseDto;
import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.exceptions.ResourceNotFoundException;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.codingsena.codingsena_backend.services.FileService;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtService {
	
	private JwtEncoder jwtEncoder;
	private UserRepository userRepository;
	private ModelMapper modelMapper;
	private FileService fileService;

	public JwtService(JwtEncoder jwtEncoder, UserRepository userRepository, ModelMapper modelMapper, FileService fileService) {
		super();
		this.jwtEncoder = jwtEncoder;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.fileService = fileService;
	}
	
	@Transactional(readOnly = true)
	public ApiResponse<UserResponseDto> createLoginResponse(HttpServletResponse response, Authentication authentication) {
		User existingUser = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
		
		var claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(24 * 60 * 60L))
				.subject(authentication.getName())
				.claim("scope", createScope(authentication))
				.claim("tokenVersion", existingUser.getTokenVersion()) 
				.build();
		
		String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		
		// Set HTTP-only cookie
		ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.path("/")
				.maxAge(24 * 60 * 60)
				.build();
		
		response.addHeader("Set-Cookie", cookie.toString());
		
		UserResponseDto userResponseDto = modelMapper.map(existingUser, UserResponseDto.class);
		if(existingUser.getProfilePicName() != null) {
			userResponseDto.setProfilePicUrl(fileService.getFileUrl(existingUser.getProfilePicName()));
		}
		
		ApiResponse<UserResponseDto> apiResponse = new ApiResponse<>(true, "Login successfull.", userResponseDto);
		
		return apiResponse;
	}
	
	private String createScope(Authentication authentication) {
		return authentication.getAuthorities()
				.stream() //returns a stream of GrantedAuthority objects
				.map(a -> a.getAuthority())
				.collect(Collectors.joining(" "));
	}
	
	@Transactional
	public ApiResponse<Void> logout(HttpServletResponse response) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        String email = auth.getName();
	        User existingUser = userRepository.findByEmail(email)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
	        existingUser.setTokenVersion(existingUser.getTokenVersion() + 1);
	    }

	    ResponseCookie cookie = ResponseCookie.from("jwt", "")
	            .httpOnly(true)
	            .secure(true)
	            .sameSite("None")
	            .path("/")
	            .maxAge(0)
	            .build();

	    response.addHeader("Set-Cookie", cookie.toString());

	    return new ApiResponse<>(true, "Logout successful.");
	}
}
