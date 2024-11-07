package com.example.taskmanager.task_manager.model;

public class UserWithTaskCountDTO {
    private User user;
    private int taskCount;

    public UserWithTaskCountDTO(User user, int taskCount) {
        this.setUser(user);
        this.setTaskCount(taskCount);
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

    // Getter e setter
}

