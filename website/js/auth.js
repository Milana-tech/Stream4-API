const loginSection = document.getElementById("loginSection");
const registerSection = document.getElementById("registerSection");

// Toggle to register
document.getElementById("registerBtn").addEventListener("click", () => {
    loginSection.style.display = "none";
    registerSection.style.display = "block";
});

// Toggle back to login
document.getElementById("backToLoginBtn").addEventListener("click", () => {
    registerSection.style.display = "none";
    loginSection.style.display = "block";
});

// LOGIN
document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const login = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login, password })
    });

    if (response.ok) {
        const data = await response.json();
        localStorage.setItem("token", data.token);
        window.location.href = "profiles.html";
    } else {
        alert("Invalid credentials!");
    }
});

// REGISTER
document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = document.getElementById("regName").value;
    const email = document.getElementById("regEmail").value;
    const password = document.getElementById("regPassword").value;

    const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password, role: "USER" })
    });

    if (response.ok) {
        alert("Account created! Please check your email to verify your account, then log in.");
        registerSection.style.display = "none";
        loginSection.style.display = "block";
    } else {
        alert("Registration failed. Email may already be in use.");
    }
});