package com.example.taskmanager.task_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.example.taskmanager.task_manager.model.Task;
import com.example.taskmanager.task_manager.model.User;
import com.example.taskmanager.task_manager.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private com.example.taskmanager.task_manager.repository.TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<Task> getTasksForUser(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        return taskRepository.findByUser(user);
    }


    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("No task with ID " + id + " found.");
        }
    }
    
    public Task createTask(Task task, UserDetails userDetails) {
        // Ottieni l'utente dal contesto di sicurezza
        User user = userRepository.findByUsername(userDetails.getUsername());
        
        // Associa l'utente al task
        task.setUser(user);
        
        // Salva il task
        return taskRepository.save(task);
    }

}

