const profilesGrid = document.getElementById("profilesGrid");
const addProfileBtn = document.getElementById("addProfileBtn");
const profileModal = document.getElementById("profileModal");
const profileForm = document.getElementById("profileForm");
const cancelProfileBtn = document.getElementById("cancelProfileBtn");
const preferencesModal = document.getElementById("preferencesModal");
const preferencesForm = document.getElementById("preferencesForm");
const cancelPrefBtn = document.getElementById("cancelPrefBtn");

cancelPrefBtn.addEventListener("click", () => preferencesModal.style.display = "none");

preferencesForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const profileId = document.getElementById("prefProfileId").value;
    const token = localStorage.getItem("token");

    const preferredType = document.querySelector("input[name='preferredType']:checked")?.value || null;
    const minimumMaturityRating = document.getElementById("prefMaturity").value || null;
    const preferredGenres = [...document.querySelectorAll("#preferencesForm input[type='checkbox']:checked")]
        .map(cb => cb.value);

    try {
        const response = await fetch(`http://localhost:8080/profiles/${profileId}/preferences`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({ preferredGenres, preferredType, minimumMaturityRating })
        });

        if (response.ok) {
            preferencesModal.style.display = "none";
            alert("Preferences saved!");
        } else {
            const err = await response.json().catch(() => null);
            alert(err?.message || "Could not save preferences.");
        }
    } catch (error) {
        alert("Could not reach the server.");
    }
});

async function openPreferences(profileId, token) {
    document.getElementById("prefProfileId").value = profileId;

    // Reset form
    document.querySelectorAll("input[name='preferredType']")[0].checked = true;
    document.getElementById("prefMaturity").value = "";
    document.querySelectorAll("#preferencesForm input[type='checkbox']").forEach(cb => cb.checked = false);

    // Load existing preferences
    try {
        const res = await fetch(`http://localhost:8080/profiles/${profileId}/preferences`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (res.ok) {
            const prefs = await res.json();
            if (prefs.preferredType) {
                const radio = document.querySelector(`input[name='preferredType'][value='${prefs.preferredType}']`);
                if (radio) radio.checked = true;
            }
            if (prefs.minimumMaturityRating) document.getElementById("prefMaturity").value = prefs.minimumMaturityRating;
            if (prefs.preferredGenres) {
                prefs.preferredGenres.forEach(genre => {
                    const cb = document.querySelector(`#preferencesForm input[value='${genre}']`);
                    if (cb) cb.checked = true;
                });
            }
        }
    } catch (_) {}

    preferencesModal.style.display = "flex";
}

addProfileBtn.addEventListener("click", () => {
    profileForm.reset();
    document.querySelectorAll(".avatar-picker span").forEach(s => s.classList.remove("selected"));
    document.getElementById("profileAvatar").value = "";
    profileModal.style.display = "flex";
});

document.querySelectorAll(".avatar-picker span").forEach(span => {
    span.addEventListener("click", () => {
        document.querySelectorAll(".avatar-picker span").forEach(s => s.classList.remove("selected"));
        span.classList.add("selected");
        document.getElementById("profileAvatar").value = span.dataset.emoji;
    });
});

cancelProfileBtn.addEventListener("click", () => {
    profileModal.style.display = "none";
});

profileForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const name = document.getElementById("profileName").value;
    const age = parseInt(document.getElementById("profileAge").value, 10);
    const avatar = document.getElementById("profileAvatar").value.trim() || null;
    const contentFilters = [...document.querySelectorAll("#profileForm input[type='checkbox']:checked")]
        .map(cb => cb.value);

    const token = localStorage.getItem("token");

    try {
        const response = await fetch("http://localhost:8080/profiles", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({ name, age, avatar, contentFilters })
        });

        if (response.ok) {
            profileModal.style.display = "none";
            renderProfiles();
        } else {
            const err = await response.json().catch(() => null);
            if (err?.errors && Object.keys(err.errors).length > 0) {
                const messages = Object.entries(err.errors)
                    .map(([field, msg]) => field === "age" ? "Invalid age. Age must be between 3 and 95." : msg)
                    .join("\n");
                alert(messages);
            } else {
                alert(err?.message || "Could not create profile.");
            }
        }
    } catch (error) {
        alert("Could not reach the server.");
    }
});

async function renderProfiles() {
    const token = localStorage.getItem("token");

    try {
        const response = await fetch("http://localhost:8080/profiles", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Accept": "application/json"
            }
        });

        if (response.status === 401) { window.location.href = "index.html"; return; }
        if (!response.ok) throw new Error("Failed to fetch profiles");

        const profiles = await response.json();
        profilesGrid.innerHTML = "";

        profiles.forEach((profile) => {
            const card = document.createElement("div");
            card.classList.add("profile-card");

            const avatarContent = profile.avatar
                ? `<div class="avatar">${profile.avatar}</div>`
                : `<div class="avatar">${profile.name.charAt(0)}</div>`;

            card.innerHTML = `
                ${avatarContent}
                <p class="profile-name">${profile.name}</p>
                <p class="profile-meta">${profile.maturityLevel}</p>
                <button class="edit-btn">Edit</button>
                <button class="pref-btn">Preferences</button>
                <button class="delete-btn">Delete</button>
            `;

            card.addEventListener("click", () => {
                localStorage.setItem("activeProfileId", profile.id);
                localStorage.setItem("activeProfile", profile.name);
                window.location.href = "home.html";
            });

            card.querySelector(".pref-btn").addEventListener("click", (e) => {
                e.stopPropagation();
                openPreferences(profile.id, token);
            });

            card.querySelector(".edit-btn").addEventListener("click", async (e) => {
                e.stopPropagation();
                const newName = prompt("New profile name:", profile.name);
                if (!newName) return;
                await fetch(`http://localhost:8080/profiles/${profile.id}`, {
                    method: "PUT",
                    headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
                    body: JSON.stringify({ name: newName })
                });
                renderProfiles();
            });

            card.querySelector(".delete-btn").addEventListener("click", async (e) => {
                e.stopPropagation();
                if (!confirm(`Delete "${profile.name}"?`)) return;
                await fetch(`http://localhost:8080/profiles/${profile.id}`, {
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

renderProfiles();
