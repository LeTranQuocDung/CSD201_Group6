/* ============================================================
   MediCare Hospital Management System — script.js
   Frontend Logic: Navigation, CRUD, localStorage, UI
   ============================================================ */

"use strict";

/* ============================================================
   1. CONSTANTS & STATE
   ============================================================ */

const STORAGE_KEY = "medicare_patients";

// App state
let state = {
  patients: [],          // all patient records
  currentCallId: null,   // ID of patient being called right now
  deleteTargetId: null,  // ID pending deletion confirmation
  darkMode: false,
};

/* ============================================================
   2. UTILITY HELPERS
   ============================================================ */

/**
 * Generate a random patient ID like "BN-482931"
 */
function generatePatientId() {
  const num = Math.floor(100000 + Math.random() * 900000);
  return `BN-${num}`;
}

/**
 * Format a Date object → "HH:MM DD/MM/YYYY"
 */
function formatDateTime(isoStr) {
  const d = new Date(isoStr);
  const hh = String(d.getHours()).padStart(2, "0");
  const mm = String(d.getMinutes()).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  const mo = String(d.getMonth() + 1).padStart(2, "0");
  const yy = d.getFullYear();
  return `${hh}:${mm} ${dd}/${mo}/${yy}`;
}

/**
 * Get first letter of a name for avatars
 */
function initials(name) {
  const parts = name.trim().split(" ");
  return parts[parts.length - 1].charAt(0).toUpperCase();
}

/**
 * Debounce helper
 */
function debounce(fn, delay) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

/* ============================================================
   3. LOCALSTORAGE — LOAD / SAVE
   ============================================================ */

function loadFromStorage() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    state.patients = raw ? JSON.parse(raw) : [];
  } catch {
    state.patients = [];
  }
}

function saveToStorage() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state.patients));
}

/* ============================================================
   4. COMPUTED STATS
   ============================================================ */

function getStats() {
  const total     = state.patients.length;
  const waiting   = state.patients.filter(p => p.status === "Chờ khám").length;
  const examining = state.patients.filter(p => p.status === "Đang khám").length;
  const done      = state.patients.filter(p => p.status === "Đã khám").length;
  return { total, waiting, examining, done };
}

/* ============================================================
   5. NAVIGATION
   ============================================================ */

const SECTION_TITLES = {
  dashboard : "Dashboard",
  register  : "Đăng Ký Khám",
  calling   : "Gọi Khám",
  search    : "Tìm Kiếm",
  history   : "Lịch Sử Khám",
};

function navigateTo(sectionId) {
  // Hide all sections
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".nav-item").forEach(n => n.classList.remove("active"));

  // Show target section
  document.getElementById(`section-${sectionId}`).classList.add("active");
  document.querySelector(`.nav-item[data-section="${sectionId}"]`).classList.add("active");

  // Update topbar title
  document.getElementById("topbarTitle").textContent = SECTION_TITLES[sectionId] || "";

  // Refresh dynamic content per section
  if (sectionId === "dashboard")  renderDashboard();
  if (sectionId === "calling")    renderCallingSection();
  if (sectionId === "history")    renderHistory();

  // Close mobile sidebar
  closeSidebar();
}

/* ============================================================
   6. SIDEBAR MOBILE TOGGLE
   ============================================================ */

function openSidebar() {
  document.getElementById("sidebar").classList.add("open");
  document.getElementById("sidebarOverlay").classList.add("open");
}

function closeSidebar() {
  document.getElementById("sidebar").classList.remove("open");
  document.getElementById("sidebarOverlay").classList.remove("open");
}

/* ============================================================
   7. DARK MODE
   ============================================================ */

function applyDarkMode(isDark) {
  state.darkMode = isDark;
  document.documentElement.setAttribute("data-theme", isDark ? "dark" : "light");
  document.getElementById("darkIcon").className  = isDark ? "ph ph-sun" : "ph ph-moon";
  document.getElementById("darkLabel").textContent = isDark ? "Light Mode" : "Dark Mode";
  localStorage.setItem("medicare_dark", isDark ? "1" : "0");
}

function toggleDarkMode() {
  applyDarkMode(!state.darkMode);
  if (document.getElementById("section-dashboard").classList.contains("active")) {
    drawPieChart(); // redraw chart with new theme colors
  }
}

/* ============================================================
   8. CLOCK
   ============================================================ */

