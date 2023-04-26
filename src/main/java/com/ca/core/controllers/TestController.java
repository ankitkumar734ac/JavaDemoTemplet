package com.ca.core.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
		List<Object> roles = new ArrayList<>();
		
		for(Role role : allRoles) {
			Map<String, String> jsonRole = new HashMap<>();
			jsonRole.put("DisplayName", role.getName().name()=="ROLE_ADMIN" ? "Admin" : role.getName().name()=="ROLE_MODERATOR"?"Moderator":"User" );
			jsonRole.put("Value", role.getName().name());
			roles.add(jsonRole);
		}
		
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
	@GetMapping("/app-info")
	public ResponseEntity<?> getPlaylode() {
		String filename = "src/main/resources/app-data.json";
        //ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(filename);
        System.out.println(filename);
        try {
            // Parse the contents of the JSON file
            String contents = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(contents);

            // Return the JSON object as a response
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file " + filename);
        }
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
	@GetMapping("/all-user")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getUsersFromDB(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		System.out.println("Page:"+page+" : Size: "+size);
		Pageable paging = PageRequest.of(page, size, Sort.by("username"));
		Page<User> pageUsers = userRepository.findAll(paging);
		List<User> users = pageUsers.getContent();
		Map<String, Object> data = new HashMap<>();
		if (users.isEmpty()) {
			return ResponseEntity.ok(HttpStatus.NO_CONTENT);
		}
		data.put("currentPage", pageUsers.getNumber());
		data.put("totalItems", pageUsers.getTotalElements());
		data.put("totalPages", pageUsers.getTotalPages());
		data.put("Users", users);
		ApiResponse response = new ApiResponse(true, "Success", data);
		return ResponseEntity.ok().body(response);
	}
}
