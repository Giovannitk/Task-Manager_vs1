package com.example.taskmanager.task_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.taskmanager.security.JwtUtil;
import com.example.taskmanager.task_manager.model.AuthenticationResponse;
import com.example.taskmanager.task_manager.model.User;

@RestController
public class UserController {
    
    @Autowired
    private com.example.taskmanager.task_manager.service.MyUserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
    	try {
    		return userDetailsService.registerNewUser(user, passwordEncoder);
    	}catch(IllegalArgumentException ex) {
    		return ex.getMessage();
    	}
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        if (userDetails != null && passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            String token = jwtUtil.generateToken(userDetails);  // Genera il token JWT
            return ResponseEntity.ok(new AuthenticationResponse(token));  // Restituisci il token
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed! Incorrect username or password.");
        }
    }

}

