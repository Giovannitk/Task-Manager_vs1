const username = localStorage.getItem('username');
document.querySelector(".header").innerText = `Benvenuto ${username}`;

const token = localStorage.getItem('token');
const role = localStorage.getItem('role'); // Recupera il ruolo dell'utente

if (!token) {
  alert("User not found. Try login again.");
  location.href = 'login.html';
} else {
  // Verifica il ruolo e mostra la sezione appropriata
  if (role === "ADMIN") {
    document.getElementById("task-section").classList.add("hidden");
    document.getElementById("user-management").classList.remove("hidden");
    fetchUsers(); // Carica la lista degli utenti per l'admin
  } else {
    document.getElementById("task-section").classList.remove("hidden");
    document.getElementById("user-management").classList.add("hidden");
    fetchTasks(); // Carica la lista dei task per gli utenti normali
  }
}

// Funzione per caricare la lista task per gli utenti normali
async function fetchTasks() {
  const response = await fetch('http://localhost:8081/tasks', {
    method: 'GET',
    headers: setAuthHeaders(),
  });

  const tasks = await response.json();
  const taskList = document.getElementById('taskList');
  taskList.innerHTML = ''; // Pulizia della tabella prima di aggiornare

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

// Funzione per caricare la lista degli utenti per l'admin
async function fetchUsers() {
  const response = await fetch('http://localhost:8081/users', {
    method: 'GET',
    headers: setAuthHeaders(),
  });

  const usersCount = await response.json();
  const userList = document.getElementById('userList');
  userList.innerHTML = ''; // Pulizia della lista prima di aggiornare

  usersCount.forEach(userCount => {
    if(userCount.user.role !== 'ADMIN'){
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${userCount.user.username}</td>
        <td>${userCount.user.role}</td>
        <td>${userCount.taskCount}</td>
        <td>
          <button class="update-button" data-id="${userCount.user.id}">Update Role</button>
          <button class="delete-button" data-id="${userCount.user.id}">Delete</button>
        </td>
      `;
      
      row.querySelector('.update-button').addEventListener('click', () => updateUserRole(userCount.user.id));
      row.querySelector('.delete-button').addEventListener('click', () => deleteUser(userCount.user.id));

      userList.appendChild(row);
    }
  });
}

// Funzione per creare un nuovo task
async function createTask() {
  const newTitle = document.getElementById('title').value;
  const newDescription = document.getElementById('description').value;

  try {
    if(newTitle==='' || newDescription==='') return;

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

// Funzione per eliminare un utente
async function deleteUser(userId) {
  try {
    const response = await fetch(`http://localhost:8081/users/${userId}`, {
      method: 'DELETE',
      headers: setAuthHeaders()
    });

    if (response.ok) {
      fetchUsers();  // Aggiorna la lista
    } else {
      throw new Error("Errore nella cancellazione dell'utente.");
    }
  } catch (error) {
    alert(error.message);
    return;
  }
}

// Funzione per aggiornare il ruolo di un utente
async function updateUserRole(userId) {
  try {
    const response = await fetch(`http://localhost:8081/users/${userId}`, {
      method: 'PUT',
      headers: setAuthHeaders(),
      body: 'ADMIN'
    });

    if (response.ok) {
      fetchUsers();  // Aggiorna la lista
    } else {
      throw new Error("Errore nell'aggiornamento dell'utente.");
    }

  } catch (error) {
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

document.getElementById('logoutButton').addEventListener('click', logOut);

function logOut() {
  location.href = 'login.html';
  localStorage.removeItem('token');
  localStorage.removeItem('role'); // Rimuovi il ruolo al logout
}