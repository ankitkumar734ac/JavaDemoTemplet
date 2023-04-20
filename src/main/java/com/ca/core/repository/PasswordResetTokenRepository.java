package com.ca.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ca.core.models.PasswordResetToken;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
	PasswordResetToken findByToken(String token);
    PasswordResetToken findByEmail(String email);
    void deleteByEmail(String email);
}
