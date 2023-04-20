package com.ca.core.services;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ca.core.models.BlacklistedToken;
import com.ca.core.repository.BlacklistedTokenRepository;
import io.jsonwebtoken.Jwts;

@Service
public class JwtBlacklistService {

	@Value("${core.ca.jwtSecret}")
	private String jwtSecret;

	@Value("${core.ca.jwtExpirationMs}")
	private int jwtExpirationMs;
	
	@Autowired
	private BlacklistedTokenRepository blacklistedTokenRepository;

	public void blacklistToken(String authToken) {
		String jwt = authToken.substring(7); // Remove the "Bearer " prefix
		System.out.println("2");
		Date expiryDate = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getExpiration();
		String email = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getSubject();
		System.out.println("3");
		BlacklistedToken blacklistedToken = new BlacklistedToken();
		blacklistedToken.setToken(jwt);
		blacklistedToken.setEmail(email);
		blacklistedToken.setExpiryDate(expiryDate);
		System.out.println(blacklistedToken);
		blacklistedTokenRepository.save(blacklistedToken);
	}

	public boolean isTokenBlacklisted(String authToken) {
		//String jwt = authToken.substring(7); // Remove the "Bearer " prefix
		return blacklistedTokenRepository.existsByToken(authToken);
	}

	public void removeExpiredTokens() {
		LocalDateTime now = LocalDateTime.now();
		blacklistedTokenRepository.deleteByExpiryDateBefore(now);
	}
}
