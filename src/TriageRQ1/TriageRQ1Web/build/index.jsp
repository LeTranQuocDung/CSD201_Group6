<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="triage.*" %>
<%@ page import="java.util.*" %>
<%
    // ===== API MODE: Trả JSON khi action=benchmark =====
    if ("benchmark".equals(request.getParameter("action"))) {
        response.setContentType("application/json;charset=UTF-8");

        BenchmarkRunner runner = new BenchmarkRunner();
        List<BenchmarkResult> allResults = new ArrayList<BenchmarkResult>();
        int[] sizes = {50, 200, 500, 1000};

        // Tạo MỚI instance cho mỗi benchmark run (tránh state leak)
        for (int n : sizes) {
            allResults.add(runner.run(new MinHeapTriage(), n));
            allResults.add(runner.run(new SortedLinkedListTriage(), n));
            allResults.add(runner.run(new UnsortedArrayTriage(), n));
        }

        // Mô phỏng tải 200 ops/phút
        StringBuilder simJson = new StringBuilder();
        TriageStructure[] simStructures = {
            new MinHeapTriage(),
            new SortedLinkedListTriage(),
            new UnsortedArrayTriage()
        };

        for (int i = 0; i < simStructures.length; i++) {
            List<Double> times = runner.simulateLoad(simStructures[i], 200);
            double maxTime = 0, avgTime = 0;
            int violations = 0;
            for (int j = 0; j < times.size(); j++) {
                double t = times.get(j).doubleValue();
                avgTime += t;
                if (t > maxTime) maxTime = t;
                if (t > 50.0) violations++;
            }
            avgTime /= times.size();

            if (i > 0) simJson.append(",");
            simJson.append(String.format(Locale.US,
                "{\"structure\":\"%s\",\"avgMs\":%.6f,\"maxMs\":%.6f,\"violations\":%d,\"totalOps\":%d}",
                simStructures[i].getName(), avgTime, maxTime, violations, times.size()));
        }

        // Build full JSON response
        StringBuilder json = new StringBuilder();
        json.append("{\"benchmarks\":[");
        for (int i = 0; i < allResults.size(); i++) {
            if (i > 0) json.append(",");
            json.append(allResults.get(i).toJson());
        }
        json.append("],\"simulation\":[");
        json.append(simJson);
        json.append("]}");

        out.print(json.toString());
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RQ1 — Smart Hospital Patient Triage System</title>
    <meta name="description" content="Benchmark: Min-Heap vs Sorted Linked List vs Unsorted Array cho bài toán triage bệnh nhân">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        /* ===== Reset & Variables ===== */
        *, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

        :root {
            --bg-primary: #0a0e27;
            --bg-secondary: #111536;
            --bg-card: rgba(255,255,255,0.035);
            --bg-card-hover: rgba(255,255,255,0.07);
            --border: rgba(255,255,255,0.07);
            --text: #e4e8f4;
            --text-secondary: #8a94b2;
            --text-muted: #555e7e;
            --blue: #4f8cff;
            --cyan: #00d4ff;
            --purple: #a855f7;
            --green: #00e676;
            --red: #ff4757;
            --orange: #ffb347;
            --grad-blue: linear-gradient(135deg, #4f8cff, #00d4ff);
            --grad-purple: linear-gradient(135deg, #a855f7, #ec4899);
            --grad-green: linear-gradient(135deg, #00e676, #00d4ff);
            --font: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
        }

        html { scroll-behavior: smooth; }

        body {
            font-family: var(--font);
            background: var(--bg-primary);
            color: var(--text);
            min-height: 100vh;
            overflow-x: hidden;
            line-height: 1.6;
        }

        /* Ambient light effects */
        body::before {
            content: '';
            position: fixed; inset: 0;
            background:
                radial-gradient(ellipse 600px 400px at 15% 15%, rgba(79,140,255,0.07) 0%, transparent 70%),
                radial-gradient(ellipse 500px 500px at 85% 80%, rgba(168,85,247,0.06) 0%, transparent 70%),
                radial-gradient(ellipse 400px 300px at 50% 50%, rgba(0,212,255,0.03) 0%, transparent 70%);
            pointer-events: none; z-index: 0;
        }

        .container { max-width: 1200px; margin: 0 auto; padding: 0 24px; position: relative; z-index: 1; }

        /* ===== Header ===== */
        .header {
            padding: 18px 0;
            border-bottom: 1px solid var(--border);
            background: rgba(10,14,39,0.85);
            backdrop-filter: blur(24px);
            -webkit-backdrop-filter: blur(24px);
            position: sticky; top: 0; z-index: 100;
        }
        .header-inner { display: flex; align-items: center; gap: 12px; }
        .header-icon { font-size: 26px; }
        .header h1 {
            font-size: 18px; font-weight: 700;
            background: var(--grad-blue);
            -webkit-background-clip: text; -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        .header-meta { margin-left: auto; font-size: 12px; color: var(--text-muted); }

        /* ===== Hero ===== */
        .hero { text-align: center; padding: 72px 0 56px; }
        .hero-badge {
            display: inline-block; padding: 5px 16px;
            background: rgba(79,140,255,0.1); border: 1px solid rgba(79,140,255,0.2);
            border-radius: 100px; font-size: 13px; color: var(--blue); font-weight: 500;
            margin-bottom: 20px; letter-spacing: 0.5px;
        }
        .hero h2 {
            font-size: clamp(28px, 5vw, 48px); font-weight: 800; line-height: 1.1;
            margin-bottom: 16px;
            background: linear-gradient(135deg, #fff 30%, #a0acd0 100%);
            -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
        }
        .hero p {
            font-size: 17px; color: var(--text-secondary);
            max-width: 620px; margin: 0 auto 36px;
        }

        /* ===== Run Button ===== */
        .run-btn {
            display: inline-flex; align-items: center; gap: 10px;
            padding: 15px 40px; background: var(--grad-blue);
            border: none; border-radius: 14px; color: #fff;
            font-size: 16px; font-weight: 600; cursor: pointer;
            font-family: var(--font);
            transition: all 0.3s ease;
            box-shadow: 0 4px 24px rgba(79,140,255,0.3);
        }
        .run-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 32px rgba(79,140,255,0.45); }
        .run-btn:active { transform: translateY(0); }
        .run-btn.running { opacity: 0.6; cursor: not-allowed; pointer-events: none; }

        @keyframes pulse-glow {
            0%,100% { box-shadow: 0 4px 24px rgba(79,140,255,0.3); }
            50%     { box-shadow: 0 4px 40px rgba(79,140,255,0.55); }
        }
        .run-btn:not(.running) { animation: pulse-glow 2.5s ease-in-out infinite; }

        /* ===== Loading ===== */
        .loading {
            display: none; position: fixed; inset: 0;
            background: rgba(10,14,39,0.92); backdrop-filter: blur(12px);
            z-index: 1000; justify-content: center; align-items: center; flex-direction: column;
        }
        .loading.active { display: flex; }
        .spinner {
            width: 56px; height: 56px;
            border: 3px solid rgba(79,140,255,0.15);
            border-top-color: var(--blue);
            border-radius: 50%;
            animation: spin 0.9s linear infinite;
        }
        @keyframes spin { to { transform: rotate(360deg); } }
        .loading-text { margin-top: 18px; font-size: 15px; color: var(--text-secondary); }
        .loading-sub  { margin-top: 6px; font-size: 13px; color: var(--text-muted); }

        /* ===== Results ===== */
        .results { display: none; padding-bottom: 60px; }
        .results.visible { display: block; }

        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(24px); }
            to   { opacity: 1; transform: translateY(0); }
        }
        .fade-in { animation: fadeUp 0.5s ease both; }

        .section { margin-bottom: 48px; }
        .section-title {
            font-size: 22px; font-weight: 700; margin-bottom: 20px;
            display: flex; align-items: center; gap: 10px;
        }
        .section-title .icon { font-size: 22px; }

        /* ===== Stat Cards ===== */
        .stat-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px,1fr)); gap: 18px; }
        .stat-card {
            background: var(--bg-card); border: 1px solid var(--border);
            border-radius: 16px; padding: 22px;
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
        }
        .stat-card:hover { background: var(--bg-card-hover); transform: translateY(-3px); box-shadow: 0 8px 30px rgba(79,140,255,0.1); }
        .stat-label { font-size: 12px; color: var(--text-muted); text-transform: uppercase; letter-spacing: 1px; margin-bottom: 6px; }
        .stat-value { font-size: 26px; font-weight: 700; }
        .stat-sub { font-size: 12px; color: var(--text-secondary); margin-top: 4px; }
        .stat-card:nth-child(1) .stat-value { color: var(--blue); }
        .stat-card:nth-child(2) .stat-value { color: var(--cyan); }
        .stat-card:nth-child(3) .stat-value { color: var(--green); }
        .stat-card:nth-child(4) .stat-value { color: var(--purple); }

        /* ===== Charts ===== */
        .chart-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(380px,1fr)); gap: 20px; }
        .chart-card {
            background: var(--bg-card); border: 1px solid var(--border);
            border-radius: 16px; padding: 24px;
            backdrop-filter: blur(10px);
        }
        .chart-card h3 { font-size: 15px; font-weight: 600; margin-bottom: 14px; }
        .chart-wrap { position: relative; height: 300px; }

        /* ===== Tables ===== */
        .table-card {
            background: var(--bg-card); border: 1px solid var(--border);
            border-radius: 16px; padding: 24px;
            backdrop-filter: blur(10px); overflow-x: auto;
        }
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        thead { background: rgba(79,140,255,0.06); }
        th {
            padding: 12px 14px; text-align: left; font-weight: 600;
            color: var(--blue); font-size: 11px; text-transform: uppercase;
            letter-spacing: 0.5px; border-bottom: 2px solid rgba(79,140,255,0.12);
            white-space: nowrap;
        }
        td { padding: 10px 14px; border-bottom: 1px solid var(--border); white-space: nowrap; }
        tbody tr { transition: background 0.2s; }
        tbody tr:hover td { background: rgba(255,255,255,0.02); }
        .badge {
            display: inline-block; padding: 3px 10px;
            border-radius: 6px; font-size: 11px; font-weight: 600;
        }
        .badge-pass { background: rgba(0,230,118,0.1); color: var(--green); }
        .badge-fail { background: rgba(255,71,87,0.1); color: var(--red); }
        .badge-warn { background: rgba(255,179,71,0.1); color: var(--orange); }

        /* Structure name colors in table */
        .struct-heap { color: var(--blue); font-weight: 600; }
        .struct-sorted { color: var(--purple); font-weight: 600; }
        .struct-unsorted { color: var(--cyan); font-weight: 600; }

        /* ===== Conclusion ===== */
        .conclusion-card {
            background: linear-gradient(135deg, rgba(79,140,255,0.05), rgba(168,85,247,0.05));
            border: 1px solid rgba(79,140,255,0.12);
            border-radius: 16px; padding: 28px;
        }
        .conclusion-card h3 { font-size: 20px; font-weight: 700; margin-bottom: 18px; display: flex; align-items: center; gap: 10px; }
        .conclusion-item { display: flex; align-items: center; gap: 14px; padding: 10px 0; border-bottom: 1px solid var(--border); }
        .conclusion-item:last-child { border-bottom: none; }
        .conclusion-label { font-size: 14px; color: var(--text-secondary); min-width: 200px; }
        .conclusion-value { font-size: 14px; font-weight: 600; color: var(--green); }

        .conclusion-notes {
            margin-top: 20px; padding: 16px; background: rgba(0,0,0,0.2);
            border-radius: 10px; border-left: 3px solid var(--blue);
        }
        .conclusion-notes p { font-size: 13px; color: var(--text-secondary); margin-bottom: 6px; }
        .conclusion-notes p:last-child { margin-bottom: 0; }

        /* ===== Footer ===== */
        .footer {
            text-align: center; padding: 28px 0;
            color: var(--text-muted); font-size: 12px;
            border-top: 1px solid var(--border);
        }

        /* ===== Responsive ===== */
        @media (max-width: 768px) {
            .stat-cards { grid-template-columns: repeat(2, 1fr); }
            .chart-grid { grid-template-columns: 1fr; }
            .conclusion-item { flex-direction: column; align-items: flex-start; gap: 4px; }
            .conclusion-label { min-width: unset; }
        }
    </style>
