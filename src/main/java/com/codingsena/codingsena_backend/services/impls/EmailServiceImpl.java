package com.codingsena.codingsena_backend.services.impls;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.codingsena.codingsena_backend.services.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import jakarta.mail.MessagingException;

@Service
public class EmailServiceImpl implements EmailService{
	
	@Value("${spring.mail.from}")
	private String fromEmail;
	
	@Value("${resend.api.key}")
	private String resendApiKey;
	
	@Override
	public void sendEmailVerificationEmail(String to, String name, String link) throws MessagingException, IOException {
		Resend resend = new Resend(resendApiKey);
		
		ClassPathResource resource = new ClassPathResource("verification-email.html");
        String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		template = template.replace("{{name}}", name);
		template = template.replace("{{link}}", link);
		
		CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(to)
                .subject("Verify Your Email - Coding Sena")
                .html(template)
                .build();
		
		try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void sendConfirmEmailVerificationEmail(String to, String name) {
		Resend resend = new Resend(resendApiKey);
		
		CreateEmailOptions params = CreateEmailOptions.builder()
				.from(fromEmail)
				.to(to)
				.subject("Welcome to Coding Sena!")
				.text("Hello " + name + ",\n\nWelcome to Coding Sena! Your account has been successfully verified.\n\nEnjoy the experience!\n\n- Team Coding Sena")
				.build();
		
		try {
			CreateEmailResponse data = resend.emails().send(params);
			System.out.println(data.getId());
		} catch (ResendException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendPromoteUserEmail(String to, String name) {
	    Resend resend = new Resend(resendApiKey);
	    
	    CreateEmailOptions params = CreateEmailOptions.builder()
	    		.from(fromEmail)
	    		.to(to)
	    		.subject("Promoted to Trainer!")
	    		.text("Hello " + name + ",\n\nWelcome to Coding Sena! Awesome news — you’ve just been promoted to Trainer! We’re thrilled to have you on board in this new role. Let’s make it count!\n\nEnjoy the experience!\n\n- Team Coding Sena")
	    		.build();
	    
	    try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void sendDemoteUserEmail(String to, String name) {
	    Resend resend = new Resend(resendApiKey);
	    
	    CreateEmailOptions params = CreateEmailOptions.builder()
	    		.from(fromEmail)
	    		.to(to)
	    		.subject("Demoted to Learner!")
	    		.text("Hello " + name + ",\n\nThis is to inform you that your account has been demoted to Student.\n\nIf you have any questions, feel free to reach out.\n\n- Team Coding Sena")
	    		.build();
	    
	    try {
			CreateEmailResponse data = resend.emails().send(params);
			System.out.println(data.getId());
		} catch (ResendException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendBlockUserEmail(String to, String name) {
	    Resend resend = new Resend(resendApiKey);
	    
	    CreateEmailOptions params = CreateEmailOptions.builder()
	    		.from(fromEmail)
	    		.to(to)
	    		.subject("Account Blocked")
	    		.text("Hello " + name + ",\n\nWe regret to inform you that your account has been blocked due to policy violations or suspicious activity.\n\nIf you believe this is a mistake, please contact support.\n\n- Team Coding Sena")
	    		.build();
	    
	    try {
	    	CreateEmailResponse data = resend.emails().send(params);
	    	System.out.println(data.getId());
	    } catch (ResendException e) {
	    	e.printStackTrace();
	    }
	}

	@Override
	public void sendUnblockUserEmail(String to, String name) {
	    Resend resend = new Resend(resendApiKey);
	    
	    CreateEmailOptions params = CreateEmailOptions.builder()
	    		.from(fromEmail)
	    		.to(to)
	    		.subject("Account Unblocked")
	    		.text("Hello " + name + ",\n\nGood news! Your account has been unblocked and you can now access all features again.\n\nWelcome back!\n\n- Team Coding Sena")
	    		.build();
	    
	    try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void sendAccountDeactivationEmail(String to, String name) {
		Resend resend = new Resend(resendApiKey);
		
		CreateEmailOptions params = CreateEmailOptions.builder()
	    		.from(fromEmail)
	    		.to(to)
	    		.subject("Account Deactivated")
	    		.text("Hello " + name + ",\n\nWe regret to inform you that your account has been deactivated as per your request.\n\nIf you change your mind, you can reactivate your account anytime by contacting support.\n\n- Team Coding Sena")
	    		.build();
		
		try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void sendAccountActivationEmail(String to, String name) {
		Resend resend = new Resend(resendApiKey);
		
		CreateEmailOptions params = CreateEmailOptions.builder()
	    		.from(fromEmail)
	    		.to(to)
	    		.subject("Account Activated")
	    		.text("Hello " + name + ",\n\nGood news! Your account has been activated successfully and you can now access all features again.\n\nWelcome back!\n\n- Team Coding Sena")
	    		.build();
		
		try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
	}


}
