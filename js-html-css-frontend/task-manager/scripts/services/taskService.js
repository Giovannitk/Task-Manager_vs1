import { setAuthHeaders } from '../utils.js';

// Function to load the task list for normal users.
export async function fetchTasks() {
  const response = await fetch('http://localhost:8081/tasks', {
    method: 'GET',
    headers: setAuthHeaders(),
  });

  if (response.ok) {
    return await response.json();
  } else {
    throw new Error("Errore nel caricamento dei task.");
  }
}

export async function createTask(title, description) {
  try {
    const response = await fetch('http://localhost:8081/tasks', {
      method: 'POST',
      headers: setAuthHeaders(),
      body: JSON.stringify({ title, description }),
    });

    console.log("Fetch response status:", response.status);
    
    // Debug: answer complete log
    const responseText = await response.text();
    console.log("Fetch response text:", responseText);

    if (!response.ok) {
      throw new Error(`Server error: ${response.status} - ${responseText}`);
    }

    const task = JSON.parse(responseText);
    console.log("Server returned new task:", task);
    return task;
  } catch (error) {
    console.error("Error during task creation:", error.message);
    //throw new Error("Failed to create task.");
  }
}



export async function updateTask(taskId, title, description, completed) {
  const response = await fetch(`http://localhost:8081/tasks/${taskId}`, {
    method: 'PUT',
    headers: setAuthHeaders(),
    body: JSON.stringify({ title, description, completed })
  });

  if (!response.ok) {
    throw new Error("Errore nell'aggiornamento del task.");
  }
}

export async function deleteTask(taskId) {
  const response = await fetch(`http://localhost:8081/tasks/${taskId}`, {
    method: 'DELETE',
    headers: setAuthHeaders()
  });

  if (!response.ok) {
    throw new Error("Errore nella cancellazione del task.");
  }
}