</head>
<body>

<!-- ===== Header ===== -->
<header class="header">
    <div class="container header-inner">
        <span class="header-icon">🏥</span>
        <h1>Smart Hospital Patient Triage System</h1>
        <span class="header-meta">CSD201 / CSD203 — FPT University</span>
    </div>
</header>

<!-- ===== Hero ===== -->
<section class="hero">
    <div class="container">
        <div class="hero-badge">Research Question 1</div>
        <h2>Data Structure<br>Performance Benchmark</h2>
        <p>So sánh Min-Heap, Sorted Linked List và Unsorted Array — cấu trúc nào duy trì insert và extract-min ổn định dưới 50ms với tối thiểu 200 lượt cập nhật/phút?</p>
        <button id="runBtn" class="run-btn" onclick="runBenchmark()">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="5 3 19 12 5 21 5 3"/></svg>
            Chạy Benchmark
        </button>
    </div>
</section>

<!-- ===== Loading Overlay ===== -->
<div id="loading" class="loading">
    <div class="spinner"></div>
    <div class="loading-text">Đang chạy benchmark...</div>
    <div class="loading-sub">Warm-up + 1000 iterations × 3 cấu trúc × 4 kích thước</div>
</div>

<!-- ===== Results ===== -->
<div id="results" class="results">
    <div class="container">

        <!-- Stat Cards -->
        <div class="section fade-in" style="animation-delay:0.1s">
            <h2 class="section-title"><span class="icon">📊</span> Tổng Quan</h2>
            <div class="stat-cards" id="statCards"></div>
        </div>

        <!-- Charts -->
        <div class="section fade-in" style="animation-delay:0.2s">
            <h2 class="section-title"><span class="icon">📈</span> Biểu Đồ So Sánh</h2>
            <div class="chart-grid">
                <div class="chart-card">
                    <h3>⏱ Avg Insert Time (ms) theo Data Size</h3>
                    <div class="chart-wrap"><canvas id="chartInsert"></canvas></div>
                </div>
                <div class="chart-card">
                    <h3>⏱ Avg Extract-Min Time (ms) theo Data Size</h3>
                    <div class="chart-wrap"><canvas id="chartExtract"></canvas></div>
                </div>
            </div>
        </div>

        <!-- Benchmark Table -->
        <div class="section fade-in" style="animation-delay:0.3s">
            <h2 class="section-title"><span class="icon">📋</span> Bảng Kết Quả Chi Tiết</h2>
            <div class="table-card">
                <table>
                    <thead>
                        <tr>
                            <th>Cấu Trúc</th>
                            <th>n</th>
                            <th>Avg Insert (ms)</th>
                            <th>Max Insert (ms)</th>
                            <th>Avg Extract (ms)</th>
                            <th>Max Extract (ms)</th>
                            <th>Insert Vio &gt;50ms</th>
                            <th>Extract Vio &gt;50ms</th>
                            <th>Trạng Thái</th>
                        </tr>
                    </thead>
                    <tbody id="benchmarkTableBody"></tbody>
                </table>
            </div>
        </div>

        <!-- Simulation -->
        <div class="section fade-in" style="animation-delay:0.4s">
            <h2 class="section-title"><span class="icon">🔄</span> Mô Phỏng Tải: 200 ops/phút (n=200)</h2>
            <div class="table-card">
                <table>
                    <thead>
                        <tr>
                            <th>Cấu Trúc</th>
                            <th>Avg (ms)</th>
                            <th>Max (ms)</th>
                            <th>Vượt 50ms</th>
                            <th>Tổng Ops</th>
                            <th>Trạng Thái</th>
                        </tr>
                    </thead>
                    <tbody id="simTableBody"></tbody>
                </table>
            </div>
        </div>

        <!-- Max Time Chart -->
        <div class="section fade-in" style="animation-delay:0.45s">
            <div class="chart-grid">
                <div class="chart-card">
                    <h3>🔺 Max Insert Time (ms) theo Data Size</h3>
                    <div class="chart-wrap"><canvas id="chartMaxInsert"></canvas></div>
                </div>
                <div class="chart-card">
                    <h3>🔺 Max Extract-Min Time (ms) theo Data Size</h3>
                    <div class="chart-wrap"><canvas id="chartMaxExtract"></canvas></div>
                </div>
            </div>
        </div>

        <!-- Conclusion -->
        <div class="section fade-in" style="animation-delay:0.5s">
            <h2 class="section-title"><span class="icon">🏆</span> Kết Luận RQ1</h2>
            <div class="conclusion-card" id="conclusionCard"></div>
        </div>

    </div>
