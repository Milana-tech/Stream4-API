const profilesGrid = document.getElementById("profilesGrid");
const addProfileBtn = document.getElementById("addProfileBtn");

let profiles = [
  { name: "A" }
];

function renderProfiles() 
{
  profilesGrid.innerHTML = "";

  profiles.forEach((profile, index) => {
    const card = document.createElement("div");
    card.classList.add("profile-card");

    card.innerHTML = `
      <div class="avatar">${profile.name.charAt(0)}</div>
      <p class="profile-name">${profile.name}</p>
    `;

    card.addEventListener("click", () => {
      localStorage.setItem("activeProfile", profile.name);
      window.location.href = "home.html";
    });

    profilesGrid.appendChild(card);
  });
}

addProfileBtn.addEventListener("click", () => {
  const name = prompt("Enter profile name:");
  if (!name) return;

  profiles.push({ name });
  renderProfiles();
});

renderProfiles();
