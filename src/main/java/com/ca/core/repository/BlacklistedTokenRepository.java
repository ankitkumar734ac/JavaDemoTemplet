package com.ca.core.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.ca.core.models.BlacklistedToken;

public interface BlacklistedTokenRepository extends MongoRepository<BlacklistedToken, String> {
    Boolean existsByToken(String token);
    String findByToken(String token);
    List<BlacklistedToken> findAllByEmail(String email);
    long deleteByExpiryDateBefore(LocalDateTime now);
}
