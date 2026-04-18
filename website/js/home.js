const API_BASE_URL = "http://localhost:8080";

const continueRow   = document.getElementById("continueRow");
const recommendedRow = document.getElementById("recommendedRow");
const popularRow    = document.getElementById("popularRow");
const watchlistRow  = document.getElementById("watchlistRow");
const titleModal    = document.getElementById("titleModal");
const playerModal   = document.getElementById("playerModal");

const token      = localStorage.getItem("token");
const profileId  = localStorage.getItem("activeProfileId");
const activeProfile = localStorage.getItem("activeProfile") || "Guest";
document.getElementById("activeProfile").textContent = `Profile: ${activeProfile}`;

let selectedQuality  = null;
let selectedEpisodeId = null;
let currentTitle     = null;
let watchlistMap     = {}; // titleId -> watchlistItemId

// ── INIT ──────────────────────────────────────────────────────────────────────

async function init() {
    if (!token) { window.location.href = "index.html"; return; }
    await Promise.all([loadTitles(), loadContinueWatching(), loadWatchlist()]);
}

// ── TITLES ────────────────────────────────────────────────────────────────────

async function loadTitles() {
    try {
        const url = profileId
            ? `${API_BASE_URL}/titles/for-profile/${profileId}`
            : `${API_BASE_URL}/titles`;
        const res = await fetch(url, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (!res.ok) {
            // Fall back to unfiltered titles if profile endpoint fails
            const fallback = await fetch(`${API_BASE_URL}/titles`, {
                headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
            });
            if (!fallback.ok) return;
            const titles = await fallback.json();
            createCards(recommendedRow, titles.filter(t => t.type === "MOVIE"));
            createCards(popularRow, titles.filter(t => t.type === "SERIES"));
            return;
        }
        const titles = await res.json();
        createCards(recommendedRow, titles.filter(t => t.type === "MOVIE"));
        createCards(popularRow, titles.filter(t => t.type === "SERIES"));
    } catch (e) { console.error(e); }
}

// ── CONTINUE WATCHING ─────────────────────────────────────────────────────────

async function loadContinueWatching() {
    try {
        const res = await fetch(`${API_BASE_URL}/viewing-behaviour/history?profileId=${profileId}`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (res.ok) {
            const history = await res.json();
            const unfinished = history.filter(e => !e.finished);
            continueRow.innerHTML = "";
            if (unfinished.length === 0) {
                document.getElementById("continueSection").style.display = "none";
            } else {
                document.getElementById("continueSection").style.display = "block";
                unfinished.forEach(e => createHistoryCard(continueRow, e));
            }
        }
    } catch (e) { console.error(e); }
}

function createHistoryCard(row, entry) {
    const card = document.createElement("div");
    card.classList.add("card");
    card.innerHTML = `
        <div class="card-badge">RESUME</div>
        <div class="card-info">
            <p class="card-title">${entry.titleName || entry.titleId}</p>
            <p class="card-meta">${formatSeconds(entry.progressSeconds)} watched</p>
        </div>
    `;
    card.addEventListener("click", async () => {
        const res = await fetch(`${API_BASE_URL}/titles/${entry.titleId}`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (res.ok) openTitleModal(await res.json());
    });
    row.appendChild(card);
}

function formatSeconds(s) {
    if (!s) return "0m";
    const h = Math.floor(s / 3600);
    const m = Math.floor((s % 3600) / 60);
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
}

// ── WATCHLIST ─────────────────────────────────────────────────────────────────

async function loadWatchlist() {
    try {
        const res = await fetch(`${API_BASE_URL}/watchlists?profileId=${profileId}`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (res.ok) {
            const items = await res.json();
            watchlistMap = {};
            watchlistRow.innerHTML = "";
            document.getElementById("watchlistSection").style.display = "block";
            if (items.length === 0) {
                watchlistRow.innerHTML = "<p class='empty-row'>No titles saved yet. Click + Watchlist on any title.</p>";
            } else {
                items.forEach(item => {
                    watchlistMap[item.titleId] = item.id;
                    createWatchlistCard(watchlistRow, item);
                });
            }
        }
    } catch (e) { console.error(e); }
}

function createWatchlistCard(row, item) {
    const card = document.createElement("div");
    card.classList.add("card");
    card.innerHTML = `
        <div class="card-badge">SAVED</div>
        <div class="card-info">
            <p class="card-title">${item.titleName || item.titleId}</p>
        </div>
    `;
    card.addEventListener("click", async () => {
        const res = await fetch(`${API_BASE_URL}/titles/${item.titleId}`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (res.ok) openTitleModal(await res.json());
    });
    row.appendChild(card);
}

// ── CARDS ─────────────────────────────────────────────────────────────────────

const genreIcons = {
    DRAMA: "🎭", ACTION: "💥", ANIMATION: "🎨", HORROR: "👻",
    COMEDY: "😂", THRILLER: "🔪", ROMANCE: "❤️", DOCUMENTARY: "🎬",
    SCIENCE_FICTION: "🚀", FANTASY: "🧙", CRIME: "🕵️", MYSTERY: "🔍"
};

function createCards(row, items) {
    items.forEach(item => {
        const icon = genreIcons[item.genre] || "🎬";
        const card = document.createElement("div");
        card.classList.add("card");
        card.innerHTML = `
            <div class="card-icon">${icon}</div>
            <div class="card-badge">${item.type}</div>
            <div class="card-info">
                <p class="card-title">${item.name}</p>
                <p class="card-meta">${item.genre} · ${item.maturityRating} · ${item.releaseYear}</p>
            </div>
        `;
        card.addEventListener("click", () => openTitleModal(item));
        row.appendChild(card);
    });
}

// ── TITLE MODAL ───────────────────────────────────────────────────────────────

async function openTitleModal(title) {
    currentTitle     = title;
    selectedQuality  = null;
    selectedEpisodeId = null;

    const icon = genreIcons[title.genre] || "🎬";
    document.getElementById("modalIcon").textContent = icon;
    document.getElementById("modalTitle").textContent = title.name;
    document.getElementById("modalMeta").textContent =
        `${title.type} · ${title.genre} · ${title.maturityRating} · ${title.releaseYear}`;
    document.getElementById("modalDesc").textContent = title.description || "";

    // Quality buttons — first one selected by default
    const qualityButtons = document.getElementById("qualityButtons");
    qualityButtons.innerHTML = "";
    const qualities = title.supportedQualities || [];
    qualities.forEach((q, index) => {
        const btn = document.createElement("button");
        btn.className = "quality-btn";
        btn.textContent = q;
        if (index === 0) {
            btn.classList.add("selected");
            selectedQuality = q;
        }
        btn.addEventListener("click", () => {
            document.querySelectorAll(".quality-btn").forEach(b => b.classList.remove("selected"));
            btn.classList.add("selected");
            selectedQuality = q;
        });
        qualityButtons.appendChild(btn);
    });

    // Watchlist button
    const wlBtn = document.getElementById("watchlistBtn");
    if (watchlistMap[title.id]) {
        wlBtn.textContent = "✓ In Watchlist";
        wlBtn.classList.add("active");
    } else {
        wlBtn.textContent = "+ Watchlist";
        wlBtn.classList.remove("active");
    }

    // Series: load seasons
    const seriesSection = document.getElementById("seriesSection");
    if (title.type === "SERIES") {
        seriesSection.style.display = "block";
        await loadSeasons(title.id);
    } else {
        seriesSection.style.display = "none";
    }

    titleModal.style.display = "flex";
}

async function loadSeasons(titleId) {
    const seasonSelect = document.getElementById("seasonSelect");
    seasonSelect.innerHTML = "<option>Loading...</option>";
    document.getElementById("episodesList").innerHTML = "";

    try {
        const res = await fetch(`${API_BASE_URL}/titles/${titleId}/seasons`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (res.ok) {
            const seasons = await res.json();
            seasonSelect.innerHTML = "";
            seasons.forEach(s => {
                const opt = document.createElement("option");
                opt.value = s.id;
                opt.textContent = `Season ${s.seasonNumber}`;
                seasonSelect.appendChild(opt);
            });
            if (seasons.length > 0) await loadEpisodes(seasons[0].id);
            seasonSelect.onchange = () => loadEpisodes(seasonSelect.value);
        }
    } catch (e) {
        seasonSelect.innerHTML = "<option>Could not load seasons</option>";
    }
}

async function loadEpisodes(seasonId) {
    const episodesList = document.getElementById("episodesList");
    episodesList.innerHTML = "<p class='modal-label'>Loading episodes...</p>";
    selectedEpisodeId = null;

    try {
        const res = await fetch(`${API_BASE_URL}/seasons/${seasonId}/episodes`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (res.ok) {
            const episodes = await res.json();
            episodesList.innerHTML = "";
            episodes.forEach(ep => {
                const row = document.createElement("div");
                row.className = "episode-row";
                row.innerHTML = `<span class="ep-number">E${ep.episodeNumber}</span><span class="ep-name">${ep.name}</span>`;
                row.addEventListener("click", () => {
                    document.querySelectorAll(".episode-row").forEach(r => r.classList.remove("selected"));
                    row.classList.add("selected");
                    selectedEpisodeId = ep.id;
                });
                episodesList.appendChild(row);
            });
        }
    } catch (e) {
        episodesList.innerHTML = "<p class='modal-label'>Could not load episodes.</p>";
    }
}

document.getElementById("closeTitleModal").addEventListener("click", () => {
    titleModal.style.display = "none";
});

// Watchlist toggle
document.getElementById("watchlistBtn").addEventListener("click", async () => {
    if (!currentTitle) return;
    const wlBtn = document.getElementById("watchlistBtn");

    if (watchlistMap[currentTitle.id]) {
        // Remove from watchlist
        await fetch(`${API_BASE_URL}/watchlists/${watchlistMap[currentTitle.id]}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });
        delete watchlistMap[currentTitle.id];
        wlBtn.textContent = "+ Watchlist";
        wlBtn.classList.remove("active");
    } else {
        // Add to watchlist
        const res = await fetch(`${API_BASE_URL}/watchlists`, {
            method: "POST",
            headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({ titleId: currentTitle.id, profileId })
        });
        if (res.ok) {
            const item = await res.json();
            watchlistMap[currentTitle.id] = item.id;
            wlBtn.textContent = "✓ In Watchlist";
            wlBtn.classList.add("active");
        }
    }
    await loadWatchlist();
});

// Play button → open player modal
document.getElementById("playBtn").addEventListener("click", () => {
    if (!selectedQuality) { alert("Please select a quality first."); return; }
    document.getElementById("playerTitle").textContent =
        `${currentTitle.name}${selectedEpisodeId ? " · Episode selected" : ""} · ${selectedQuality}`;
    document.getElementById("progressSlider").value = 0;
    document.getElementById("progressLabel").textContent = "0%";
    document.getElementById("finishedCheck").checked = false;
    titleModal.style.display = "none";
    playerModal.style.display = "flex";
});

// Player progress slider
document.getElementById("progressSlider").addEventListener("input", (e) => {
    const val = e.target.value;
    document.getElementById("progressLabel").textContent = `${val}%`;
    if (val == 100) document.getElementById("finishedCheck").checked = true;
});

document.getElementById("cancelPlayerBtn").addEventListener("click", () => {
    playerModal.style.display = "none";
});

// Save watch event
document.getElementById("saveWatchBtn").addEventListener("click", async () => {
    const progress = parseInt(document.getElementById("progressSlider").value);
    const finished = document.getElementById("finishedCheck").checked;
    const progressSeconds = Math.round((progress / 100) * (currentTitle.durationSeconds || 3600));

    try {
        await fetch(`${API_BASE_URL}/viewing-behaviour/watch`, {
            method: "POST",
            headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({
                titleId: currentTitle.id,
                episodeId: selectedEpisodeId || null,
                profileId,
                progressSeconds,
                finished,
                autoContinued: false
            })
        });

        playerModal.style.display = "none";
        alert(finished ? "Marked as watched!" : "Progress saved!");
        await Promise.all([loadContinueWatching(), loadWatchlist()]);
    } catch (e) {
        alert("Could not save progress.");
    }
});

// ── ACCOUNT MODAL ─────────────────────────────────────────────────────────────

let selectedPlan = null;

document.getElementById("accountBtn").addEventListener("click", async () => {
    await loadSubscriptionStatus();
    document.getElementById("accountModal").style.display = "flex";
});

document.getElementById("closeAccountModal").addEventListener("click", () => {
    document.getElementById("accountModal").style.display = "none";
});

async function loadSubscriptionStatus() {
    const statusBox = document.getElementById("subscriptionStatus");
    const subscribeSection = document.getElementById("subscribeSection");
    const cancelSection = document.getElementById("cancelSection");
    statusBox.textContent = "Loading...";
    selectedPlan = null;
    document.querySelectorAll(".plan-card").forEach(c => c.classList.remove("selected"));

    try {
        // Check active subscription
        const subRes = await fetch(`${API_BASE_URL}/subscriptions/current`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });

        if (subRes.ok) {
            const sub = await subRes.json();
            const hasDiscount = sub.discountPercentage && sub.discountPercentage > 0;
            const priceDisplay = hasDiscount
                ? `€${sub.discountedPrice}/mo <s style="opacity:0.5">€${sub.totalPrice}</s> (${sub.discountPercentage}% off until ${sub.discountEndDate})`
                : `€${sub.totalPrice}/mo`;
            statusBox.innerHTML = `<span class="status-active">✓ ${sub.plan} – ${priceDisplay}</span>`;
            subscribeSection.style.display = "none";
            cancelSection.style.display = "block";
            return;
        }

        // Check trial
        const trialRes = await fetch(`${API_BASE_URL}/trials`, {
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });

        if (trialRes.ok) {
            const trial = await trialRes.json();
            if (trial.status === "ACTIVE") {
                statusBox.innerHTML = `<span class="status-trial">🎁 Free trial active — ends ${trial.endDate} (${trial.daysRemaining} days left)</span>`;
            } else {
                statusBox.innerHTML = `<span class="status-none">No active subscription</span>`;
            }
        } else {
            statusBox.innerHTML = `<span class="status-none">No active subscription</span>`;
        }

        subscribeSection.style.display = "block";
        cancelSection.style.display = "none";

    } catch (e) {
        statusBox.textContent = "Could not load status.";
    }
}

// Plan selection
document.querySelectorAll(".plan-card").forEach(card => {
    card.addEventListener("click", () => {
        document.querySelectorAll(".plan-card").forEach(c => c.classList.remove("selected"));
        card.classList.add("selected");
        selectedPlan = card.dataset.plan;
    });
});

document.getElementById("subscribeBtn").addEventListener("click", async () => {
    if (!selectedPlan) { alert("Please select a plan."); return; }
    try {
        const res = await fetch(`${API_BASE_URL}/subscriptions`, {
            method: "POST",
            headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({ plan: selectedPlan })
        });
        if (res.ok) {
            alert(`Subscribed to ${selectedPlan}!`);
            await loadSubscriptionStatus();
        } else {
            const err = await res.json().catch(() => null);
            alert(err?.message || "Could not subscribe.");
        }
    } catch (e) { alert("Could not reach the server."); }
});

document.getElementById("cancelSubBtn").addEventListener("click", async () => {
    if (!confirm("Are you sure you want to cancel your subscription?")) return;
    try {
        const res = await fetch(`${API_BASE_URL}/subscriptions/cancel`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}`, "Accept": "application/json" }
        });
        if (res.ok) {
            alert("Subscription cancelled.");
            await loadSubscriptionStatus();
        } else {
            const err = await res.json().catch(() => null);
            alert(err?.message || "Could not cancel.");
        }
    } catch (e) { alert("Could not reach the server."); }
});

document.getElementById("sendInviteBtn").addEventListener("click", async () => {
    const email = document.getElementById("inviteEmail").value.trim();
    if (!email) { alert("Please enter an email address."); return; }
    try {
        const res = await fetch(`${API_BASE_URL}/invitations`, {
            method: "POST",
            headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json", "Accept": "application/json" },
            body: JSON.stringify({ inviteeEmail: email })
        });
        if (res.ok) {
            alert(`Invitation sent to ${email}!`);
            document.getElementById("inviteEmail").value = "";
        } else {
            const err = await res.json().catch(() => null);
            alert(err?.message || "Could not send invitation.");
        }
    } catch (e) { alert("Could not reach the server."); }
});

// ── SCROLL ────────────────────────────────────────────────────────────────────

function scrollRow(rowId, direction) {
    const row = document.getElementById(rowId);
    row.scrollBy({ left: 300 * direction, behavior: "smooth" });
    setTimeout(() => updateArrows(rowId), 100);
}

function updateArrows(rowId) {
    const row = document.getElementById(rowId);
    const leftArrow = row.parentElement.querySelector(".arrow.left");
    if (leftArrow) leftArrow.style.display = row.scrollLeft === 0 ? "none" : "flex";
}

document.getElementById("switchProfileBtn").addEventListener("click", () => {
    localStorage.removeItem("activeProfileId");
    localStorage.removeItem("activeProfile");
    window.location.href = "profiles.html";
});

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

[continueRow, recommendedRow, popularRow, watchlistRow].forEach(row => {
    row.addEventListener("scroll", () => updateArrows(row.id));
    updateArrows(row.id);
});

init();
