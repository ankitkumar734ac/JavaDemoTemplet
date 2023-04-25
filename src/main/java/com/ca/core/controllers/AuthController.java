package com.ca.core.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ca.core.models.ERole;
import com.ca.core.models.Role;
import com.ca.core.models.User;
import com.ca.core.payload.request.LoginRequest;
import com.ca.core.payload.request.ResetPasswordRequest;
import com.ca.core.payload.request.SignupRequest;
import com.ca.core.payload.response.ApiResponse;
import com.ca.core.payload.response.JwtResponse;
import com.ca.core.payload.response.MessageResponse;
import com.ca.core.repository.RoleRepository;
import com.ca.core.repository.UserRepository;
import com.ca.core.security.jwt.AuthTokenFilter;
import com.ca.core.security.jwt.JwtUtils;
import com.ca.core.services.EmailService;
import com.ca.core.services.JwtBlacklistService;
import com.ca.core.services.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	JwtBlacklistService jwtBlacklistService;

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordData) {
		Optional<User> userOptional = userRepository.findByEmail(resetPasswordData.getEmail());
		if (!userOptional.isPresent()) {
			// User with the provided email does not exist
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("email", "User with email " + resetPasswordData.getEmail() + " does not exist");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse(false, "Email does not exit!", errorMap));
		}
		if (resetPasswordData.getPassword().equals(resetPasswordData.getConformPassword())) {
			User user = userOptional.get();
			user.setPassword(encoder.encode(resetPasswordData.getPassword()));
			userRepository.save(user);
			System.out.println(resetPasswordData);
			System.out.println(user);
			Map<String, String> successMap = new HashMap<>();
			successMap.put("password", "Password reset successfully!");
			return ResponseEntity.ok(new ApiResponse(true, "Success", successMap));
		} else {
			Map<String, String> successMap = new HashMap<>();
			successMap.put("password", "Password and conform password not matched!");
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Password reset failed!", successMap));
		}
	}

	@GetMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestParam String email) {
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (!userOptional.isPresent()) {
			// User with the provided email does not exist
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("email", "User with email " + email + " does not exist");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse(false, "Email does not exit!", errorMap));
		}

		// Here This code is normal password reseter
		Map<String, String> successMap = new HashMap<>();
		successMap.put("email", "User Exited, Send Email with password to reset!");
		return ResponseEntity.ok(new ApiResponse(true, "Success", successMap));

	}

	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String tokenHeader) {
		System.out.println("1==========>"+tokenHeader);
		jwtBlacklistService.blacklistToken(tokenHeader);
		System.out.println("2");
		Map<String, String> successMap = new HashMap<>();
		successMap.put("data", "Successfully logged out");
		return ResponseEntity.ok(new ApiResponse(true, "Success", successMap));
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, BindingResult result) {

		if (result.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError error : result.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
			}
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Validation error", errorMap));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("email", "Email is already in use!");
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Error ", errorMap));
		}
		// Create new user's account
		User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));
		Set<String> strRoles = signUpRequest.getRoles();
		System.out.println("==============================>: "+strRoles);
		Set<Role> roles = new HashSet<>();
		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "ROLE_ADMIN":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					break;
				case "ROLE_MODERATOR":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);
					break;
				default:
					Role userRole = roleRepository.findByName(ERole.valueOf(role))
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		Map<String, String> successMap = new HashMap<>();
		successMap.put("user", "User registered successfully!");
		return ResponseEntity.ok().body(new ApiResponse(true, "Success", successMap));

	}
}
