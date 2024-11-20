import { setAuthHeaders } from '../utils.js';

export async function fetchUsers() {
  const response = await fetch('http://localhost:8081/users', {
    method: 'GET',
    headers: setAuthHeaders(),
  });

  if (response.ok) {
    return await response.json();
  } else {
    throw new Error("Errore nel caricamento degli utenti.");
  }
}

export async function deleteUser(userId) {
  const response = await fetch(`http://localhost:8081/users/${userId}`, {
    method: 'DELETE',
    headers: setAuthHeaders()
  });

  if (!response.ok) {
    throw new Error("Errore nella cancellazione dell'utente.");
  }
}

export async function updateUserRole(userId, role) {
  const response = await fetch(`http://localhost:8081/users/${userId}`, {
    method: 'PUT',
    headers: setAuthHeaders(),
    body: role
  });

  if (!response.ok) {
    throw new Error("Errore nell'aggiornamento del ruolo.");
  }
}
