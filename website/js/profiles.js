const API_BASE_URL = "http://localhost:8080";

const profilesGrid = document.getElementById("profilesGrid");
const addProfileBtn = document.getElementById("addProfileBtn");

// Load profiles from API
async function loadProfiles() {
    const token = localStorage.getItem("token");

    const res = await fetch(`${API_BASE_URL}/profiles`, {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if (!res.ok) {
        console.error("Failed to load profiles");
        return;
    }

    const profiles = await res.json();
    renderProfiles(profiles);
}

// Render profiles to the page
function renderProfiles(profiles) {
    profilesGrid.innerHTML = "";

    profiles.forEach(profile => {
        const card = document.createElement("div");
        card.classList.add("profile-card");

        card.innerHTML = `
            <div class="avatar">${profile.name.charAt(0)}</div>
            <p class="profile-name">${profile.name}</p>
        `;

        // Select profile
        card.addEventListener("click", () => {
            localStorage.setItem("activeProfileId", profile.id);
            localStorage.setItem("activeProfileName", profile.name);
            window.location.href = "home.html";
        });

        profilesGrid.appendChild(card);
    });
}

// Create a new profile
addProfileBtn.addEventListener("click", async () => {
    const name = prompt("Enter profile name:");
    const age = parseInt(prompt("Enter age:"));
    if (!name || !age) return;

    const token = localStorage.getItem("token");

    const res = await fetch(`${API_BASE_URL}/profiles`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ name, age })
    });

    if (!res.ok) {
        alert("Failed to create profile");
        return;
    }

    void loadProfiles();
});

// Load profiles on page start
void loadProfiles();
