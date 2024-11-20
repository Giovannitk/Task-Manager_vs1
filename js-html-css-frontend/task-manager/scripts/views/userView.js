// Renders the list of users
export function renderUsers(users, onUserUpdate, onUserDelete) {
  const userList = document.getElementById('userList');
  userList.innerHTML = '';

  users.forEach(user => {
    if(user.user.role !== 'ADMIN'){
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${user.user.username}</td>
        <td>${user.user.role}</td>
        <td>${user.taskCount}</td>
        <td>
          <button class="update-button" data-id="${user.user.id}">Update Role</button>
          <button class="delete-button" data-id="${user.user.id}">Delete</button>
        </td>
      `;
      userList.appendChild(row);

      // Attach event listeners
      row.querySelector('.update-button').addEventListener('click', () => onUserUpdate(user.user.id));
      row.querySelector('.delete-button').addEventListener('click', () => onUserDelete(user.user.id));
    }
  });
}