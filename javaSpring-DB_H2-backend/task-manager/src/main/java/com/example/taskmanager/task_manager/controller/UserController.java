package com.example.taskmanager.task_manager.controller;

//Annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//Class-Utility
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

//Custom-Classes
import org.springframework.security.core.userdetails.UserDetails;
import com.example.taskmanager.security.JwtUtil;
import com.example.taskmanager.task_manager.model.AuthenticationResponse;
import com.example.taskmanager.task_manager.model.User;
import com.example.taskmanager.task_manager.service.TaskService;

//Exceptions
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
public class UserController {
    
    @Autowired
    private com.example.taskmanager.task_manager.service.MyUserDetailsService userDetailsService;
    
    @Autowired
    private TaskService taskService;
    
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

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            taskService.deleteAllTasksByUser(userDetailsService.getUserById(id));
            userDetailsService.deleteUserById(id);
            return ResponseEntity.ok("User and tasks deleted successfully");
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("id=" + id + " -->" + ex.getMessage());
        }
    }

}

