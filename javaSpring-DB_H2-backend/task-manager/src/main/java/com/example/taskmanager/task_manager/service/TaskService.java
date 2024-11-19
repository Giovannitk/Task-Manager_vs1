/*
 * This class handles the business logic related to tasks.
 * It is annotated with @Service, making it a component managed by Spring.*/
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

@Service //Indicates that this class is a service component managed by Spring.
public class TaskService {

    @Autowired
    private com.example.taskmanager.task_manager.repository.TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;

    //Takes a UserDetails object (representing the authenticated user).
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
        // Obtains the user in the security context
        User user = userRepository.findByUsername(userDetails.getUsername());
        
        // Associate the user with the task.
        task.setUser(user);
        
        // Save task.
        return taskRepository.save(task);
    }

}