</div>

<!-- ===== Footer ===== -->
<footer class="footer">
    <div class="container">
        CSD201/CSD203 — Data Structures & Algorithms — FPT University 2025-2026<br>
        Smart Hospital Patient Triage System — RQ1 Benchmark Report
    </div>
</footer>

<script>
// ===== Chart.js Global Config =====
Chart.defaults.color = '#8a94b2';
Chart.defaults.borderColor = 'rgba(255,255,255,0.06)';
Chart.defaults.font.family = "'Inter', sans-serif";

const COLORS = {
    'Min-Heap':           { bg: 'rgba(79,140,255,0.7)',  border: '#4f8cff' },
    'Sorted Linked List': { bg: 'rgba(168,85,247,0.7)',  border: '#a855f7' },
    'Unsorted Array':     { bg: 'rgba(0,212,255,0.7)',   border: '#00d4ff' }
};

const STRUCT_CLASS = {
    'Min-Heap': 'struct-heap',
    'Sorted Linked List': 'struct-sorted',
    'Unsorted Array': 'struct-unsorted'
};

let chartInstances = [];

// ===== Main Function =====
function runBenchmark() {
    const btn = document.getElementById('runBtn');
    const loading = document.getElementById('loading');
    const results = document.getElementById('results');

    btn.classList.add('running');
    btn.textContent = 'Đang chạy...';
    loading.classList.add('active');
    results.classList.remove('visible');

    // Destroy old charts
    chartInstances.forEach(c => c.destroy());
    chartInstances = [];

    fetch('index.jsp?action=benchmark')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            loading.classList.remove('active');
            btn.classList.remove('running');
            btn.innerHTML = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="5 3 19 12 5 21 5 3"/></svg> Chạy Lại';
            renderResults(data);
            results.classList.add('visible');
            results.scrollIntoView({ behavior: 'smooth', block: 'start' });
        })
        .catch(function(err) {
            loading.classList.remove('active');
            btn.classList.remove('running');
            btn.textContent = 'Lỗi - Thử lại';
            alert('Lỗi khi chạy benchmark: ' + err.message);
        });
}

