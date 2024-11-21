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

export function validateEmail(email) {
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return emailPattern.test(email);
}

export function validateUsername(username) {
  const usernamePattern = /^[a-zA-Z0-9_-]{8,16}$/;
  return usernamePattern.test(username);
}

export function validatePassword(password) {
  const passPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$!%*?&])[A-Za-z\d$!%*?&]{8,}$/;
  return passPattern.test(password);
}

export async function loginUser(username, password) {
  const response = await fetch("http://localhost:8081/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ username, password })
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("Incorrect password.");
    } else if (response.status === 403) {
      throw new Error("Incorrect username or password.");
    } else if (response.status === 500) {
      throw new Error("Server error. Try later.");
    } else {
      throw new Error("Unknown error. Try again.");
    }
  }
  
  return response.json(); // Return JSON data if login is successful
}

export async function signUpUser(username, email, password, role){
  const response = await fetch("http://localhost:8081/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ username, email, password, role })
  });

  if (!response.ok) {
    const errorData = await response.json(); // If the server returns a useful message
    throw new Error(errorData.message || "Signup failed!");
  }

  return response.json(); // Return JSON data if signup is successful
}

export function passVisibility(){
  // Toggle password visibility
  const eyeIcons = document.querySelectorAll(".show-hide");
  eyeIcons.forEach(eyeIcon => {
    eyeIcon.addEventListener("click", () => {
      const pInput = eyeIcon.parentElement.querySelector("input");
      if (pInput.type === "password") {
        eyeIcon.classList.replace("bx-hide", "bx-show");
        pInput.type = "text";
      } else {
        eyeIcon.classList.replace("bx-show", "bx-hide");
        pInput.type = "password";
      }
    });
  });
}

export async function hCheckAdmin() {
  const response = await fetch("http://localhost:8081/userAdmin");

  if (!response.ok) {  // Check if the answer is 200 OK
    throw new Error(`HTTP error! Status: ${response.status}`);
  }

  return response.json();
}