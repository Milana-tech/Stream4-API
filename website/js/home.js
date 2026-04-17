const API_BASE_URL = "http://localhost:8080/api";

const continueRow = document.getElementById("continueRow");
const recommendedRow = document.getElementById("recommendedRow");
const popularRow = document.getElementById("popularRow");
const activeProfileText = document.getElementById("activeProfile");

const activeProfile = localStorage.getItem("activeProfile") || "Guest";
activeProfileText.textContent = `Profile: ${activeProfile}`;

async function loadTitles() {
    const token = localStorage.getItem("token");

    const res = await fetch(`${API_BASE_URL}/titles`, {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if (!res.ok) {
        console.error("Failed to load titles");
        return;
    }

    const titles = await res.json();

    // Split titles into 3 sections
    const continueWatching = titles.slice(0, 8);
    const recommended = titles.slice(8, 16);
    const popular = titles.slice(16, 24);

    createCards(continueRow, continueWatching);
    createCards(recommendedRow, recommended);
    createCards(popularRow, popular);
}

const logoutBtn = document.getElementById("logoutBtn");

function createCards(row, titles) {
    titles.forEach(title => {
        const card = document.createElement("div");
        card.classList.add("card");
        card.innerHTML = `<p class="card-title">${title.name}</p>`;

        card.addEventListener("click", () => {
            alert(`Clicked: ${title.name}`);
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

logoutBtn.addEventListener("click", () => {
    localStorage.removeItem("activeProfile");
    window.location.href = "index.html";
});

function updateArrows(rowId) {
    const row = document.getElementById(rowId);
    const wrapper = row.parentElement;
    const leftArrow = wrapper.querySelector(".arrow.left");

    if (row.scrollLeft === 0) {
        leftArrow.style.display = "none";
    } else {
        leftArrow.style.display = "flex";
    }
}

[continueRow, recommendedRow, popularRow].forEach(row => {
    row.addEventListener("scroll", () => {
        updateArrows(row.id);
    });

    updateArrows(row.id);
});

// Load titles from API
loadTitles();