// ===== Render Results =====
function renderResults(data) {
    var benchmarks = data.benchmarks;
    var simulation = data.simulation;

    renderStatCards(benchmarks, simulation);
    renderBenchmarkTable(benchmarks);
    renderSimTable(simulation);
    renderCharts(benchmarks);
    renderConclusion(benchmarks, simulation);
}

// ===== Stat Cards =====
function renderStatCards(benchmarks, simulation) {
    // Find best at n=500
    var b500 = benchmarks.filter(function(b) { return b.dataSize === 500; });
    var bestIns = b500.reduce(function(a, b) { return a.avgInsertMs < b.avgInsertMs ? a : b; });
    var bestExt = b500.reduce(function(a, b) { return a.avgExtractMs < b.avgExtractMs ? a : b; });
    var totalVio = benchmarks.reduce(function(s, b) { return s + b.insertViolations + b.extractViolations; }, 0);
    var simVio = simulation.reduce(function(s, sim) { return s + sim.violations; }, 0);

    document.getElementById('statCards').innerHTML =
        '<div class="stat-card"><div class="stat-label">Insert Nhanh Nhất (n=500)</div>' +
        '<div class="stat-value">' + bestIns.structure + '</div>' +
        '<div class="stat-sub">avg ' + bestIns.avgInsertMs.toFixed(4) + ' ms</div></div>' +

        '<div class="stat-card"><div class="stat-label">Extract Nhanh Nhất (n=500)</div>' +
        '<div class="stat-value">' + bestExt.structure + '</div>' +
        '<div class="stat-sub">avg ' + bestExt.avgExtractMs.toFixed(4) + ' ms</div></div>' +

        '<div class="stat-card"><div class="stat-label">Benchmark Violations</div>' +
        '<div class="stat-value">' + totalVio + '</div>' +
        '<div class="stat-sub">' + (totalVio === 0 ? 'Tất cả < 50ms ✓' : 'Có vi phạm ngưỡng 50ms') + '</div></div>' +

        '<div class="stat-card"><div class="stat-label">Simulation Violations</div>' +
        '<div class="stat-value">' + simVio + ' / 600</div>' +
        '<div class="stat-sub">' + (simVio === 0 ? 'Ổn định ✓' : 'Có vi phạm khi mô phỏng') + '</div></div>';
}

