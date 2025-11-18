package com.codingsena.codingsena_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import io.github.cdimascio.dotenv.Dotenv;

@EnableJpaAuditing
@SpringBootApplication
@EnableAsync
@EnableMethodSecurity
@EnableScheduling
public class CodingsenaBackendApplication {

	public static void main(String[] args) {
		// Load .env file
        Dotenv dotenv = Dotenv.configure()
        		.directory(".")
                .ignoreIfMissing()
                .load();

        // Inject all .env values into System properties
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
        
		SpringApplication.run(CodingsenaBackendApplication.class, args);
	}

}
