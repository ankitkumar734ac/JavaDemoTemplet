package com.ca.core.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ca.core.services.JwtBlacklistService;
import com.ca.core.services.UserDetailsImpl;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${core.ca.jwtSecret}")
	private String jwtSecret;

	@Value("${core.ca.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Autowired
	JwtBlacklistService jwtBlacklistService;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject((userPrincipal.getEmail())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		if (jwtBlacklistService.isTokenBlacklisted(authToken)) {
			//TODO Ankit :
			// Need to implement this Custom exception is jwt was blocked 
			// throw new JwtAuthenticationException("Invalid JWT token"); 
			logger.error("Blacklisted JWT token");
			return false;
		}
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		} catch (IncorrectClaimException e) {
			logger.error("Incorrect JWT claim: {}", e.getMessage());
		} catch (PrematureJwtException e) {
			logger.error("JWT token is not yet valid: {}", e.getMessage());
		} catch (MissingClaimException e) {
			logger.error("Missing required JWT claim: {}", e.getMessage());
		}

		return false;
	}
}
