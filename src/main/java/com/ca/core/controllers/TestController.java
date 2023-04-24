package com.ca.core.controllers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.core.payload.response.ApiResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	@GetMapping("/roles")
	public ResponseEntity<?> allRoles() {
		List<String> roles = new ArrayList<>();
		roles.add("user");
		roles.add("mod");
		roles.add("admin");
		Map<String, Object> data = new HashMap<>();
		data.put("roles", roles);
		ApiResponse response = new ApiResponse(true, "Success", data);
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> allAccess() {
		String message = "Public Content.";
	    ApiResponse response = new ApiResponse(true, "Success", message);
	    return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> userAccess() {
		String message =  "User Content.";
		ApiResponse response = new ApiResponse(true, "Success", message);
	    return ResponseEntity.ok().body(response);
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<?> moderatorAccess() {
		String message =  "Moderator Board.";
		ApiResponse response = new ApiResponse(true, "Success", message);
	    return ResponseEntity.ok().body(response);
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> adminAccess() {
		String message =  "Admin Board.";
		ApiResponse response = new ApiResponse(true, "Success", message);
	    return ResponseEntity.ok().body(response);
	}
}
