const API_BASE_URL = "http://localhost:8080";

const continueRow = document.getElementById("continueRow");
const recommendedRow = document.getElementById("recommendedRow");
const popularRow = document.getElementById("popularRow");
const activeProfileText = document.getElementById("activeProfile");
const logoutBtn = document.getElementById("logoutBtn");

const activeProfile = localStorage.getItem("activeProfile") || "Guest";
const token = localStorage.getItem("token");
activeProfileText.textContent = `Profile: ${activeProfile}`;

async function loadHomeData() {
    if (!token) {
        window.location.href = "index.html";
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/titles`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });

        if (response.ok) {
            const titles = await response.json();
            createCards(continueRow, titles.slice(0, 8));
            createCards(recommendedRow, titles.slice(8, 16));
            createCards(popularRow, titles.slice(16, 24));
        }
    } catch (err) {
        console.error("Failed to fetch titles:", err);
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
        card.addEventListener("click", () => alert(`Now streaming: ${item.name}`));
        row.appendChild(card);
    });
}

function scrollRow(rowId, direction) {
    const row = document.getElementById(rowId);
    row.scrollBy({ left: 300 * direction, behavior: "smooth" });
    setTimeout(() => updateArrows(rowId), 100);
}

function updateArrows(rowId) {
    const row = document.getElementById(rowId);
    const leftArrow = row.parentElement.querySelector(".arrow.left");
    leftArrow.style.display = row.scrollLeft === 0 ? "none" : "flex";
}

logoutBtn.addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

loadHomeData();

[continueRow, recommendedRow, popularRow].forEach(row => {
    row.addEventListener("scroll", () => updateArrows(row.id));
    updateArrows(row.id);
});
