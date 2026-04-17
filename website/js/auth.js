const API_BASE_URL = "http://localhost:8080/api";

const loginSection = document.getElementById("loginSection");
const registerSection = document.getElementById("registerSection");
const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");

// --- UI TOGGLING ---

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

// --- LOGIN LOGIC ---

loginForm.addEventListener("submit", async function(e) {
    e.preventDefault();

    // Use the IDs from your HTML - check if they are 'loginEmail' or just 'email'
    const email = document.getElementById("loginEmail")?.value || document.getElementById("email").value;
    const password = document.getElementById("loginPassword")?.value || document.getElementById("password").value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            alert("Invalid login credentials");
            return;
        }

        const data = await response.json();

        // Save token (make sure your backend returns 'token' or 'accessToken')
        localStorage.setItem("token", data.token || data.accessToken);

        // Redirect to profiles page
        window.location.href = "profiles.html";
    } catch (err) {
        console.error("Login Error:", err);
        alert("Server error. Is the backend running?");
    }
});

// --- REGISTER LOGIC ---

registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    
    const name = document.getElementById("regName").value;
    const email = document.getElementById("regEmail").value;
    const password = document.getElementById("regPassword").value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ 
                name, 
                email, 
                password, 
                role: "USER" // Default role
            })
        });

        if (response.ok) {
            alert("Account created! Please log in.");
            registerSection.style.display = "none";
            loginSection.style.display = "block";
        } else {
            const errorData = await response.json();
            alert("Registration failed: " + (errorData.message || "Email may be in use."));
        }
    } catch (err) {
        console.error("Registration Error:", err);
        alert("Server error during registration.");
    }
});