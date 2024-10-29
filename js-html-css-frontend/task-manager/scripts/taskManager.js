const username = localStorage.getItem('username');
document.querySelector("h1").innerText = `Benvenuto ${username}`;

const token = localStorage.getItem('token');
if (!token) {
  alert("Token non trovato. Effettua nuovamente il login.");
} else {
  fetchTasks();
}

async function fetchTasks() {
  const response = await fetch('http://localhost:8081/tasks', {
    method: 'GET',
    headers: setAuthHeaders(),
  });

  const tasks = await response.json();
  const taskList = document.getElementById('taskList');
  taskList.innerHTML = '';  // Pulizia della tabella prima di aggiornare

  tasks.forEach(task => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${task.title}</td>
      <td>${task.description}</td>
      <td><input type="checkbox" class="complete-checkbox" data-id="${task.id}" ${task.completed ? 'checked' : ''}></td>
      <td>
        <button class="update-button" data-id="${task.id}">Update</button>
        <button class="delete-button" data-id="${task.id}">Delete</button>
      </td>
    `;
    taskList.appendChild(row);

    listenerUpdateDeleteIsCompleted(row, task);
  }); 

  document.getElementById('addTaskButton').addEventListener('click', createTask);
}


// Funzione per creare un nuovo task
async function createTask() {
  const newTitle = document.getElementById('title').value;
  const newDescription = document.getElementById('description').value;

  try {
    const response = await fetch(`http://localhost:8081/tasks`, {
      method: 'POST',
      headers: setAuthHeaders(),
      body: JSON.stringify({ title: newTitle, description: newDescription })
    });

    if (response.ok) {
      fetchTasks();  // Aggiorna la lista
    } else {
      throw new Error("Errore nella creazione del task.");
    }

  } catch (error) {
    //alert(error.message);
    return;
  }
}

// Funzione per aggiornare un task
async function updateTask(taskId) {
  const newTitle = prompt("Enter new title:");
  const newDescription = prompt("Enter new description:");

  try {
    const response = await fetch(`http://localhost:8081/tasks/${taskId}`, {
      method: 'PUT',
      headers: setAuthHeaders(),
      body: JSON.stringify({ title: newTitle, description: newDescription, completed: false })
    });

    if (response.ok) {
      fetchTasks();  // Aggiorna la lista
    } else {
      throw new Error("Errore nell'aggiornamento del task.");
    }

  } catch (error) {
    alert(error.message);
    return;
  }
}

// Funzione per eliminare un task
async function deleteTask(taskId) {
  try {
    const response = await fetch(`http://localhost:8081/tasks/${taskId}`, {
      method: 'DELETE',
      headers: setAuthHeaders()
    });

    if (response.ok) {
      fetchTasks();  // Aggiorna la lista
    } else {
      throw new Error("Errore nella cancellazione del task.");
    }
  } catch (error) {
    alert(error.message);
    return;
  }
}

// Funzione per marcare come completato/uncompleted
async function toggleTaskCompletion(task, isCompleted) {
  try {
    const response = await fetch(`http://localhost:8081/tasks/${task.id}`, {
      method: 'PUT',
      headers: setAuthHeaders(),
      body: JSON.stringify({ title: task.title, description: task.description, completed: isCompleted })
    });

    if (!response.ok) {
      throw new Error(`Task with ID ${task.id} not found.`);
    }
    fetchTasks();
  } catch (error) {
    console.error(error);
    alert(error.message);
    return;
  }
}

function listenerUpdateDeleteIsCompleted(li, task) {
  li.querySelector('.update-button').addEventListener('click', () => updateTask(task.id));
  li.querySelector('.delete-button').addEventListener('click', () => deleteTask(task.id));
  li.querySelector('.complete-checkbox').addEventListener('change', () => toggleTaskCompletion(task, !task.completed));
}

function setAuthHeaders(){
	return {
		'Content-Type': 'application/json',
		'Authorization': `Bearer ${token}`
	};
}