package com.codingsena.codingsena_backend.security;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import com.codingsena.codingsena_backend.entities.User;
import com.codingsena.codingsena_backend.repositories.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class SecurityConfiguration {
	
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	
	private CookieBearerTokenResolver cookieBearerTokenResolver;
	
	public SecurityConfiguration(CustomAccessDeniedHandler customAccessDeniedHandler,
			CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CookieBearerTokenResolver cookieBearerTokenResolver) {
		super();
		this.customAccessDeniedHandler = customAccessDeniedHandler;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
		this.cookieBearerTokenResolver = cookieBearerTokenResolver;
	}

	@Value("${jwt.keystore.location}")
	private Resource keystore;
	
	@Value("${jwt.keystore.password}")
	private String keystorePassword;
	
	@Value("${jwt.key.alias}")
	private String keyAlias;
	
	@Value("${jwt.key.password}")
	private String keyPassword;
	
	@Value("${frontend.base.url}")
	private String frontendBaseUrl;

	@Bean
	SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
		
		http.cors(cors -> {});
		
		http.authorizeHttpRequests((requests) -> 
			requests.requestMatchers("/api/batches/**").permitAll()
			        .requestMatchers("/api/auth/**").permitAll()
			        .requestMatchers("/api/users/register").permitAll()
			        .requestMatchers("/api/skills").permitAll()
			        .requestMatchers("/api/webhook/**").permitAll()
			        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
					.anyRequest().authenticated()
		);
		
		// Configuration for handling JWT specific exceptions
		http.exceptionHandling(ex ->
			ex.accessDeniedHandler(customAccessDeniedHandler) // for RDAC   
		);
		
		// Configuration to enable JWT based authentication
		http.oauth2ResourceServer(oauth2 -> oauth2
			    .bearerTokenResolver(cookieBearerTokenResolver) // <-- use cookie resolver
			    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
			    .authenticationEntryPoint(customAuthenticationEntryPoint)
			);

		
		// Configuration to make sure 'Spring Security' will not create or use http session
		http.sessionManagement(session -> 
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);
		
		// CSRF disabled for SPA + SameSite=Lax
		http.csrf(csrf -> csrf.disable());
		
		// Configuration to allow iframe only if request is from the same origin
		http.headers(header -> 
			header.frameOptions(frameOption -> frameOption.sameOrigin())
		);

		return http.build();
	}
	
	@Bean
	public KeyPair keyPair() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException   {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(keystore.getInputStream(), keystorePassword.toCharArray());
		Key key = ks.getKey(keyAlias, keyPassword.toCharArray());
		PublicKey publicKey = ks.getCertificate(keyAlias).getPublicKey();
		return new KeyPair(publicKey, (PrivateKey)key);
	}
	
	@Bean
	public RSAKey rsaRey(KeyPair keyPair) { // wraps key pair into JWK (JSON web key) format required by spring security
		return new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
				.privateKey(keyPair.getPrivate())
				.keyID(UUID.randomUUID().toString()).build();
	}
	
	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
		var jwkSet = new JWKSet(rsaKey);
		return (JWKSelector jwkSelector, SecurityContext context) -> jwkSelector.select(jwkSet);
	}
	
	@Bean
	public JwtDecoder jwtDecoder(RSAKey rsaKey, UserRepository userRepository) throws JOSEException {
	    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();

	    // Custom validator for tokenVersion
	    OAuth2TokenValidator<Jwt> tokenVersionValidator = jwt -> {
	        String email = jwt.getSubject();
	        Long tokenVersionFromJwt = jwt.getClaim("tokenVersion");

	        User user = userRepository.findByEmail(email).orElse(null);

	        if (user == null) {
	            return OAuth2TokenValidatorResult.failure(
	                new OAuth2Error("invalid_token", "User not found", null)
	            );
	        }

	        if (!user.getTokenVersion().equals(tokenVersionFromJwt)) {
	            return OAuth2TokenValidatorResult.failure(
	                new OAuth2Error("invalid_token", "Invalid token version", null)
	            );
	        }

	        return OAuth2TokenValidatorResult.success();
	    };

	    // Combine default validators with custom validator
	    jwtDecoder.setJwtValidator(
	        new DelegatingOAuth2TokenValidator<>(
	            JwtValidators.createDefault(), // default validations (expiry, signature, etc.)
	            tokenVersionValidator          // your custom version check
	        )
	    );

	    return jwtDecoder;
	}

	
	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
	    JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
	    authoritiesConverter.setAuthoritiesClaimName("scope");
	    authoritiesConverter.setAuthorityPrefix("");

	    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
	    jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
	    return jwtConverter;
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();

	    config.setAllowedOrigins(List.of(
	        frontendBaseUrl
	    ));

	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
	    config.setAllowedHeaders(List.of("*"));
	    config.setExposedHeaders(List.of("Authorization")); // Let the frontend read the Authorization header.
	    config.setAllowCredentials(true);
	    config.setMaxAge(3600L);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}

}
