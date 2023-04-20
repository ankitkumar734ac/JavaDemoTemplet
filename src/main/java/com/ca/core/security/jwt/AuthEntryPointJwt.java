package com.ca.core.security.jwt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.ca.core.payload.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("Unauthorized error: {}", authException.getMessage());
		//Custom response if Authentication failed
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		Map<String, String> authError  = new HashMap<>();
		
		/*// TODO Ankit :
			// Need to handle all jwt Custom exception and return well formated response
	    String errorMessage = "Authentication failed";
		HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
		if (authException instanceof InvalidJwtTokenException) {
			authError.put("error", "Invalid token");
        } else if (authException instanceof ExpiredJwtTokenException) {
        	authError.put("error", "Token is expired");
        } else if (authException instanceof BlockedJwtTokenException) {
        	authError.put("error", "Token is blocked");
        } else if (authException instanceof UnsupportedJwtTokenException) {
        	authError.put("error", "Unsupported token");
        } else {
            errorMessage = "Internal server error";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
		*/
		
		authError.put("error",authException.getMessage());
		ApiResponse errorResponse = new ApiResponse(false, "errorMessage", authError);
		OutputStream out = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, errorResponse);
		out.flush();
	}

}