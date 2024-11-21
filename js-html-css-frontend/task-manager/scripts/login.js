//This is Controller

import { validateUsername, validatePassword, loginUser, passVisibility } from './services/userService.js';
import { saveAuthData } from './services/authService.js';

const form = document.querySelector("form"),
  usernameField = form.querySelector(".username-field"),
  usernameInput = usernameField.querySelector(".username"),
  passField = form.querySelector(".password-field"),
  passInput = passField.querySelector(".password");

// Username validation
function checkUsername() {
  if (!validateUsername(usernameInput.value)) {
    usernameField.classList.add("invalid");
    return false;
  }
  usernameField.classList.remove("invalid");
  return true;
}

// Password validation
function checkPassword() {
  if (!validatePassword(passInput.value)) {
    passField.classList.add("invalid");
    return false;
  }
  passField.classList.remove("invalid");
  return true;
}

// Handle form submission
form.addEventListener("submit", async (event) => {
  event.preventDefault();

  const isUsernameValid = checkUsername();
  const isPasswordValid = checkPassword();

  if (!isUsernameValid || !isPasswordValid) {
    alert("Please fix the validation errors.");
    return;
  }

  try {
    const username = usernameInput.value;
    const password = passInput.value;

    const data = await loginUser(username, password);

    const token = data.jwt;
    const role = data.role;

    saveAuthData(token, username, role);

    console.log("Login successful, redirecting...");
    location.href = form.getAttribute("action");
  } catch (error) {
    console.error("Error:", error.message);
    alert(error.message);
  }
});

// Hide and show password
passVisibility();