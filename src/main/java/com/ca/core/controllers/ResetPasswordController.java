package com.ca.core.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.ca.core.models.PasswordResetForm;
import com.ca.core.models.PasswordResetToken;
import com.ca.core.models.User;
import com.ca.core.payload.request.ResetPasswordRequest;
import com.ca.core.payload.response.ApiResponse;
import com.ca.core.repository.PasswordResetTokenRepository;
import com.ca.core.repository.RoleRepository;
import com.ca.core.repository.UserRepository;
import com.ca.core.security.jwt.JwtUtils;
import com.ca.core.services.EmailService;
import com.ca.core.services.JwtBlacklistService;
import com.ca.core.services.PasswordResetTokenService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class ResetPasswordController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordResetTokenService passwordResetService;

	@PostMapping("/password-reset")
	public String resetPassword(@Valid @ModelAttribute("passwordResetForm") PasswordResetForm form,
			BindingResult bindingResult, Model model) {
		System.out.println(form);
		if (bindingResult.hasErrors()) {
			return "reset-password";
		}
		PasswordResetToken passwordResetToken = passwordResetService.getPasswordResetToken(form.getToken());
		if (passwordResetToken == null) {
			model.addAttribute("message", "Invalid password reset token");
			return "error";
		} else if (passwordResetService.isExpired(passwordResetToken)) {
			model.addAttribute("message", "Password reset token has expired");
			return "error";
		} else {
			Optional<User> userOptional = userRepository.findByEmail(passwordResetToken.getEmail());
			System.out.println(passwordResetToken.getEmail());
			if (!userOptional.isPresent()) {
				model.addAttribute("message", "Account is not found!");
				return "error";
			} else if (form.getPassword().equals(form.getConformPassword())) {
				User user = userOptional.get();
				user.setPassword(form.getPassword());
				userRepository.save(user);
				model.addAttribute("message", "Password reset successfully");
				return "reset-password-success";
			} else {
				model.addAttribute("message", "password and cnf password not matched!");
				return "error";
			}

		}

	}

	@GetMapping("/password-reset")
	public ModelAndView showResetPasswordForm(@RequestParam("token") String token, Model model) {
		System.err.println("1=======>");
		ModelAndView modelAndView = new ModelAndView();
		PasswordResetToken passwordResetToken = passwordResetService.getPasswordResetToken(token);
		System.err.println("2=======>" + passwordResetToken);
		if (passwordResetToken == null) {
			System.err.println("3=======>");
			model.addAttribute("message", "Invalid password reset token");
			return new ModelAndView("error");
		} else if (passwordResetService.isExpired(passwordResetToken)) {
			modelAndView.setViewName("error");
			System.err.println("4=======>");
			modelAndView.addObject("message", "Password reset token has expired");
		} else {
			System.err.println("5=======>");
			model.addAttribute("token", token);
			model.addAttribute("passwordResetForm", new PasswordResetForm());
			return new ModelAndView("reset-password");
		}
		System.err.println("6=======>");
		return modelAndView;
	}

	@GetMapping("/forgot-password")
	public ResponseEntity<?> resetPassword(@RequestParam String email) {
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (!userOptional.isPresent()) {
			// User with the provided email does not exist
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("email", "User with email " + email + " does not exist");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse(false, "Email does not exit!", errorMap));
		}
		System.out.println(email);

		User user = userOptional.get();

		PasswordResetToken passwordResetToken = passwordResetService.createPasswordResetTokenForEmail(email);
		System.out.println(passwordResetToken.getToken());
		emailService.sendPasswordResetEmail(user.getEmail(), passwordResetToken.getToken());
		System.out.println("=======pass sended===>");
		Map<String, String> successMap = new HashMap<>();
		successMap.put("email", "Password reset link has been sent to " + user.getEmail());
		return ResponseEntity.ok(new ApiResponse(true, "Success", successMap));
	}

}
