package com.example.taskmanager.task_manager.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskmanager.task_manager.model.Task;
import com.example.taskmanager.task_manager.model.User;

public interface TaskRepository extends JpaRepository<com.example.taskmanager.task_manager.model.Task, Long> {
	List<Task> findByUser(User user);
	
	@Transactional
	public void deleteAllByUser(User user);

}
