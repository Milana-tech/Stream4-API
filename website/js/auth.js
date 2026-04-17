const loginForm = document.getElementById("loginForm");
const registerBtn = document.getElementById("registerBtn");

loginForm.addEventListener("submit", async function(e) {
  e.preventDefault();
  
  const email = e.target.querySelector('input[type="email"]').value;
  const password = e.target.querySelector('input[type="password"]').value;

  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ login: email, password: password })
    });

    if (response.ok) {
      const data = await response.json();
      // data.token comes from your UserLoginResponse
      localStorage.setItem("token", data.token); 
      window.location.href = "profiles.html";
    } else {
      alert("Invalid credentials!");
    }
  } catch (err) {
    console.error("Connection failed", err);
  }
});

registerBtn.addEventListener("click", function() 
{
  alert("Register flow later");
});
