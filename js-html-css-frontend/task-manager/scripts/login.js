const form = document.querySelector("form"),
  usernameField = form.querySelector(".username-field"),
  usernameInput = usernameField.querySelector(".username"),
  passField = form.querySelector(".password-field"),
  passInput = passField.querySelector(".password");



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

// Calling Function on form Submit
form.addEventListener("submit", async function(event) {
  event.preventDefault(); //preventing form submitting
  checkUsername();
  createPass();

  //Calling function on key up
  usernameInput.addEventListener("keyup", checkUsername);
  passInput.addEventListener("keyup", createPass);

  if(!usernameField.classList.contains("invalid") && !passField.classList.contains("invalid")){

    const username = usernameInput.value;
    const password = passInput.value;
    
    fetch("http://localhost:8081/login", {
      method: "POST",
      headers: {
          "Content-Type": "application/json"
      },
      body: JSON.stringify({ username, password })
    })
    .then(response => {
      if (!response.ok) { 
        if (response.status === 401) {
          alert("Incorrect password.");
          throw new Error("Login failed! Incorrect password.");
        } else if (response.status === 403) {
          alert("Incorrect username or password.");
          throw new Error("Login failed! Access forbidden.");
        } else if (response.status === 500) {
          alert("Server error. Try later.");
          throw new Error("Server error.");
        } else {
          alert("Errore sconosciuto. Riprova.");
          throw new Error("Unknown error. Try again.");
        }
      }
      return response.json(); // Converte la risposta in JSON se tutto va bene
    })
    .then(data => {
      const token = data.jwt; // Accede al token
      console.log("Token:", token);

      localStorage.setItem('token', token);
      localStorage.setItem('username', username);

      localStorage.setItem("role", data.role);

      location.href = form.getAttribute("action");  // Login effettuato con successo.
    })
    .catch(error => {
      console.error("Error:", error);
      alert("Login failed");
      return;  // Interrompe il flusso in caso di errore
    });
  }
});