function updateClock() {
  const now = new Date();
  const hh  = String(now.getHours()).padStart(2, "0");
  const mm  = String(now.getMinutes()).padStart(2, "0");
  const ss  = String(now.getSeconds()).padStart(2, "0");
  const dd  = String(now.getDate()).padStart(2, "0");
  const mo  = String(now.getMonth() + 1).padStart(2, "0");
  const yy  = now.getFullYear();
  document.getElementById("topbarTime").textContent = `${hh}:${mm}:${ss}  ${dd}/${mo}/${yy}`;
}

/* ============================================================
   9. DASHBOARD
   ============================================================ */

function renderDashboard() {
  const { total, waiting, examining, done } = getStats();

  // Animate counters
  animateCounter("stat-total",     total);
  animateCounter("stat-waiting",   waiting);
  animateCounter("stat-examining", examining);
  animateCounter("stat-done",      done);

  // Waiting badge on nav
  document.getElementById("waiting-badge").textContent = waiting;

  // Recent patients list (last 6 registered)
  const recent = [...state.patients].reverse().slice(0, 6);
  const recentList = document.getElementById("recentList");

  if (recent.length === 0) {
    recentList.innerHTML = `
      <div class="empty-state">
        <i class="ph ph-hospital"></i>
        <p>Chưa có bệnh nhân nào</p>
      </div>`;
  } else {
    recentList.innerHTML = recent.map(p => `
      <div class="recent-item">
        <div class="recent-avatar">${initials(p.name)}</div>
        <div class="recent-info">
          <div class="recent-name">${escHtml(p.name)}</div>
          <div class="recent-meta">${p.gender} · ${p.age} tuổi · ${escHtml(p.department)}</div>
        </div>
        <span class="recent-id">${p.id}</span>
      </div>`).join("");
  }

  // Pie chart
  drawPieChart();
}

/** Animate a number counter from 0 to target */
function animateCounter(elId, target) {
  const el = document.getElementById(elId);
  const start = parseInt(el.textContent) || 0;
  const diff  = target - start;
  const steps = 20;
  let step = 0;
  clearInterval(el._anim);
  el._anim = setInterval(() => {
    step++;
    el.textContent = Math.round(start + (diff * step) / steps);
    if (step >= steps) {
      clearInterval(el._anim);
      el.textContent = target;
    }
  }, 20);
}

/** Draw a simple pie/donut chart on canvas */
function drawPieChart() {
  const canvas = document.getElementById("statusChart");
  if (!canvas) return;
  const ctx = canvas.getContext("2d");
  const { waiting, examining, done } = getStats();

  const data   = [waiting, examining, done];
  const colors = ["#f39c12", "#1a6fc4", "#00b894"];
  const labels = ["Chờ khám", "Đang khám", "Đã khám"];
  const total  = data.reduce((a, b) => a + b, 0);

  const W = canvas.width;
  const H = canvas.height;
  ctx.clearRect(0, 0, W, H);

  if (total === 0) {
    // Draw empty circle
    ctx.beginPath();
    ctx.arc(W / 2, H / 2, W / 2 - 14, 0, Math.PI * 2);
    ctx.strokeStyle = getComputedStyle(document.documentElement)
      .getPropertyValue("--border").trim();
    ctx.lineWidth = 22;
    ctx.stroke();

    ctx.fillStyle = getComputedStyle(document.documentElement)
      .getPropertyValue("--text-muted").trim();
    ctx.font = "bold 13px DM Sans, sans-serif";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillText("Chưa có dữ liệu", W / 2, H / 2);
    document.getElementById("chartLegend").innerHTML = "";
    return;
  }

  let startAngle = -Math.PI / 2;
  const cx = W / 2, cy = H / 2;
  const radius = W / 2 - 14;
  const innerRadius = radius - 38;

  data.forEach((val, i) => {
    if (val === 0) return;
    const slice = (val / total) * Math.PI * 2;

    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.arc(cx, cy, radius, startAngle, startAngle + slice);
    ctx.closePath();
    ctx.fillStyle = colors[i];
    ctx.fill();
    startAngle += slice;
  });

  // Inner circle (donut hole)
  const isDark = state.darkMode;
  ctx.beginPath();
  ctx.arc(cx, cy, innerRadius, 0, Math.PI * 2);
  ctx.fillStyle = isDark ? "#162032" : "#ffffff";
  ctx.fill();

  // Center text
  ctx.fillStyle = isDark ? "#e8edf3" : "#1e2a3a";
  ctx.font = `bold 28px Syne, sans-serif`;
  ctx.textAlign = "center";
  ctx.textBaseline = "middle";
  ctx.fillText(total, cx, cy - 8);
  ctx.font = `12px DM Sans, sans-serif`;
  ctx.fillStyle = "#7f8c8d";
  ctx.fillText("bệnh nhân", cx, cy + 14);

  // Legend
  document.getElementById("chartLegend").innerHTML = data.map((val, i) => `
    <div class="legend-item">
      <div class="legend-dot" style="background:${colors[i]}"></div>
      <span class="legend-label">${labels[i]}</span>
      <span class="legend-count">${val}</span>
    </div>`).join("");
}

