const loginSection = document.getElementById("loginSection");
const registerSection = document.getElementById("registerSection");
const forgotSection = document.getElementById("forgotSection");
const resetSection = document.getElementById("resetSection");

function showOnly(section) {
    [loginSection, registerSection, forgotSection, resetSection]
        .forEach(s => s.style.display = "none");
    section.style.display = "block";
}

// Auto-show reset form if token is in URL
const urlParams = new URLSearchParams(window.location.search);
const urlToken = urlParams.get("reset-token");
if (urlToken) {
    document.getElementById("resetToken").value = urlToken;
    showOnly(resetSection);
}

// Auto-show register form if invitation token is in URL
const invitationToken = urlParams.get("invitationToken");
if (invitationToken) {
    showOnly(registerSection);
}

document.getElementById("registerBtn").addEventListener("click", () => showOnly(registerSection));
document.getElementById("backToLoginBtn").addEventListener("click", () => showOnly(loginSection));
document.getElementById("forgotBtn").addEventListener("click", () => showOnly(forgotSection));
document.getElementById("backToLoginFromForgot").addEventListener("click", () => showOnly(loginSection));
document.getElementById("backToLoginFromReset").addEventListener("click", () => showOnly(loginSection));

// LOGIN
document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const login = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({ login, password })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            window.location.href = "profiles.html";
        } else {
            const err = await response.json().catch(() => null);
            alert(err?.message || "Invalid credentials!");
        }
    } catch (e) {
        alert("Could not reach the server. Make sure the backend is running on port 8080.");
    }
});

// FORGOT PASSWORD
document.getElementById("forgotForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("forgotEmail").value;

    try {
        const response = await fetch("http://localhost:8080/auth/forgot-password", {
            method: "POST",
            headers: { "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({ email })
        });

        if (response.ok) {
            alert("Reset link sent! Check your email for the token, then come back to enter it.");
            showOnly(resetSection);
        } else {
            const err = await response.json().catch(() => null);
            alert(err?.message || "Could not send reset link.");
        }
    } catch (e) {
        alert("Could not reach the server. Make sure the backend is running on port 8080.");
    }
});

// RESET PASSWORD
document.getElementById("resetForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const token = document.getElementById("resetToken").value;
    const newPassword = document.getElementById("resetPassword").value;

    try {
        const response = await fetch("http://localhost:8080/auth/reset-password", {
            method: "POST",
            headers: { "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({ token, newPassword })
        });

        if (response.ok) {
            alert("Password reset successfully! You can now log in.");
            showOnly(loginSection);
        } else {
            const err = await response.json().catch(() => null);
            alert(err?.message || "Reset failed. Your token may have expired.");
        }
    } catch (e) {
        alert("Could not reach the server. Make sure the backend is running on port 8080.");
    }
});

// REGISTER
document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = document.getElementById("regName").value;
    const email = document.getElementById("regEmail").value;
    const password = document.getElementById("regPassword").value;

    try {
        const response = await fetch("http://localhost:8080/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({ name, email, password, role: "USER", invitationToken: invitationToken || undefined })
        });

        if (response.ok) {
            alert("Account created! Please check your email to verify your account, then log in.");
            registerSection.style.display = "none";
            loginSection.style.display = "block";
        } else {
            const err = await response.json().catch(() => null);
            alert(err?.message || "Registration failed. Email may already be in use.");
        }
    } catch (e) {
        alert("Could not reach the server. Make sure the backend is running on port 8080.");
    }
});
