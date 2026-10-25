# Omnichannel Financial Research Analyst
### Enterprise Multi-Module RAG + Model Context Protocol (MCP) AI Architecture

An institutional-grade financial analysis AI system built with **Java 21**, **Spring Boot 3.3**, and **Spring AI**. It autonomously cross-references unstructured qualitative filings (10-Ks, 10-Qs, Earnings Transcripts) via a local **RAG Pipeline** with ground-truth quantitative balance sheet metrics stored in **MySQL**, accessed securely via a standalone **Model Context Protocol (MCP)** server over **HTTP + Server-Sent Events (SSE)**.

---

## 1. System Architecture

```text
                                  +------------------------------------------+
                                  |     Wall Street Portfolio Manager        |
                                  +--------------------+---------------------+
                                                       | HTTP POST /memo
                                                       v
+---------------------------------------------------------------------------------------------------------------+
| MODULE 2: financial-agent-orchestrator (Spring Boot App - Port 8080)                                          |
|                                                                                                               |
|   +-------------------------------------------------------------------------------------------------------+   |
|   | Spring AI ChatClient Orchestrator (System Prompt: Institutional Equity Research Analyst)              |   |
|   +-----------------------------------+-----------------------------------+-------------------------------+   |
|                                       |                                   |                                   |
|   (Unstructured Qualitative Pillar)   |                                   |  (Remote MCP Tools via SSE)       |
|                                       v                                   v                                   |
|   +---------------------------------------+       +-------------------------------------------------------+   |
|   | RagRetrievalTool (@Tool)              |       | Spring AI MCP Client (spring-ai-mcp-client)           |   |
|   +-------------------+-------------------+       +-----------------------+-------------------------------+   |
|                       |                                                   |                                   |
|                       v                                                   | HTTP GET /mcp/sse (Handshake)     |
|   +---------------------------------------+                               | HTTP POST /mcp/message (JSON-RPC) |
|   | VectorStore (SimpleVectorStore)       |                               |                                   |
|   +-------------------+-------------------+                               |                                   |
|                       ^                                                   |                                   |
|                       | TokenTextSplitter (800 / 350)                     |                                   |
|   +-------------------+-------------------+                               |                                   |
|   | DocumentIngestionService (ETL)        |                               |                                   |
|   | - PagePdfDocumentReader               |                               |                                   |
|   | - classpath:docs/*.txt, *.pdf         |                               |                                   |
|   +---------------------------------------+                               |                                   |
+---------------------------------------------------------------------------+-----------------------------------+
                                                                            |
                                                                            v
+---------------------------------------------------------------------------------------------------------------+
| MODULE 1: financial-mcp-server (Spring Boot App - Port 8081)                                                  |
|                                                                                                               |
|   +-------------------------------------------------------------------------------------------------------+   |
|   | Spring AI MCP Server WebMVC (spring-ai-mcp-server-webmvc)                                             |   |
|   | Endpoints: SSE /mcp/sse  |  JSON-RPC Message /mcp/message                                            |   |
|   +-------------------+---------------------------------------------------+-------------------------------+   |
|                       |                                                   |                                   |
|                       v                                                   v                                   |
|   +---------------------------------------+       +-------------------------------------------------------+   |
|   | DatabaseFinancialTools (@Tool)        |       | WebSearchTools (@Tool - Secondary Market News)        |   |
|   | - getCompanyFinancials(ticker)        |       | - searchFinancialNews(query, ticker)                  |   |
|   | - compareTickers(tickers)             |       +-------------------------------------------------------+   |
|   +-------------------+-------------------+                                                                   |
|                       | Spring Data JPA                                                                       |
|                       v                                                                                       |
|   +---------------------------------------+                                                                   |
|   | MySQL Database (company_financials)   |                                                                   |
|   | (P/E ratio, Revenue, FCF, Net Debt)   |                                                                   |
|   +---------------------------------------+                                                                   |
+---------------------------------------------------------------------------------------------------------------+
```

---

## 2. Project Modules

