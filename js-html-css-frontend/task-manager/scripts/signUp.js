const form = document.querySelector("form"),
  usernameField = form.querySelector(".username-field"),
  usernameInput = usernameField.querySelector(".username"),

  passField = form.querySelector(".password-field"),
  passInput = passField.querySelector(".password"),

  passConfField = form.querySelector(".confirm-field"),
  passConfInput = passConfField.querySelector(".confirm-pass");

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

// Calling Function on form Submit
form.addEventListener("submit", async function(event) {
  event.preventDefault(); //preventing form submitting
  checkUsername();
  createPass();
  confirmPass();

  //Calling function on key up
  usernameInput.addEventListener("keyup", checkUsername);
  passInput.addEventListener("keyup", createPass);

  if(!usernameField.classList.contains("invalid") && !passField.classList.contains("invalid")){

    const username = usernameInput.value;
    const password = passInput.value;
    
    fetch("http://localhost:8081/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ username, password })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error("Signup failed!");
      }
      // Controlla il tipo di contenuto della risposta
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        return response.json();
      } else {
        return response.text(); // gestisce risposte non JSON
      }
    })
    .then(data => {
      location.href = form.getAttribute("action"); // Registrazione effettuata con successo.
    })
    .catch(error => console.error("Error:", error));
  }
});