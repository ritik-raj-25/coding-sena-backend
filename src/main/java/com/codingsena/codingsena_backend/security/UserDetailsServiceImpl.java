package com.codingsena.codingsena_backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingsena.codingsena_backend.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private UserRepository userRepository;
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
	
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username).orElseThrow(
					() -> new UsernameNotFoundException("User with given email doesn't exist.")
				);
	}
}
