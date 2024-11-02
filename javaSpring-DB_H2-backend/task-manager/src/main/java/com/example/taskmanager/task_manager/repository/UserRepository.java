package com.example.taskmanager.task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskmanager.task_manager.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);  // Metodo per trovare un utente in base al nome utente
    User findByRole(String role); // Metodo per trovare un utente in base al rulo
}
