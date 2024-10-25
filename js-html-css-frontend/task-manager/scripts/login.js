const form = document.querySelector("form"),
  usernameField = form.querySelector(".username-field"),
  usernameInput = usernameField.querySelector(".username"),
  passField = form.querySelector(".password-field"),
  passInput = passField.querySelector(".password");

// Username validation
function checkUsername() {
  const usernamePattern = /^[^ ]+@[^ ]+\.[a-z]/;
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
  const passPattern = /^(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\d@!%*?&]{8,}$/;

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
            console.log("cipolla");
            throw new Error("Login failed!");
        }
        return response.json(); // Converte la risposta in JSON
    })
    .then(data => {
        console.log("Response JSON:", data);
        const token = data.jwt; // Accede al token
        console.log("Token:", token);
        // Qui puoi salvare il token in localStorage o usarlo per successive richieste

        //location.href = form.getAttribute("action");  // Login effettuato con successo.
    })
    .catch(error => console.error("Error:", error));
  }
});