/* ============================================================
   10. REGISTER PATIENT
   ============================================================ */

function validateRegisterForm() {
  let valid = true;

  const fields = [
    { id: "fullName",  errId: "err-fullName",  rule: v => v.trim().length >= 2,     msg: "Họ tên tối thiểu 2 ký tự" },
    { id: "age",       errId: "err-age",        rule: v => v > 0 && v <= 150,        msg: "Tuổi phải từ 1–150" },
    { id: "phone",     errId: "err-phone",      rule: v => /^[0-9]{9,11}$/.test(v.replace(/\s/g, "")), msg: "Số điện thoại không hợp lệ" },
    { id: "symptoms",  errId: "err-symptoms",   rule: v => v.trim().length >= 5,     msg: "Mô tả triệu chứng tối thiểu 5 ký tự" },
  ];

  fields.forEach(({ id, errId, rule, msg }) => {
    const el  = document.getElementById(id);
    const err = document.getElementById(errId);
    if (!rule(el.value)) {
      el.classList.add("error");
      err.textContent = msg;
      valid = false;
    } else {
      el.classList.remove("error");
      err.textContent = "";
    }
  });

  // Gender radio
  const genderErr = document.getElementById("err-gender");
  const gender = document.querySelector('input[name="gender"]:checked');
  if (!gender) {
    genderErr.textContent = "Vui lòng chọn giới tính";
    valid = false;
  } else {
    genderErr.textContent = "";
  }

  return valid;
}

function handleRegister(e) {
  e.preventDefault();
  if (!validateRegisterForm()) return;

  const patient = {
    id         : generatePatientId(),
    name       : document.getElementById("fullName").value.trim(),
    age        : parseInt(document.getElementById("age").value),
    gender     : document.querySelector('input[name="gender"]:checked').value,
    phone      : document.getElementById("phone").value.trim(),
    symptoms   : document.getElementById("symptoms").value.trim(),
    department : document.getElementById("department").value,
    status     : "Chờ khám",
    registeredAt: new Date().toISOString(),
  };

  state.patients.push(patient);
  saveToStorage();

  // Reset form
  document.getElementById("registerForm").reset();
  document.querySelectorAll(".field-error").forEach(el => el.textContent = "");
  document.querySelectorAll("input.error, textarea.error").forEach(el => el.classList.remove("error"));

  // Show success modal
  document.getElementById("newPatientId").textContent = patient.id;
  document.getElementById("successMsg").textContent =
    `${patient.name} — ${patient.department} — Vui lòng ghi nhớ mã để tra cứu.`;
  openModal("successModal");

  // Update badge
  document.getElementById("waiting-badge").textContent =
    state.patients.filter(p => p.status === "Chờ khám").length;
}

/* ============================================================
   11. CALLING SECTION
   ============================================================ */

