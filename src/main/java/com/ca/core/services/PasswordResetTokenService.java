package com.ca.core.services;

import org.springframework.stereotype.Service;

import com.ca.core.models.PasswordResetToken;
import com.ca.core.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@Service
public class PasswordResetTokenService {
	@Value("${core.ca.expiration.in.minutes}")
    private int EXPIRATION_IN_MINUTES ;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    public PasswordResetToken createPasswordResetTokenForEmail(String email) {
    	// TODO Ankit: Generate a secure random token using a library like Apache
    	 		// Commons Codec
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(EXPIRATION_IN_MINUTES);
        System.out.println("-------------->"+expiryDate);
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, email, expiryDate);
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void deletePasswordResetToken(String email) {
        passwordResetTokenRepository.deleteByEmail(email);
    }
    public boolean isExpired(PasswordResetToken token) {
        LocalDateTime expiryDate = token.getExpiryDate();
        LocalDateTime now = LocalDateTime.now();
        return expiryDate.isBefore(now);
    }

}
