package com.redplutoanalytics.callpluto.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redplutoanalytics.callpluto.dto.LoginRequest;
import com.redplutoanalytics.callpluto.dto.RegisterRequest;
import com.redplutoanalytics.callpluto.login.security.JwtUtils;
import com.redplutoanalytics.callpluto.model.Users;
import com.redplutoanalytics.callpluto.repository.UserRepository;
import com.redplutoanalytics.callpluto.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    
	 @Autowired
	    private AuthService authService;

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private JwtUtils jwtUtils;

	    @PostMapping("/register")
	    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
	        Users user = new Users();
	        user.setUsername(request.getUsername());
	        user.setPasswordHash(request.getPassword());
	        user.setFullName(request.getFullName());
	        user.setEmail(request.getEmail());
	        return ResponseEntity.ok(authService.registerUser(user));
	    }

	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
	        Users user = authService.authenticate(request.getUsername(), request.getPassword());
	        if (user != null) {
	            String token = jwtUtils.generateToken(user.getUsername());
	            return ResponseEntity.ok(Map.of(
	                "message", "Login successful",
	                "username", user.getUsername(),
	                "token", token
	            ));
	        } else {
	            return ResponseEntity.status(401).body("Invalid credentials");
	        }
	    }

	    @GetMapping("/all")
	    public List<Users> getAllUsers() {
	        return userRepository.findAll();
	    }

	    @PostMapping("/logout")
	    public ResponseEntity<String> logout() {
	        return ResponseEntity.ok("Logged out successfully");
	    }
	    @GetMapping("/validate")
		public ResponseEntity<?> validateToken(HttpServletRequest request) {
		    String authHeader = request.getHeader("Authorization");
		    if (authHeader != null && authHeader.startsWith("Bearer ")) {
		        String token = authHeader.substring(7);
		        boolean isValid = jwtUtils.validateToken(token);
		        if (isValid) {
		            return ResponseEntity.ok(Map.of("valid", true));
		        }
		    }
		    return ResponseEntity.status(401).body(Map.of("valid", false, "error", "Invalid or missing token"));
		}
		@GetMapping("/me")
		public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
		    String authHeader = request.getHeader("Authorization");
		    if (authHeader != null && authHeader.startsWith("Bearer ")) {
		        String token = authHeader.substring(7);
		        String username = jwtUtils.getUsernameFromToken(token);
		        Users user = userRepository.findByUsername(username).orElse(null);
		        if (user != null) {
		            return ResponseEntity.ok(Map.of(
		                "username", user.getUsername(),
		                "email", user.getEmail(),
		                "fullName", user.getFullName()
		            ));
		        }
		    }
		    return ResponseEntity.status(401).body("Unauthorized");
		}
	}