| Module Name | Port | Description | Key Dependencies |
| :--- | :--- | :--- | :--- |
| `financial-mcp-server` | `8081` | MCP Server exposing MySQL queries and breaking web news via HTTP+SSE | `spring-ai-mcp-server-webmvc-spring-boot-starter`, `spring-boot-starter-data-jpa`, `mysql-connector-j` |
| `financial-agent-orchestrator` | `8080` | AI Agent client running RAG pipeline and `ChatClient` with dynamic MCP tool injection | `spring-ai-mcp-client-spring-boot-starter`, `spring-ai-openai-spring-boot-starter`, `spring-ai-pdf-document-reader` |

---

## 3. Communication Protocol (HTTP + SSE)

The Model Context Protocol uses Server-Sent Events (SSE) for server-to-client notifications and HTTP POST for JSON-RPC 2.0 messages:

1. **Client Handshake (`GET http://localhost:8081/mcp/sse`):**
   - The MCP Client opens an HTTP SSE stream to the server.
   - The server registers the client and returns an SSE event containing the session message endpoint (`/mcp/message?sessionId=...`).
2. **Tool Discovery (`tools/list`):**
   - The MCP Client sends a JSON-RPC request to the message endpoint.
   - The server inspects `@Tool` beans in its application context and responds with the schemas for `getCompanyFinancials`, `compareTickers`, and `searchFinancialNews`.
3. **Execution (`tools/call`):**
   - When the LLM decides to retrieve financial metrics for a company (e.g. `AAPL`), the client dispatches a `tools/call` request over HTTP.
   - The server invokes the JPA repository, queries MySQL, and returns the result in JSON format over the SSE transport.

---

## 4. Configuration Details

### Server Configuration (`financial-mcp-server/src/main/resources/application.yml`):
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/financial_db?createDatabaseIfNotExist=true&useSSL=false
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: rootpassword

  ai:
    mcp:
      server:
        name: financial-database-mcp-server
        version: 1.0.0
        type: SYNC
        webmvc:
          enabled: true
          sse-endpoint: /mcp/sse
          message-endpoint: /mcp/message
```

### Client Configuration (`financial-agent-orchestrator/src/main/resources/application.yml`):
```yaml
server:
  port: 8080

spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o
          temperature: 0.1
    mcp:
      client:
        enabled: true
        name: financial-analyst-agent
        version: 1.0.0
        type: SYNC
        sse:
          connections:
            financial-db-server:
              url: http://localhost:8081
              sse-endpoint: /mcp/sse
```

---

## 5. How to Run Concurrently

### Prerequisites
- **JDK 21** or later
- **Maven 3.9+**

### Step 1: Start the Database MCP Server (Terminal 1)
```bash
cd d:/Pro/financial-mcp-server
mvn spring-boot:run
```
*The server will initialize the schema, populate mock data for AAPL, MSFT, and NVDA, and begin listening on `http://localhost:8081`.*

### Step 2: Start the Agent Orchestrator (Terminal 2)
```bash
cd d:/Pro/financial-agent-orchestrator
# Optionally export your OpenAI API key (or run with built-in offline simulation mode):
export OPENAI_API_KEY=your_key_here
mvn spring-boot:run
```
*The agent will ingest transcripts from `classpath:docs/` into the VectorStore and connect to the MCP Server at `http://localhost:8081/mcp/sse`.*

---

## 6. Testing the Agent

### Generate Investment Research Memo
```bash
curl -X POST http://localhost:8080/api/v1/analyst/memo \
  -H "Content-Type: application/json" \
  -d '{
    "ticker": "AAPL",
    "query": "Analyze Apple Q3 performance, Services growth trajectory, China market headwinds, and current valuation multiple."
  }'
```

### Browser Quick Test (GET)
```text
http://localhost:8080/api/v1/analyst/memo/AAPL
```

### Test Local RAG Retrieval Directly
```text
http://localhost:8080/api/v1/analyst/rag/search?query=Services+gross+margin+guidance
```
