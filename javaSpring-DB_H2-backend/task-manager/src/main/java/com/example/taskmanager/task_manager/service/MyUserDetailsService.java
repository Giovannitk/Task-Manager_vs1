package com.example.taskmanager.task_manager.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.taskmanager.task_manager.model.Task;
import com.example.taskmanager.task_manager.repository.TaskRepository;
import com.example.taskmanager.task_manager.repository.UserRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.taskmanager.task_manager.model.User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }

        // Converti l'oggetto User in un UserDetails di Spring Security
        UserBuilder builder = User.withUsername(user.getUsername());
        builder.password(user.getPassword());
        builder.roles(user.getRole());

        return builder.build();
    }
    
    public Map<String, String> registerNewUser(com.example.taskmanager.task_manager.model.User user, PasswordEncoder passwordEncoder) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        //String encodedPassword = passwordEncoder.encode(user.getPassword());
        //user.setPassword(encodedPassword);
        
        Map<String, String> response = new HashMap<>();
        
        if ("ADMIN".equals(user.getRole()) && userRepository.findByRole("ADMIN") != null) {
            user.setRole("USER");
            response.put("message", "Admin already exists, registered as USER instead.");
        } else {
        	user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(false); // Disabilitato finché non conferma

            // Genera il token di conferma e scadenza
            String confirmationToken = UUID.randomUUID().toString();
            user.setConfirmationToken(confirmationToken);
            user.setConfirmationExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)); // 24 ore

            userRepository.save(user);

			// Invia il token via email
            emailService.sendConfirmationEmail(user.getEmail(), confirmationToken);

            response.put("message", "User registered successfully! Check your email to confirm account.");
        }
        
        //userRepository.save(user);
        return response;
    }

	public void deleteUserById(Long id) {
		// TODO Auto-generated method stub
		userRepository.deleteById(id);
	}

	public void deleteAllTasksByUserId(Long id) {
	    com.example.taskmanager.task_manager.model.User user = userRepository.getReferenceById(id);
	    java.util.List<Task> tasks = taskRepository.findByUser(user);
	    
	    // Cancella tutti i task con una singola chiamata
	    taskRepository.deleteAll(tasks);
	}
	
	public boolean checkIfExistsAdmin() {
	    List<com.example.taskmanager.task_manager.model.User> admins = userRepository.findAllByRole("ADMIN");
	    return !admins.isEmpty();
	}


	public List<com.example.taskmanager.task_manager.model.User> getAllUsers() {
		return userRepository.findAll();
	}

	public String findRoleByUsername(String username) {
	    com.example.taskmanager.task_manager.model.User user = userRepository.findByUsername(username);
	    return user != null ? user.getRole() : null;
	}
	
	public com.example.taskmanager.task_manager.model.User saveUser(com.example.taskmanager.task_manager.model.User updatedUser) {
        return userRepository.save(updatedUser);
    }

	public Optional<com.example.taskmanager.task_manager.model.User> findUserById(Long id) {
        return userRepository.findById(id);
    }
	
	public int getCountTasksByUser(com.example.taskmanager.task_manager.model.User user) {
		return taskRepository.findByUser(user).size();
	}

	public Optional<com.example.taskmanager.task_manager.model.User> findByConfirmationToken(String token) {
	    return userRepository.findByConfirmationToken(token);
	}

	public boolean isUserEnabled(String username) {
	    com.example.taskmanager.task_manager.model.User user = userRepository.findByUsername(username);
	    return user != null && user.isEnabled();
	}
}
