const continueRow = document.getElementById("continueRow");
const recommendedRow = document.getElementById("recommendedRow");
const popularRow = document.getElementById("popularRow");
const activeProfileText = document.getElementById("activeProfile");
const logoutBtn = document.getElementById("logoutBtn");

const activeProfile = localStorage.getItem("activeProfile") || "Guest";
const token = localStorage.getItem("token"); 
activeProfileText.textContent = `Profile: ${activeProfile}`;

// Main function to load real data from your API
async function loadHomeData() {
    if (!token) {
        window.location.href = "index.html"; // Redirect if not logged in
        return;
    }

    // Example calls to your actual API endpoints
    fetchRowData("/api/titles", continueRow, recommendedRow, popularRow);
}

async function fetchRowData(endpoint, rowElement) {
    try {
        const response = await fetch(`http://localhost:8080${endpoint}`, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Accept": "application/json"
            }
        });

        if (response.ok) {
            const titles = await response.json();
            // Clear the "loading" or old content
            rowElement.innerHTML = "";
            // titles is likely a list of objects, so we pass the whole object
            createCards(rowElement, titles);
        }
    } catch (err) {
        console.error("Failed to fetch row data:", err);
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

function scrollRow(rowId, direction) 
{
    const row = document.getElementById(rowId);
    const scrollAmount = 300;
    row.scrollBy(
    {
        left: scrollAmount * direction,
        behavior: "smooth"
    });

    setTimeout(() => updateArrows(rowId), 100);
}

function updateArrows(rowId) 
{
    const row = document.getElementById(rowId);
    const wrapper = row.parentElement;
    const leftArrow = wrapper.querySelector(".arrow.left");

    if (row.scrollLeft === 0)
    {
        leftArrow.style.display = "none";
    } 
    else    
    {
        leftArrow.style.display = "flex";
    }
}

logoutBtn.addEventListener("click", () => {
    localStorage.clear(); 
    window.location.href = "index.html";
});

// INITIALIZE
loadHomeData();

[continueRow, recommendedRow, popularRow].forEach(row => {
    row.addEventListener("scroll", () => updateArrows(row.id));
    updateArrows(row.id);
});