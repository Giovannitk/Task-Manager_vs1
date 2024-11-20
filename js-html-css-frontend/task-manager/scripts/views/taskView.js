// Renders the list of tasks
export function renderTasks(tasks, onTaskUpdate, onTaskDelete, onTaskToggle) {
  const taskList = document.getElementById('taskList');
  taskList.innerHTML = '';

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

    // Attach event listeners
    row.querySelector('.update-button').addEventListener('click', () => onTaskUpdate(task));
    row.querySelector('.delete-button').addEventListener('click', () => onTaskDelete(task.id));
    row.querySelector('.complete-checkbox').addEventListener('change', () => onTaskToggle(task, !task.completed));
  });
}

// Clears the form after adding a task
export function clearTaskForm() {
  document.getElementById('title').value = '';
  document.getElementById('description').value = '';
}
