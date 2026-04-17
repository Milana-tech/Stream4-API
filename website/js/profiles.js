const API_BASE_URL = "http://localhost:8080/api";

const profilesGrid = document.getElementById("profilesGrid");
const addProfileBtn = document.getElementById("addProfileBtn");
const token = localStorage.getItem("token");

// 1. Load profiles from API
async function loadProfiles() {
    if (!token) {
        console.error("No token found, redirecting to login...");
        window.location.href = "index.html";
        return;
    }

    try {
        const res = await fetch(`${API_BASE_URL}/profiles`, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Accept": "application/json"
            }
        });

        if (!res.ok) throw new Error("Failed to fetch profiles");

        const profiles = await res.json();
        renderProfiles(profiles);
    } catch (error) {
        console.error("Error loading profiles:", error);
        profilesGrid.innerHTML = "<p>Error loading profiles. Please log in again.</p>";
    }
}

// 2. Render profiles to the page with Edit/Delete functionality
function renderProfiles(profiles) {
    profilesGrid.innerHTML = "";

    profiles.forEach((profile) => {
        const card = document.createElement("div");
        card.classList.add("profile-card");

        card.innerHTML = `
            <div class="avatar">${profile.name.charAt(0)}</div>
            <p class="profile-name">${profile.name}</p>
            <div class="profile-actions">
                <button class="edit-btn">Edit</button>
                <button class="delete-btn">Delete</button>
            </div>
        `;

        // Action: Select Profile
        card.addEventListener("click", () => {
            localStorage.setItem("activeProfileId", profile.id);
            localStorage.setItem("activeProfileName", profile.name); // Matches main's naming
            window.location.href = "home.html";
        });

        // Action: Edit (with stopPropagation to prevent selecting the profile)
        card.querySelector(".edit-btn").addEventListener("click", async (e) => {
            e.stopPropagation();
            const newName = prompt("New profile name:", profile.name);
            if (!newName) return;

            try {
                const res = await fetch(`${API_BASE_URL}/profiles/${profile.id}`, {
                    method: "PUT",
                    headers: { 
                        "Authorization": `Bearer ${token}`, 
                        "Content-Type": "application/json" 
                    },
                    body: JSON.stringify({ name: newName })
                });
                if (res.ok) loadProfiles();
            } catch (err) {
                console.error("Update failed:", err);
            }
        });

        // Action: Delete
        card.querySelector(".delete-btn").addEventListener("click", async (e) => {
            e.stopPropagation();
            if (!confirm(`Delete "${profile.name}"?`)) return;

            try {
                const res = await fetch(`${API_BASE_URL}/profiles/${profile.id}`, {
                    method: "DELETE",
                    headers: { "Authorization": `Bearer ${token}` }
                });
                if (res.ok) loadProfiles();
            } catch (err) {
                console.error("Delete failed:", err);
            }
        });

        profilesGrid.appendChild(card);
    });
}

// 3. Create a new profile (Includes Age from main)
addProfileBtn.addEventListener("click", async () => {
    const name = prompt("Enter profile name:");
    const ageInput = prompt("Enter age:");
    if (!name || !ageInput) return;

    const age = parseInt(ageInput);

    try {
        const res = await fetch(`${API_BASE_URL}/profiles`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ name, age })
        });

        if (res.ok) {
            loadProfiles();
        } else {
            alert("Could not create profile. Check if the age is valid.");
        }
    } catch (error) {
        console.error("Error creating profile:", error);
    }
});

// 4. Initial load
loadProfiles();