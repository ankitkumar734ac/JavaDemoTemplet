package com.ca.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService  {
	@Autowired
    private JavaMailSender mailSender;
	
	@Value("${core.ca.endpoint}")
	private String END_POINT;
	
	public void sendPasswordResetEmail(String recipientEmail, String resetToken) {
		System.out.println(recipientEmail+" : "+resetToken);
        // Create a SimpleMailMessage object
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the recipient email address
        message.setTo(recipientEmail);

        // Set the email subject
        message.setSubject("Password Reset Request");

        // Set the email body
        String resetLink = END_POINT+"/api/v1/auth/password-reset?token=" + resetToken;
        message.setText("Hi,\n\nWe received a request to reset your password. If you did not request this, please ignore this email.\n\nTo reset your password, please click on the link below:\n\n" + resetLink);
        System.err.println(resetLink);
        // Send the email
        mailSender.send(message);
    }
}
