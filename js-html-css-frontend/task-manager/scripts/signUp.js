//This is Controller

import { signUpUser, validateEmail, hCheckAdmin, passVisibility, validateUsername, validatePassword } from "./services/userService.js";

const form = document.querySelector("form"),
  usernameField = form.querySelector(".username-field"),
  usernameInput = usernameField.querySelector(".username"),

  emailField = form.querySelector(".email-field"),
  emailInput = emailField.querySelector(".email"),

  passField = form.querySelector(".password-field"),
  passInput = passField.querySelector(".password"),

  passConfField = form.querySelector(".confirm-field"),
  passConfInput = passConfField.querySelector(".confirm-pass");

async function hideCheckAdmin() {
  try {
    const data = await hCheckAdmin();

    if (data.adminExists) {
      document.querySelector('.js-checkbox-role').classList.add("hidden");
      document.getElementById("admin-info").classList.remove("hidden");
    }
  } catch (error) {
    console.error("Error checking admin status:", error);
    alert(error.message);
  }
}

hideCheckAdmin();  
  
// Email validation
function checkEmail() {
  if (!validateEmail(emailInput.value)) {
    emailField.classList.add("invalid");
    return false;
  }
  emailField.classList.remove("invalid");
  return true;
}

// Username validation
function checkUsername() {
  if (!validateUsername(usernameInput.value)) {
    usernameField.classList.add("invalid");
    return false;
  }
  usernameField.classList.remove("invalid");
  return true;
}

// Hide and show password
passVisibility();

// Password validation
function checkPassword() {
  if (!validatePassword(passInput.value)) {
    passField.classList.add("invalid");
    return false;
  }
  passField.classList.remove("invalid");
  return true;
}

//Confirm Password
function confirmPass(){
  if(passConfInput.value !== passInput.value){
    passConfField.classList.add("invalid");
    return false;
  }
  passConfField.classList.remove("invalid");
  return true;
}

function getSelectedRole() {
  return document.querySelector('.js-checkbox-role input[type="checkbox"]').checked ? 'ADMIN' : 'USER';
}

// Calling Function on form Submit
// Form submission
form.addEventListener("submit", async function(event) {
  event.preventDefault();

  const isEmailValid = checkEmail();
  const isUsernameValid = checkUsername();
  const isPasswordValid = checkPassword();
  const isConfirmPassValid = confirmPass();

  if (!isUsernameValid || !isPasswordValid || !isEmailValid || !isConfirmPassValid) {
    alert("Please fix the validation errors.");
    return;
  }

  try{
    // if (![usernameField, emailField, passField, passConfField].some(field => field.classList.contains("invalid"))) {
    const username = usernameInput.value;
    const email = emailInput.value;
    const password = passInput.value;
    const role = getSelectedRole();

    const data = await signUpUser(username, email, password, role);

    alert(data.message.includes("Admin already exists") ? data.message : "Registration successful!");
    location.href = form.getAttribute("action");
    //}
    form.reset();
  } catch (error) {
    if (error.message.includes("Admin already exists")) {
      alert("An admin user already exists. Cannot create another admin.");
    } else {
      alert(`Registration failed: ${error.message}`);
    }
    console.error("Error:", error.message);
  }
});