function renderCallingSection() {
  const waitingPatients = state.patients.filter(p => p.status === "Chờ khám");
  const examiningList   = state.patients.filter(p => p.status === "Đang khám");

  // Count badge
  document.getElementById("waitingCount").textContent =
    `${waitingPatients.length} bệnh nhân`;

  // Currently calling display
  const callingDisplay = document.getElementById("callingDisplay");
  if (examiningList.length > 0) {
    const cur = examiningList[examiningList.length - 1]; // latest one being examined
    callingDisplay.className = "calling-display calling-active";
    callingDisplay.innerHTML = `
      <div class="calling-active-inner">
        <div class="call-avatar"><i class="ph-fill ph-stethoscope"></i></div>
        <div class="call-details">
          <h2>${escHtml(cur.name)}</h2>
          <div class="call-id-tag">${cur.id}</div>
          <div class="call-dept"><i class="ph ph-buildings"></i> ${escHtml(cur.department)}</div>
        </div>
        <div style="margin-left:auto;display:flex;gap:8px;flex-wrap:wrap;">
          <button class="btn-done" onclick="markDone('${cur.id}')">
            <i class="ph ph-check-circle"></i> Khám xong
          </button>
        </div>
      </div>`;
  } else {
    callingDisplay.className = "calling-display";
    callingDisplay.innerHTML = `
      <div class="calling-placeholder">
        <i class="ph ph-bell-simple-slash"></i>
        <p>Chưa có bệnh nhân nào đang được gọi</p>
      </div>`;
  }

  // Waiting list
  const waitingList = document.getElementById("waitingList");
  if (waitingPatients.length === 0) {
    waitingList.innerHTML = `
      <div class="empty-state">
        <i class="ph ph-smiley"></i>
        <p>Không có bệnh nhân trong hàng chờ</p>
      </div>`;
    return;
  }

  waitingList.innerHTML = waitingPatients.map((p, idx) => `
    <div class="waiting-item" id="wait-${p.id}">
      <div class="queue-number">${idx + 1}</div>
      <div class="waiting-info">
        <div class="waiting-name">${escHtml(p.name)}</div>
        <div class="waiting-meta">
          ${p.id} &nbsp;·&nbsp; ${escHtml(p.department)} &nbsp;·&nbsp; ${p.gender}, ${p.age} tuổi
        </div>
      </div>
      <button class="btn-call" onclick="callPatient('${p.id}')">
        <i class="ph ph-bell-ringing"></i> Gọi khám
      </button>
    </div>`).join("");
}

/**
 * Call a patient → show ringing modal → change status after 2.5s
 */
function callPatient(id) {
  const patient = state.patients.find(p => p.id === id);
  if (!patient || patient.status !== "Chờ khám") return;

  // Fill calling modal info
  document.getElementById("callingName").textContent = patient.name;
  document.getElementById("callingId").textContent   = patient.id;
  openModal("callingModal");

  // Show loading for 2.5 seconds then update status
  setTimeout(() => {
    closeModal("callingModal");
    patient.status = "Đang khám";
    state.currentCallId = id;
    saveToStorage();
    renderCallingSection();
    showToast(`Đã gọi ${patient.name} vào khám`, "success");
  }, 2500);
}

/**
 * Mark current patient as done
 */
function markDone(id) {
  const patient = state.patients.find(p => p.id === id);
  if (!patient) return;
  patient.status = "Đã khám";
  saveToStorage();
  renderCallingSection();
  showToast(`${patient.name} đã khám xong`, "success");
  // Update badge
  document.getElementById("waiting-badge").textContent =
    state.patients.filter(p => p.status === "Chờ khám").length;
}

/* ============================================================
   12. SEARCH
   ============================================================ */

function handleSearch() {
  const query = document.getElementById("searchInput").value.trim().toLowerCase();
  const resultEl = document.getElementById("searchResult");

  if (!query) {
    resultEl.innerHTML = "";
    return;
  }

  // Search by ID (exact, case-insensitive) OR name (partial)
  const found = state.patients.filter(p =>
    p.id.toLowerCase() === query ||
    p.name.toLowerCase().includes(query)
  );

  if (found.length === 0) {
    resultEl.innerHTML = `
      <div class="no-result">
        <i class="ph ph-magnifying-glass-minus"></i>
        <p>Không tìm thấy bệnh nhân với từ khóa "<strong>${escHtml(query)}</strong>"</p>
      </div>`;
    return;
  }

  resultEl.innerHTML = found.map(p => `
    <div class="search-result-card" style="margin-bottom:14px;">
      <div class="result-header">
        <div class="result-avatar">${initials(p.name)}</div>
        <div>
          <div style="font-family:'Syne',sans-serif;font-size:18px;font-weight:800;color:var(--text)">
            ${escHtml(p.name)}
          </div>
          <span class="result-id-tag">${p.id}</span>
        </div>
        <div style="margin-left:auto;">
          ${statusBadge(p.status)}
        </div>
      </div>
      <div class="result-grid">
        <div class="result-field">
          <label><i class="ph ph-cake"></i> Tuổi</label>
          <span>${p.age} tuổi</span>
        </div>
        <div class="result-field">
          <label><i class="ph ph-gender-intersex"></i> Giới tính</label>
          <span>${p.gender}</span>
        </div>
        <div class="result-field">
          <label><i class="ph ph-phone"></i> Điện thoại</label>
          <span>${escHtml(p.phone)}</span>
        </div>
        <div class="result-field">
          <label><i class="ph ph-buildings"></i> Khoa</label>
          <span>${escHtml(p.department)}</span>
        </div>
        <div class="result-field" style="grid-column:1/-1">
          <label><i class="ph ph-heartbeat"></i> Triệu chứng</label>
          <span>${escHtml(p.symptoms)}</span>
        </div>
        <div class="result-field" style="grid-column:1/-1">
          <label><i class="ph ph-clock"></i> Thời gian đăng ký</label>
          <span>${formatDateTime(p.registeredAt)}</span>
        </div>
      </div>
    </div>`).join("");
}

