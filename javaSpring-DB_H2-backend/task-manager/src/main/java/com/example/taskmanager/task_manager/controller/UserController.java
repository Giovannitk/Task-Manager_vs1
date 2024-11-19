package com.example.taskmanager.task_manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.taskmanager.security.JwtUtil;
import com.example.taskmanager.task_manager.model.ResponseLogin;
import com.example.taskmanager.task_manager.model.User;
import com.example.taskmanager.task_manager.model.UserWithTaskCountDTO;

@RestController
public class UserController {
    
    @Autowired
    private com.example.taskmanager.task_manager.service.MyUserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
        	//Validates the user’s data and uses userDetailsService to save it.
            Map<String, String> response = userDetailsService.registerNewUser(user, passwordEncoder);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
        	//Handles exceptions like duplicate usernames and returns an error message.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Errore durante la registrazione: " + ex.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        if (userDetails != null && passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
        	//Check if the password is valid.
            if (!userDetailsService.isUserEnabled(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account non confermato. Controlla la tua email.");
            }
            String token = jwtUtil.generateToken(userDetails);
            String role = userDetailsService.findRoleByUsername(user.getUsername());
            //Returns a JWT token if login was successful.
            return ResponseEntity.ok(new ResponseLogin(role, token));
        } else {
        	//Returns errors in case of invalid credentials or unconfirmed account.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali non valide.");
        }
    }
    
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmAccount(@RequestParam("token") String token) {

        //Confirm a user’s account using the token sent via email.
        Optional<User> userOptional = userDetailsService.findByConfirmationToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            //Check the validity of the token and update the user status.
            if (user.getConfirmationExpiryDate().after(new Date())) {
                user.setEnabled(true); // Enable account
                user.setConfirmationToken(null); // Removes the token
                user.setConfirmationExpiryDate(null);
                userDetailsService.saveUser(user);
                return ResponseEntity.ok("Account confermato con successo!");
            } else {
                return ResponseEntity.status(HttpStatus.GONE).body("Il token di conferma è scaduto.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token non valido.");
        }
    }

    /*
     * Update a user’s role. Only accessible to users with ADMIN role 
     * thanks to @PreAuthorize("hasRole('ADMIN')").*/
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateTask(@PathVariable Long id, @RequestBody String role) {
        Optional<User> user = userDetailsService.findUserById(id);
        if (user.isPresent()) {
            User updatedUser = user.get();
            updatedUser.setRole(role);
            userDetailsService.saveUser(updatedUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
     * Delete a user and all of its tasks. Returns an 
     * error if the ID does not exist.*/
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
    	try {
    		userDetailsService.deleteAllTasksByUserId(id);
    		userDetailsService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully");
    	}catch(EmptyResultDataAccessException ex) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("id=" + id + " -->" + ex.getMessage());
    	}
    }

    //Check if there is at least one user with ADMIN role.
    @GetMapping("/userAdmin")
    public ResponseEntity<Map<String, Boolean>> checkIfExistsAdmin() {
        boolean adminExists = userDetailsService.checkIfExistsAdmin();
        return ResponseEntity.ok(Map.of("adminExists", adminExists));
    }
    
    /*
     * Returns a list of users with their tasks. Requires administrator 
     * permissions.
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserWithTaskCountDTO>> getAllUsersWithTaskCount() {
        List<User> users = userDetailsService.getAllUsers();
        List<UserWithTaskCountDTO> usersWithTaskCounts = new ArrayList<>();

        for (User user : users) {
            int taskCount = userDetailsService.getCountTasksByUser(user);
            usersWithTaskCounts.add(new UserWithTaskCountDTO(user, taskCount));
        }

        return ResponseEntity.ok(usersWithTaskCounts);
    }
}

