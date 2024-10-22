package com.example.taskmanager.task_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.taskmanager.task_manager.repository.UserRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.taskmanager.task_manager.model.User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Converti l'oggetto User in un UserDetails di Spring Security
        UserBuilder builder = User.withUsername(user.getUsername());
        builder.password(user.getPassword());
        builder.roles(user.getRole());

        return builder.build();
    }
    
    public String registerNewUser(com.example.taskmanager.task_manager.model.User user, PasswordEncoder passwordEncoder) {
    	if (userRepository.findByUsername(user.getUsername()) != null) {
    	    throw new IllegalArgumentException("Username already exists");
    	}
    	
    	// Crittografa la password prima di salvarla
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        
        user.setPassword(encodedPassword);
        
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // Ruolo predefinito
        }
        
        // Salva l'utente nel database
        userRepository.save(user);
        
        return "User registered successfully!";
    }

	public void deleteUserById(Long id) {
		// TODO Auto-generated method stub
		userRepository.deleteById(id);
	}
	
	public com.example.taskmanager.task_manager.model.User getUserById(Long id) {
	    return userRepository.findById(id)
	                         .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

}