/* ============================================================
   13. HISTORY TABLE
   ============================================================ */

function renderHistory(filterStatus = "") {
  const filter = filterStatus ||
    document.getElementById("filterStatus").value || "";

  let list = [...state.patients].reverse();
  if (filter) list = list.filter(p => p.status === filter);

  const tbody = document.getElementById("historyBody");

  if (list.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="8" class="table-empty">
          <i class="ph ph-database"></i>
          <p>${filter ? "Không có bệnh nhân với trạng thái này" : "Chưa có dữ liệu"}</p>
        </td>
      </tr>`;
    return;
  }

  tbody.innerHTML = list.map(p => `
    <tr>
      <td><span class="patient-id-cell">${p.id}</span></td>
      <td><strong>${escHtml(p.name)}</strong></td>
      <td>${p.age}</td>
      <td>${escHtml(p.department)}</td>
      <td><span class="symptom-text" title="${escHtml(p.symptoms)}">${escHtml(p.symptoms)}</span></td>
      <td>${statusBadge(p.status)}</td>
      <td style="white-space:nowrap;font-size:12px;color:var(--text-muted)">${formatDateTime(p.registeredAt)}</td>
      <td>
        <button class="btn-delete" onclick="confirmDelete('${p.id}')">
          <i class="ph ph-trash"></i>
        </button>
      </td>
    </tr>`).join("");
}

function statusBadge(status) {
  const map = {
    "Chờ khám"  : "status-wait",
    "Đang khám" : "status-exam",
    "Đã khám"   : "status-done",
  };
  return `<span class="status-badge ${map[status] || ""}">${status}</span>`;
}

/* ============================================================
   14. DELETE
   ============================================================ */

function confirmDelete(id) {
  const p = state.patients.find(pt => pt.id === id);
  if (!p) return;
  state.deleteTargetId = id;
  document.getElementById("deleteMsg").textContent =
    `Bạn có chắc muốn xóa bệnh nhân "${p.name}" (${p.id}) không?`;
  openModal("deleteModal");
}

function executeDelete() {
  const id = state.deleteTargetId;
  if (!id) return;
  const name = state.patients.find(p => p.id === id)?.name || "";
  state.patients = state.patients.filter(p => p.id !== id);
  saveToStorage();
  closeModal("deleteModal");
  renderHistory();
  showToast(`Đã xóa bệnh nhân ${name}`, "warning");
  state.deleteTargetId = null;
  // Refresh stats badge
  document.getElementById("waiting-badge").textContent =
    state.patients.filter(p => p.status === "Chờ khám").length;
}

function clearAllPatients() {
  state.patients = [];
  saveToStorage();
  renderHistory();
  showToast("Đã xóa toàn bộ lịch sử", "warning");
  document.getElementById("waiting-badge").textContent = 0;
}

/* ============================================================
   15. MODALS
   ============================================================ */

function openModal(id) {
  document.getElementById(id).classList.add("open");
}

function closeModal(id) {
  document.getElementById(id).classList.remove("open");
}

// Close modal when clicking the backdrop
document.querySelectorAll(".modal-overlay").forEach(overlay => {
  overlay.addEventListener("click", e => {
    if (e.target === overlay) {
      overlay.classList.remove("open");
    }
  });
});

/* ============================================================
   16. TOAST NOTIFICATIONS
   ============================================================ */

function showToast(message, type = "info") {
  const iconMap = {
    success : "ph-check-circle",
    error   : "ph-x-circle",
    warning : "ph-warning",
    info    : "ph-info",
  };

  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <i class="ph-fill ${iconMap[type] || "ph-info"}"></i>
    <span class="toast-msg">${escHtml(message)}</span>`;

  const container = document.getElementById("toastContainer");
  container.appendChild(toast);

  // Auto remove after 3.5s with fade-out
  setTimeout(() => {
    toast.style.transition = "opacity .4s, transform .4s";
    toast.style.opacity    = "0";
    toast.style.transform  = "translateX(40px)";
    setTimeout(() => toast.remove(), 400);
  }, 3500);
}

/* ============================================================
   17. SECURITY: Escape HTML to prevent XSS
   ============================================================ */
function escHtml(str) {
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

/* ============================================================
   18. LOGOUT
   ============================================================ */

function handleLogout() {
  // Animate page fade then reload (simulates logout for SPA)
  document.body.style.transition = "opacity .4s";
  document.body.style.opacity    = "0";
  setTimeout(() => {
    // In a real app, clear auth tokens here
    localStorage.removeItem("medicare_dark");
    window.location.reload();
  }, 400);
}

/* ============================================================
   19. LOADING SCREEN
   ============================================================ */

function hideLoadingScreen() {
  const screen = document.getElementById("loadingScreen");
  screen.classList.add("hidden");
  setTimeout(() => screen.remove(), 500);
}

/* ============================================================
   20. EVENT LISTENERS — BOOT
   ============================================================ */

document.addEventListener("DOMContentLoaded", () => {

  /* --- Load data --- */
  loadFromStorage();

  /* --- Restore dark mode preference --- */
  const savedDark = localStorage.getItem("medicare_dark") === "1";
  applyDarkMode(savedDark);

  /* --- Hide loading screen after brief delay --- */
  setTimeout(hideLoadingScreen, 1900);

  /* --- Clock --- */
  updateClock();
  setInterval(updateClock, 1000);

  /* --- Initial render --- */
  renderDashboard();

  /* --- Sidebar navigation --- */
  document.querySelectorAll(".nav-item").forEach(btn => {
    btn.addEventListener("click", () => navigateTo(btn.dataset.section));
  });

  /* --- Mobile sidebar toggle --- */
  document.getElementById("menuToggle").addEventListener("click", openSidebar);
  document.getElementById("sidebarOverlay").addEventListener("click", closeSidebar);

  /* --- Dark mode toggle --- */
  document.getElementById("darkToggle").addEventListener("click", toggleDarkMode);

  /* --- Register form --- */
  document.getElementById("registerForm").addEventListener("submit", handleRegister);

  // Clear error on input
  ["fullName","age","phone","symptoms"].forEach(id => {
    document.getElementById(id).addEventListener("input", () => {
      document.getElementById(id).classList.remove("error");
      document.getElementById(`err-${id}`).textContent = "";
    });
  });

  /* --- Calling section --- */
  // (button callbacks are inline onclick for simplicity)

  /* --- Search --- */
  document.getElementById("searchBtn").addEventListener("click", handleSearch);

  document.getElementById("searchInput").addEventListener("keydown", e => {
    if (e.key === "Enter") handleSearch();
  });

  document.getElementById("searchInput").addEventListener("input",
    debounce(handleSearch, 350)
  );

  /* --- History filter --- */
  document.getElementById("filterStatus").addEventListener("change", () => {
    renderHistory();
  });

  /* --- Clear all history --- */
  document.getElementById("clearAllBtn").addEventListener("click", () => {
    if (state.patients.length === 0) {
      showToast("Không có dữ liệu để xóa", "warning");
      return;
    }
    openModal("deleteModal");
    document.getElementById("deleteMsg").textContent = "Bạn có chắc muốn xóa TOÀN BỘ lịch sử bệnh nhân không?";
    state.deleteTargetId = "__ALL__";
  });

  /* --- Delete modal buttons --- */
  document.getElementById("cancelDelete").addEventListener("click", () => {
    closeModal("deleteModal");
    state.deleteTargetId = null;
  });

  document.getElementById("confirmDelete").addEventListener("click", () => {
    if (state.deleteTargetId === "__ALL__") {
      clearAllPatients();
      state.deleteTargetId = null;
      closeModal("deleteModal");
    } else {
      executeDelete();
    }
  });

  /* --- Success modal close --- */
  document.getElementById("closeSuccessModal").addEventListener("click", () => {
    closeModal("successModal");
  });

  /* --- Logout modal --- */
  document.getElementById("logoutBtn").addEventListener("click", () => openModal("logoutModal"));
  document.getElementById("cancelLogout").addEventListener("click", () => closeModal("logoutModal"));
  document.getElementById("confirmLogout").addEventListener("click", handleLogout);

  /* --- Keyboard shortcuts --- */
  document.addEventListener("keydown", e => {
    // ESC closes any open modal
    if (e.key === "Escape") {
      document.querySelectorAll(".modal-overlay.open").forEach(m => m.classList.remove("open"));
    }
  });

});