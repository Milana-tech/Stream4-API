const API_BASE_URL = "http://localhost:8080/api";

const loginForm = document.getElementById("loginForm");
const registerBtn = document.getElementById("registerBtn");

loginForm.addEventListener("submit", async function(e)
{
  e.preventDefault();

  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  try {
      const red = await fetch(`${API_BASE_URL}/auth/login`, {
          method: "POST",
          headers: {"Content-Type": "application/json"},
          body: JSON.stringify({email, password})
      });

      if(!red.ok)
      {
          alert("Invalid login");
          return;
      }

      const data = await red.json();

      //Save data
      localStorage.setItem("token", data.token);

      //Redirect to profiles page
      window.location.href = "profiles.html";
  } catch (err) {
      console.error(err);
      alert("Server error");
  }
});

registerBtn.addEventListener("click", function() 
{
  alert("Register flow later");
});
