package com.codingsena.codingsena_backend.services;

import java.io.IOException;

import jakarta.mail.MessagingException;

public interface EmailService {
	void sendEmailVerificationEmail(String to, String name, String link) throws MessagingException, IOException;
	void sendConfirmEmailVerificationEmail(String to, String name);
	void sendPromoteUserEmail(String to, String name);
	void sendDemoteUserEmail(String to, String name);
	void sendBlockUserEmail(String to, String name);
	void sendUnblockUserEmail(String to, String name);
	void sendAccountDeactivationEmail(String to, String name);
	void sendAccountActivationEmail(String to, String name);
}
