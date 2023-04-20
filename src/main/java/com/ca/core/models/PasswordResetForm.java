package com.ca.core.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordResetForm {
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 20, message = "Invalid password")
	//Need to update regexp like  passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&~`#^()[\]{}|\\,.:;'"<>/])[A-Za-z\d@$!%*?&~`#^()[\]{}|\\,.:;'"<>/]+$/;
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+", message = "Invalid password")
	private String password;
	
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 20, message = "Invalid password")
	//Need to update regexp like  passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&~`#^()[\]{}|\\,.:;'"<>/])[A-Za-z\d@$!%*?&~`#^()[\]{}|\\,.:;'"<>/]+$/;
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+", message = "Invalid password")
	private String conformPassword;
	
	private String token;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConformPassword() {
		return conformPassword;
	}

	public void setConformPassword(String conformPassword) {
		this.conformPassword = conformPassword;
	}
	
}
