const continueRow = document.getElementById("continueRow");
const recommendedRow = document.getElementById("recommendedRow");
const popularRow = document.getElementById("popularRow");
const activeProfileText = document.getElementById("activeProfile");

const activeProfile = localStorage.getItem("activeProfile") || "Guest";
activeProfileText.textContent = `Profile: ${activeProfile}`;

const continueWatching = ["Film 1", "Film 2", "Film 3","Film 4","Film 5","Film 6","Film 7","Film 8"];
const recommended = ["Film 1","Film 2","Film 3","Film 4", "Film 5", "Film 6","Film 7","Film 8"];
const popular = ["Film 1","Film 2","Film 3","Film 4", "Film 5", "Film 6","Film 7","Film 8"];

const logoutBtn = document.getElementById("logoutBtn");

function createCards(row, titles) 
{
    titles.forEach(title => {
        const card = document.createElement("div");
        card.classList.add("card");
        card.innerHTML = `<p class="card-title">${title}</p>`;

        card.addEventListener("click", () => {
        alert(`Clicked: ${title}`);
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

logoutBtn.addEventListener("click", () => {
    localStorage.removeItem("activeProfile");
    window.location.href = "index.html";
});

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

[continueRow, recommendedRow, popularRow].forEach(row => {
    row.addEventListener("scroll", () => {
        updateArrows(row.id);
    });

    updateArrows(row.id);
});

createCards(continueRow, continueWatching);
createCards(recommendedRow, recommended);
createCards(popularRow, popular);
