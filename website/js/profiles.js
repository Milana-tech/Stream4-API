const profilesGrid = document.getElementById("profilesGrid");
const addProfileBtn = document.getElementById("addProfileBtn");

async function renderProfiles() {
    const token = localStorage.getItem("token"); // Get JWT from login

    try {
        const response = await fetch("http://localhost:8080/api/profiles", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Accept": "application/json"
            }
        });

        if (!response.ok) throw new Error("Failed to fetch profiles");

        const profiles = await response.json();
        profilesGrid.innerHTML = "";

        profiles.forEach((profile) => {
            const card = document.createElement("div");
            card.classList.add("profile-card");

            card.innerHTML = `
                <div class="avatar">${profile.name.charAt(0)}</div>
                <p class="profile-name">${profile.name}</p>
                <button class="edit-btn">Edit</button>
                <button class="delete-btn">Delete</button>
            `;

            // Select profile
            card.addEventListener("click", () => {
                // Store the ID (for API calls) and Name (for the UI)
                localStorage.setItem("activeProfileId", profile.id);
                localStorage.setItem("activeProfile", profile.name);
                window.location.href = "home.html";
            });

            // Edit
            card.querySelector(".edit-btn").addEventListener("click", async (e) => {
                e.stopPropagation(); // prevent card click
                const newName = prompt("New profile name:", profile.name);
                if (!newName) return;
                await fetch(`http://localhost:8080/api/profiles/${profile.id}`, {
                    method: "PUT",
                    headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
                    body: JSON.stringify({ name: newName })
                });
                renderProfiles();
            });

            // Delete
            card.querySelector(".delete-btn").addEventListener("click", async (e) => {
                e.stopPropagation(); // prevent card click
                if (!confirm(`Delete "${profile.name}"?`)) return;
                await fetch(`http://localhost:8080/api/profiles/${profile.id}`, {
                    method: "DELETE",
                    headers: { "Authorization": `Bearer ${token}` }
                });
                renderProfiles();
            });

            profilesGrid.appendChild(card);
        });
    } catch (error) {
        console.error("Error:", error);
        profilesGrid.innerHTML = "<p>Please log in to view profiles.</p>";
    }
}

addProfileBtn.addEventListener("click", async () => {
    const name = prompt("Enter profile name:");
    if (!name) return;

    const token = localStorage.getItem("token");

    try {
        const response = await fetch("http://localhost:8080/api/profiles", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ name: name })
        });

        if (response.ok) {
            renderProfiles(); // Refresh the list from the DB
        } else {
            alert("Could not create profile.");
        }
    } catch (error) {
        console.error("Error creating profile:", error);
    }
});

// Initial load
renderProfiles();