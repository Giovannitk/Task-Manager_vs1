//The class is a REST controller that handles HTTP requests related to tasks.
package com.example.taskmanager.task_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.example.taskmanager.task_manager.model.Task;

@RestController
@RequestMapping("/tasks")  //All end-points defined in the class start with /tasks.
public class TaskController {

    @Autowired
    private com.example.taskmanager.task_manager.service.TaskService taskService;

    @GetMapping //Associate the HTTP GET endpoint.
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") //Requires the user to have the USER or ADMIN role.
    // Retrieves the authenticated user from the security context and 
    // returns the tasks filtered through the service,
    public List<Task> getTasks() {
        // Get the authenticated user.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Pass the user to the service to filter tasks
        return taskService.getTasksForUser(userDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.findTaskById(id);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    //@RequestBody: Maps the JSON request body to a Task object.
    public Task createTask(@RequestBody Task task) { //Retrieve the authenticated user and use the service to create a task.
        // Get the authenticated user.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Pass authenticated user to the service.
        return taskService.createTask(task, userDetails);
    }

    @PutMapping("/{id}")
    // Search for the task to update and modify its fields if found.
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Optional<Task> task = taskService.findTaskById(id);
        if (task.isPresent()) {
            Task updatedTask = task.get();
            updatedTask.setTitle(taskDetails.getTitle());
            updatedTask.setDescription(taskDetails.getDescription());
            updatedTask.setCompleted(taskDetails.isCompleted());
            taskService.saveTask(updatedTask);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

