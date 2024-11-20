// This is Controller

import { isLoggedIn, logOut, getUserRole } from './services/authService.js';
import { fetchTasks, createTask, updateTask, deleteTask } from './services/taskService.js';
import { fetchUsers, deleteUser, updateUserRole } from './services/userService.js';
import { renderTasks, clearTaskForm } from './views/taskView.js';
import { renderUsers } from './views/userView.js';

// Login control
if (!isLoggedIn()) {
  alert("User not found. Try login again.");
  location.href = 'login.html';
}

// Role management
const role = getUserRole();
if (role === "ADMIN") {
  document.getElementById("task-section").classList.add("hidden");
  document.getElementById("user-management").classList.remove("hidden");
  loadUsers();
} else {
  document.getElementById("task-section").classList.remove("hidden");
  document.getElementById("user-management").classList.add("hidden");
  loadTasks();
}

// main functions
async function loadTasks() {
  try {
    const tasks = await fetchTasks();
    renderTasks(tasks, handleTaskUpdate, handleTaskDelete, handleTaskToggle);
  } catch (error) {
    alert(error.message);
  }
}

async function loadUsers() {
  try {
    const users = await fetchUsers();
    renderUsers(users, handleUserUpdate, handleUserDelete);
  } catch (error) {
    alert(error.message);
  }
}

// Event Handlers
async function handleTaskUpdate(task) {
  const newTitle = prompt("Enter new title:", task.title);
  const newDescription = prompt("Enter new description:", task.description);

  try {
    await updateTask(task.id, newTitle, newDescription, task.completed);
    loadTasks();
  } catch (error) {
    alert(error.message);
  }
}

async function handleTaskDelete(taskId) {
  try {
    await deleteTask(taskId);
    loadTasks();
  } catch (error) {
    alert(error.message);
  }
}

async function handleTaskToggle(task, isCompleted) {
  try {
    await updateTask(task.id, task.title, task.description, isCompleted);
    loadTasks();
  } catch (error) {
    alert(error.message);
  }
}

async function handleUserUpdate(userId) {
  const newRole = 'ADMIN';
  if (newRole) {
    try {
      await updateUserRole(userId, newRole);
      loadUsers();
    } catch (error) {
      alert(error.message);
    }
  }
}

async function handleUserDelete(userId) {
  try {
    await deleteUser(userId);
    loadUsers();
  } catch (error) {
    alert(error.message);
  }
}

// Form submission
document.getElementById('addTaskButton').addEventListener('click', async () => {
  const title = document.getElementById('title').value.trim();
  const description = document.getElementById('description').value.trim();

  if (!title || !description) {
    alert("Both title and description are required.");
    return;
  }

  try {
  await createTask(title, description);
  clearTaskForm();
  await loadTasks();
} catch (error) {
  console.error("Detailed error during task creation:", error.message, error.stack);
  alert(`Failed to create task: ${error.message}`);
}
  
});


// Logout
document.getElementById('logoutButton').addEventListener('click', logOut);
