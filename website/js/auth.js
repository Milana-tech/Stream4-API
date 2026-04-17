const loginForm = document.getElementById("loginForm");
const registerBtn = document.getElementById("registerBtn");

loginForm.addEventListener("submit", function(e) 
{
  e.preventDefault();
  window.location.href = "profiles.html";
});

registerBtn.addEventListener("click", function() 
{
  alert("Register flow later");
});
