const API_BASE_URL = "http://localhost:8080";

const continueRow = document.getElementById("continueRow");
const recommendedRow = document.getElementById("recommendedRow");
const popularRow = document.getElementById("popularRow");
const activeProfileText = document.getElementById("activeProfile");
const logoutBtn = document.getElementById("logoutBtn");

// 1. Get Auth Data - Using "activeProfileName" to match main's logic
const activeProfile = localStorage.getItem("activeProfileName") || "Guest";
const token = localStorage.getItem("token"); 

activeProfileText.textContent = `Profile: ${activeProfile}`;

// 2. Main function to load real data from your API
async function loadTitles() {
    // AUTH GUARD: Redirect to login if no token found
    if (!token) {
        window.location.href = "index.html"; 
        return;
    }

    try {
        const res = await fetch(`${API_BASE_URL}/api/titles`, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Accept": "application/json"
            }
        });

        if (!res.ok) {
            console.error("Failed to load titles");
            return;
        }

        const titles = await res.json();

        // Split titles into 3 sections (Logic from main)
        const continueWatching = titles.slice(0, 8);
        const recommended = titles.slice(8, 16);
        const popular = titles.slice(16, 24);

        // Clear rows and create cards
        continueRow.innerHTML = "";
        recommendedRow.innerHTML = "";
        popularRow.innerHTML = "";

        createCards(continueRow, continueWatching);
        createCards(recommendedRow, recommended);
        createCards(popularRow, popular);

    } catch (err) {
        console.error("API Error:", err);
    }
}

function createCards(row, items) {
    items.forEach(item => {
        const card = document.createElement("div");
        card.classList.add("card");
        
        card.innerHTML = `
            <img src="${item.imageUrl || 'assets/placeholder.jpg'}" alt="${item.name}">
            <p class="card-title">${item.name}</p>
        `;

        card.addEventListener("click", () => {
            alert(`Now streaming: ${item.name}`);
        });

        row.appendChild(card);
    });
}

function scrollRow(rowId, direction) {
    const row = document.getElementById(rowId);
    const scrollAmount = 300;
    row.scrollBy({
        left: scrollAmount * direction,
        behavior: "smooth"
    });
    setTimeout(() => updateArrows(rowId), 100);
}

function updateArrows(rowId) {
    const row = document.getElementById(rowId);
    const wrapper = row.parentElement;
    const leftArrow = wrapper.querySelector(".arrow.left");

    if (leftArrow) {
        leftArrow.style.display = row.scrollLeft === 0 ? "none" : "flex";
    }
}

// 3. Cleanup Logout logic
logoutBtn.addEventListener("click", () => {
    localStorage.clear(); 
    window.location.href = "index.html";
});

// 4. INITIALIZE
loadTitles();

[continueRow, recommendedRow, popularRow].forEach(row => {
    row.addEventListener("scroll", () => updateArrows(row.id));
    updateArrows(row.id);
});