# 🏛️ Omnichannel Financial Research Analyst - Frontend

Institutional-Grade Wall Street Equity Research Web Application for the **Omnichannel Financial Research Analyst**.

Synthesizes **audited quantitative metrics** from MySQL via the **Model Context Protocol (MCP)** with **qualitative SEC 10-K filings** via **Spring AI RAG** and real-time market sentiment into institutional-grade investment research reports.

---

## 🌟 Key Features

1. **Research Command Deck**:
   - Quick ticker selection for benchmark securities (**AAPL**, **MSFT**, **NVDA**) and custom ticker inputs.
   - One-click institutional thesis prompts (Q3 performance, multiple expansion, capital allocation, Greater China risks).
   - Analyst Persona switcher (Wall Street Managing Director, Forensic Balance Sheet Auditor, Growth & Multiple Specialist).
   - Keyboard shortcut execution (`Ctrl + Enter`).

2. **Real-Time Tri-Pillar Orchestration Visualizer**:
   - Live step-by-step pipeline tracker:
     - **Stage 1 (MCP Quantitative)**: Queries `getCompanyFinancials` over HTTP+SSE (Port 8081).
     - **Stage 2 (Qualitative RAG)**: Performs similarity search across SEC 10-K filings and earnings call transcripts.
     - **Stage 3 (Market Sentiment Wire)**: Cross-references macro sentiment and forward guidance.
     - **Stage 4 (Spring AI ChatClient)**: Senior analyst persona cross-examines guidance against hard balance-sheet data.

3. **Institutional Equity Research Workspace**:
   - **📑 Investment Memo**: Complete rendered Wall Street report with executive summaries, recommendation badges (BUY / STRONG BUY / HOLD / SELL), price targets, and implied upside.
   - **📊 Audited Scorecard**: Full financial metric cards (Revenue, Net Income, EPS, P/E ratio, Free Cash Flow, Debt, Cash, Operating Margin) and quarterly progression tables verified by MySQL MCP.
   - **🔍 Qualitative Citations (RAG)**: Verifiable citation cards with relevance scores, speaker attributions (Tim Cook, Luca Maestri, Satya Nadella), and source document tags.
   - **🏛️ Peer Benchmark Matrix**: Comparative valuation across multiple tickers with P/E multiples, margins, and cash conversion.
   - **🔎 SEC Filings Explorer**: Direct interactive semantic vector search against the RAG store.
   - **⚡ MCP Protocol Stream Inspector**: Live JSON-RPC 2.0 message inspector detailing tool calls and SSE transport payloads.

4. **Institutional Export Suite**:
   - 📋 Copy Raw Markdown Memo
   - 💾 Download Report (`.md`)
   - 🖨️ Print / Export to Institutional PDF (with dedicated `@media print` print styles for a formal research document)

5. **Zero-Configuration Hybrid Resilience**:
   - Automatically detects if the Spring Boot backend (`http://localhost:8080`) is active.
   - If running, it queries the live agent orchestrator.
   - If offline or during development, it seamlessly executes with high-fidelity deterministic demonstration data and realistic pipeline latency.

---

## 🚀 How to Run

### Option 1: Embedded in Spring Boot (Zero Setup)
The frontend is pre-bundled in `financial-agent-orchestrator/src/main/resources/static/`.

When you run the backend orchestrator:
```powershell
./run-agent.ps1
```
Simply open your browser to:
**`http://localhost:8080`**

### Option 2: Standalone Dev Server
To run the frontend independently using Node:
```powershell
cd frontend
npm run dev
```
Open **`http://localhost:5173`**

### Option 3: Direct File Access
You can also directly open `frontend/index.html` in any modern web browser.
