package com.codingsena.codingsena_backend.services.impls;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.codingsena.codingsena_backend.services.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{
	
	@Value("${spring.mail.username}")
	private String fromEmail;
	
	private JavaMailSender mailSender;
	
	public EmailServiceImpl(JavaMailSender mailSender) {
		super();
		this.mailSender = mailSender;
	}

	@Override
	@Async
	public void sendEmailVerificationEmail(String to, String name, String link) throws MessagingException, IOException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setTo(to);
		helper.setFrom(fromEmail);
		helper.setSubject("Verify Your Email - Coding Sena");
		
		String template = new String(Files.readAllBytes(Paths.get("src/main/resources/verification-email.html")));
		template = template.replace("{{name}}", name);
		template = template.replace("{{link}}", link);
		
		helper.setText(template, true);
		mailSender.send(message);
	}

	@Override
	@Async
	public void sendConfirmEmailVerificationEmail(String to, String name) {
		var message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Welcome to Coding Sena!");
		message.setText("Hello " + name + ",\n\nWelcome to Coding Sena! Your account has been successfully verified.\n\nEnjoy the experience!\n\n- Team Coding Sena");
		mailSender.send(message);
	}

	@Override
	@Async
	public void sendPromoteUserEmail(String to, String name) {
	    var message = new SimpleMailMessage();
	    message.setTo(to);
	    message.setSubject("Promoted to Trainer!");
	    message.setText("Hello " + name + ",\n\nWelcome to Coding Sena! Awesome news — you’ve just been promoted to Trainer! We’re thrilled to have you on board in this new role. Let’s make it count!\n\nEnjoy the experience!\n\n- Team Coding Sena");
	    mailSender.send(message);
	}

	@Override
	@Async
	public void sendDemoteUserEmail(String to, String name) {
	    var message = new SimpleMailMessage();
	    message.setTo(to);
	    message.setSubject("Demoted to Learner!");
	    message.setText("Hello " + name + ",\n\nThis is to inform you that your account has been demoted to Student.\n\nIf you have any questions, feel free to reach out.\n\n- Team Coding Sena");
	    mailSender.send(message);
	}

	@Override
	@Async
	public void sendBlockUserEmail(String to, String name) {
	    var message = new SimpleMailMessage();
	    message.setTo(to);
	    message.setSubject("Account Blocked");
	    message.setText("Hello " + name + ",\n\nWe regret to inform you that your account has been blocked due to policy violations or suspicious activity.\n\nIf you believe this is a mistake, please contact support.\n\n- Team Coding Sena");
	    mailSender.send(message);
	}

	@Override
	@Async
	public void sendUnblockUserEmail(String to, String name) {
	    var message = new SimpleMailMessage();
	    message.setTo(to);
	    message.setSubject("Account Unblocked");
	    message.setText("Hello " + name + ",\n\nYour account has been de-activated successfully.\n\nWe hope to see you again soon!\n\n- Team Coding Sena");
	    mailSender.send(message);
	}

	@Override
	@Async
	public void sendAccountDeactivationEmail(String to, String name) {
		var message = new SimpleMailMessage();
	    message.setTo(to);
	    message.setSubject("Account Unblocked");
	    message.setText("Hello " + name + ",\n\nGood news! Your account has been unblocked and you can now access all features again.\n\nWelcome back!\n\n- Team Coding Sena");
	    mailSender.send(message);
	}

	@Override
	@Async
	public void sendAccountActivationEmail(String to, String name) {
		var message = new SimpleMailMessage();
	    message.setTo(to);
	    message.setSubject("Account Unblocked");
	    message.setText("Hello " + name + ",\n\nGood news! Your account has been activated successfully and you can now access all features again.\n\nWelcome back!\n\n- Team Coding Sena");
	    mailSender.send(message);
	}


}
