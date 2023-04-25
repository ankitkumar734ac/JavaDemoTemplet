package com.ca.core.controllers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ca.core.models.Role;
import com.ca.core.models.User;
import com.ca.core.payload.response.ApiResponse;
import com.ca.core.repository.RoleRepository;
import com.ca.core.repository.UserRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	
	@GetMapping("/roles")
	public ResponseEntity<?> allRoles() {
		List<Role> allRoles  = roleRepository.findAll();
		List<String> roles = allRoles.stream().map(role -> role.getName().name()).collect(Collectors.toList());
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