// ===== Benchmark Table =====
function renderBenchmarkTable(benchmarks) {
    var html = '';
    benchmarks.forEach(function(b) {
        var safe = b.insertViolations === 0 && b.extractViolations === 0;
        var cls = STRUCT_CLASS[b.structure] || '';
        html += '<tr>' +
            '<td class="' + cls + '">' + b.structure + '</td>' +
            '<td>' + b.dataSize + '</td>' +
            '<td>' + b.avgInsertMs.toFixed(4) + '</td>' +
            '<td>' + b.maxInsertMs.toFixed(4) + '</td>' +
            '<td>' + b.avgExtractMs.toFixed(4) + '</td>' +
            '<td>' + b.maxExtractMs.toFixed(4) + '</td>' +
            '<td>' + b.insertViolations + '</td>' +
            '<td>' + b.extractViolations + '</td>' +
            '<td><span class="badge ' + (safe ? 'badge-pass' : 'badge-fail') + '">' +
                (safe ? 'ĐẠT' : 'VI PHẠM') + '</span></td>' +
            '</tr>';
    });
    document.getElementById('benchmarkTableBody').innerHTML = html;
}

// ===== Simulation Table =====
function renderSimTable(simulation) {
    var html = '';
    simulation.forEach(function(s) {
        var safe = s.violations === 0;
        var cls = STRUCT_CLASS[s.structure] || '';
        html += '<tr>' +
            '<td class="' + cls + '">' + s.structure + '</td>' +
            '<td>' + s.avgMs.toFixed(4) + '</td>' +
            '<td>' + s.maxMs.toFixed(4) + '</td>' +
            '<td>' + s.violations + '</td>' +
            '<td>' + s.totalOps + '</td>' +
            '<td><span class="badge ' + (safe ? 'badge-pass' : 'badge-fail') + '">' +
                (safe ? 'ĐẠT' : 'VI PHẠM') + '</span></td>' +
            '</tr>';
    });
    document.getElementById('simTableBody').innerHTML = html;
}

