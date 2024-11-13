package com.example.taskmanager.task_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskmanager.task_manager.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);  // Metodo per trovare un utente in base al nome utente
    List<User> findAllByRole(String role); // Metodo per trovare tutti gli utenti in base ad un ruolo
    User findByRole(String role); // Metodo per trovare un utente in base al rulo
    Optional<User> findByConfirmationToken(String confirmationToken);
}
