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
    const response = await fetch("http://localhost:8081/userAdmin");

    if (!response.ok) {  // Controlla se la risposta è 200 OK
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const data = await response.json();

    if (data.adminExists) {
      document.querySelector('.js-checkbox-role').classList.add("hidden");
      document.getElementById("admin-info").classList.remove("hidden");
    }
  } catch (error) {
    console.error("Error checking admin status:", error);
  }
}

hideCheckAdmin();  
  
// Email validation
function checkEmail() {
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!emailInput.value.match(emailPattern)) {
    return emailField.classList.add("invalid");
  }
  emailField.classList.remove("invalid");
}

// Username validation
function checkUsername() {
  const usernamePattern = /^[a-zA-Z0-9_-]{8,16}$/;
  if(!usernameInput.value.match(usernamePattern)){
    return usernameField.classList.add("invalid");
  }
  usernameField.classList.remove("invalid");
}

// Hide and show password
const eyeIcons = document.querySelectorAll(".show-hide");
eyeIcons.forEach(eyeIcon => {
  eyeIcon.addEventListener("click", () => {
    const pInput = eyeIcon.parentElement.querySelector("input");
    if(pInput.type === "password"){
      eyeIcon.classList.replace("bx-hide", "bx-show");
      return (pInput.type = "text");
    }
    eyeIcon.classList.replace("bx-show", "bx-hide");
    pInput.type = "password";
  });
});

//Password Validation
function createPass(){
  const passPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$!%*?&])[A-Za-z\d$!%*?&]{8,}$/;

  if(!passInput.value.match(passPattern)){
    return passField.classList.add("invalid");
  }
  passField.classList.remove("invalid");
}

//Confirm Password
function confirmPass(){
  if(passConfInput.value !== passInput.value){
    return passConfField.classList.add("invalid");
  }
  passConfField.classList.remove("invalid");
}

// // Calling Function on form Submit

// Form submission
form.addEventListener("submit", async function(event) {
  event.preventDefault();
  checkUsername();
  checkEmail();
  createPass();
  confirmPass();

  if (![usernameField, emailField, passField, passConfField].some(field => field.classList.contains("invalid"))) {
    const username = usernameInput.value;
    const email = emailInput.value;
    const password = passInput.value;
    const role = document.querySelector('.js-checkbox-role input[type="checkbox"]').checked ? 'ADMIN' : 'USER';

    fetch("http://localhost:8081/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ username, email, password, role })
    })
    .then(response => {
      if (!response.ok) throw new Error("Signup failed!");
      return response.json();
    })
    .then(data => {
      alert(data.message.includes("Admin already exists") ? data.message : "Registration successful!");
      location.href = form.getAttribute("action");
    })
    .catch(error => console.error("Error:", error));
  }
});