// ===== Charts =====
function renderCharts(benchmarks) {
    var sizes = [50, 200, 500, 1000];
    var structures = ['Min-Heap', 'Sorted Linked List', 'Unsorted Array'];

    function getData(struct, field) {
        return sizes.map(function(n) {
            var found = benchmarks.find(function(b) { return b.structure === struct && b.dataSize === n; });
            return found ? found[field] : 0;
        });
    }

    function makeChart(canvasId, field, title) {
        var ctx = document.getElementById(canvasId).getContext('2d');
        var chart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: sizes.map(function(s) { return 'n=' + s; }),
                datasets: structures.map(function(struct) {
                    return {
                        label: struct,
                        data: getData(struct, field),
                        backgroundColor: COLORS[struct].bg,
                        borderColor: COLORS[struct].border,
                        borderWidth: 1.5,
                        borderRadius: 6,
                        borderSkipped: false
                    };
                })
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: { mode: 'index', intersect: false },
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { padding: 16, usePointStyle: true, pointStyleWidth: 10, font: { size: 12 } }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(17,21,54,0.95)',
                        titleColor: '#e4e8f4',
                        bodyColor: '#8a94b2',
                        borderColor: 'rgba(79,140,255,0.2)',
                        borderWidth: 1,
                        cornerRadius: 8,
                        padding: 12,
                        callbacks: {
                            label: function(ctx) { return ctx.dataset.label + ': ' + ctx.parsed.y.toFixed(4) + ' ms'; }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        title: { display: true, text: 'Time (ms)', font: { size: 12 } },
                        grid: { color: 'rgba(255,255,255,0.04)' },
                        ticks: { font: { size: 11 } }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { font: { size: 12 } }
                    }
                }
            }
        });
        chartInstances.push(chart);
    }

    makeChart('chartInsert', 'avgInsertMs', 'Avg Insert');
    makeChart('chartExtract', 'avgExtractMs', 'Avg Extract');
    makeChart('chartMaxInsert', 'maxInsertMs', 'Max Insert');
    makeChart('chartMaxExtract', 'maxExtractMs', 'Max Extract');
}

