package com.ca.core.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blacklisted_tokens")
public class BlacklistedToken {
    @Id
    private String id;
    private String token;
    private String email;
    private Date expiryDate;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	@Override
	public String toString() {
		return "BlacklistedToken [id=" + id + ", token=" + token + ", email=" + email + ", expiryDate=" + expiryDate
				+ "]";
	}

    
}

