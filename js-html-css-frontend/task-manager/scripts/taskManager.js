const username = localStorage.getItem('username');
document.querySelector("h1").innerText = `Benvenuto ${username}`;

const token = localStorage.getItem('token');
console.log(token);
console.log(typeof(token));

async function fetchTasks() {
	const response = await fetch('http://localhost:8081/tasks', {
			method: 'GET',
			headers: {
					'Content-Type': 'application/json',
					'Authorization': `Bearer ${token}`
			},
	});
  const tasks = await response.json();
  const taskList = document.getElementById('taskList');
  taskList.innerHTML = '';  // Pulizia della lista prima di aggiornare

  tasks.forEach(task => {
    const li = document.createElement('li');
    li.innerHTML = `
      <span>${task.title} - ${task.description} (Completed: ${task.completed})</span>
      <button class="update-button" data-id="${task.id}">Update</button>
      <button class="delete-button" data-id="${task.id}">Delete</button>
      <input type="checkbox" class="complete-checkbox" data-id="${task.id}" ${task.completed ? 'checked' : ''}>
    `;
    taskList.appendChild(li);

    // listener per aggiornare/eliminare/completare
    li.querySelector('.update-button').addEventListener('click', () => updateTask(task.id));
    li.querySelector('.delete-button').addEventListener('click', () => deleteTask(task.id));
    li.querySelector('.complete-checkbox').addEventListener('change', () => toggleTaskCompletion(task, !task.completed));
  });

	document.getElementById('addTaskButton').addEventListener('click', createTask);
}

// Funzione per recuperare i task
// document.getElementById('taskForm').addEventListener('submit', async function(event) {
//   event.preventDefault();

//   const title = document.getElementById('title').value;
//   const description = document.getElementById('description').value;

//   const response = await fetch('http://localhost:8081/tasks', {
//         method: 'GET',
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${token}`
//         },
//     });

//   if (response.ok) {
//       fetchTasks();  // Aggiorna la lista delle attività
//   }
// });

// Funzione per creare un nuovo task
async function createTask() {
	const newTitle = document.getElementById('title').value;
	const newDescription = document.getElementById('description').value;
	
	try{
		const response = await fetch(`http://localhost:8081/tasks`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': `Bearer ${token}`
			},
			body: JSON.stringify({
				title: newTitle,
				description: newDescription
			})
		});
	
		if (response.ok) {
			await fetchTasks();  // Aggiorna la lista
		}else{
			throw new Error(`Task with ID ${taskId} not found.`);
      }
  
    }catch(error){
      //alert(error.message);
    }
  }

// Funzione per aggiornare un task
async function updateTask(taskId) {
  const newTitle = prompt("Enter new title:");
  const newDescription = prompt("Enter new description:");
  
  try{
    const response = await fetch(`http://localhost:8081/tasks/${taskId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            title: newTitle,
            description: newDescription,
            completed: false  // Puoi gestire anche il completamento qui
        })
    });
  
    if (response.ok) {
      await fetchTasks();  // Aggiorna la lista
    }else{
			throw new Error(`Task with ID ${taskId} not found.`);
		}

  }catch(error){
    alert(error.message);
  }
}

// Funzione per eliminare un task
async function deleteTask(taskId) {
	try{
		const response = await fetch(`http://localhost:8081/tasks/${taskId}`, {
				method: 'DELETE',
				headers: {
						'Content-Type': 'application/json',
						'Authorization': `Bearer ${token}`
				}
		});
	
		if (response.ok) {
			await fetchTasks();  // Aggiorna la lista
		}else{
			throw new Error(`Task with ID ${taskId} not found.`);
		}
	}catch(error){
			alert(error.message);
	}
}

// Funzione per marcare come completato/uncompleted
async function toggleTaskCompletion(task, isCompleted) {
  try {
		const response = await fetch(`http://localhost:8081/tasks/${task.id}`, {
				method: 'PUT',
				headers: {
					'Content-Type': 'application/json',
					'Authorization': `Bearer ${token}`
				},
				body: JSON.stringify({
						title: task.title,
						description: task.description,
						completed: isCompleted
				})
		});
		if (!response.ok) {
				throw new Error(`Task with ID ${task} not found.`);
		}
		await fetchTasks();
  } catch (error) {
      console.error(error);
      alert(error.message);
  }
}


// Inizializza il caricamento delle attività al caricamento della pagina
fetchTasks();




// const tasks = [{
//     id: 1,
//     title: "Peppe",
//     description: "Franco è peppe",
//     completed: false
// }];
// tasks.forEach(task => {
//     const li = document.createElement('li');
//     li.innerHTML = `
//       <span>${task.title} - ${task.description} (Completed: ${task.completed})</span>
//       <button class="update-button" data-id="${task.id}">Update</button>
//       <button class="delete-button" data-id="${task.id}">Delete</button>
//       <input type="checkbox" class="complete-checkbox" data-id="${task.id}" ${task.completed ? 'checked' : ''}>
//     `;
//     taskList.appendChild(li);

//     // Aggiungi listener per aggiornare/completare/eliminare
//     li.querySelector('.update-button').addEventListener('click', () => updateTask(task.id));
//     li.querySelector('.delete-button').addEventListener('click', () => deleteTask(task.id));
//     li.querySelector('.complete-checkbox').addEventListener('change', () => toggleTaskCompletion(task.id, !task.completed));
//   });