// ===== Conclusion =====
function renderConclusion(benchmarks, simulation) {
    var b500 = benchmarks.filter(function(b) { return b.dataSize === 500; });
    var bestIns = b500.reduce(function(a, b) { return a.avgInsertMs < b.avgInsertMs ? a : b; });
    var bestExt = b500.reduce(function(a, b) { return a.avgExtractMs < b.avgExtractMs ? a : b; });

    var html = '<h3>🏆 Kết Luận</h3>';

    // Items
    html += '<div class="conclusion-item">' +
        '<span class="conclusion-label">Insert nhanh nhất tại n=500</span>' +
        '<span class="conclusion-value">' + bestIns.structure + ' (avg ' + bestIns.avgInsertMs.toFixed(4) + 'ms)</span></div>';

    html += '<div class="conclusion-item">' +
        '<span class="conclusion-label">Extract nhanh nhất tại n=500</span>' +
        '<span class="conclusion-value">' + bestExt.structure + ' (avg ' + bestExt.avgExtractMs.toFixed(4) + 'ms)</span></div>';

    // Check 50ms for each structure at n=500
    b500.forEach(function(b) {
        var safe = b.insertViolations === 0 && b.extractViolations === 0;
        html += '<div class="conclusion-item">' +
            '<span class="conclusion-label">' + b.structure + ' (n=500)</span>' +
            '<span class="conclusion-value" style="color:' + (safe ? 'var(--green)' : 'var(--red)') + '">' +
                (safe ? '✓ ĐẠT < 50ms ổn định' : '✗ CÓ vi phạm 50ms (ins=' + b.insertViolations + ', ext=' + b.extractViolations + ')') +
            '</span></div>';
    });

    // Notes
    html += '<div class="conclusion-notes">' +
        '<p><strong>📝 Phân tích:</strong></p>' +
        '<p>• Min-Heap (PriorityQueue): O(log n) cho cả insert và extract — hiệu năng cân bằng, phù hợp nhất cho triage thực tế.</p>' +
        '<p>• Sorted Linked List: extract O(1) rất nhanh, nhưng insert O(n) chậm dần khi n lớn.</p>' +
        '<p>• Unsorted Array: insert O(1) nhanh, nhưng extract O(n) chậm dần khi n lớn.</p>' +
        '<p>• <strong>Trả lời RQ1:</strong> Min-Heap là cấu trúc duy trì ổn định cả insert và extract-min dưới 50ms với tải 200 cập nhật/phút.</p>' +
        '</div>';

    document.getElementById('conclusionCard').innerHTML = html;
}
</script>

</body>
</html>
