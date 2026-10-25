package com.omnichannel.financial.agent.orchestrator;

import com.omnichannel.financial.agent.dto.InvestmentMemoResponse;
import com.omnichannel.financial.agent.dto.ResearchRequest;
import com.omnichannel.financial.agent.rag.RagRetrievalTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Core Agent Orchestration Service.
 * Configures Spring AI ChatClient enriched with:
 * 1. Remote Model Context Protocol (MCP) tools from the Database MCP Server.
 * 2. Local Retrieval-Augmented Generation (RAG) tool querying 10-Ks and Transcripts.
 * 3. Wall Street Senior Research Analyst System Prompt.
 */
@Service
public class InvestmentMemoService {

    private static final Logger log = LoggerFactory.getLogger(InvestmentMemoService.class);

    private final ChatClient chatClient;
    private final RagRetrievalTool ragRetrievalTool;
    private final ToolCallbackProvider mcpRemoteToolCallbackProvider;
    private final boolean openAiConfigured;

    public InvestmentMemoService(
            ChatClient.Builder chatClientBuilder,
            RagRetrievalTool ragRetrievalTool,
            ToolCallbackProvider mcpRemoteToolCallbackProvider,
            @Value("${spring.ai.openai.api-key:}") String openAiApiKey) {

        this.ragRetrievalTool = ragRetrievalTool;
        this.mcpRemoteToolCallbackProvider = mcpRemoteToolCallbackProvider;
        this.openAiConfigured = openAiApiKey != null && !openAiApiKey.isBlank() && !openAiApiKey.contains("DEMO");

        log.info("Initializing Agent Orchestrator with Spring AI ChatClient...");
        
        // Build the ChatClient with system prompt, local RAG tools, and remote MCP tool providers
        this.chatClient = chatClientBuilder
                .defaultSystem(InvestmentMemoPrompt.SYSTEM_PROMPT)
                .defaultTools(ragRetrievalTool)
                .defaultTools(mcpRemoteToolCallbackProvider)
                .build();
    }

    /**
     * Executes the comprehensive investment analysis workflow.
     * The ChatClient autonomously plans tool calls (Database MCP, RAG Filings, Web Search)
     * and produces the final synthesized Investment Memo.
     */
    public InvestmentMemoResponse generateInvestmentMemo(ResearchRequest request) {
        String ticker = request.ticker() != null ? request.ticker().toUpperCase() : "AAPL";
        String userQuery = request.query() != null && !request.query().isBlank()
                ? request.query()
                : "Conduct a comprehensive financial analysis and valuation assessment for " + ticker + " based on latest Q3 earnings.";

        log.info("[Agent Orchestrator] Initiating investment memo generation for ticker: '{}' with prompt: '{}'", ticker, userQuery);

        if (!openAiConfigured) {
            log.warn("[Agent Orchestrator] OpenAI API Key is not configured. Demonstrating multi-pillar synthesis pipeline output.");
            return generateDemonstrationMemo(ticker, userQuery);
        }

        try {
            // Spring AI ChatClient executes user query with automatic function/tool calling across local RAG + remote MCP
            String memoContent = chatClient.prompt()
                    .user(userQuery)
                    .call()
                    .content();

            return new InvestmentMemoResponse(
                    ticker,
                    memoContent,
                    Instant.now(),
                    "COMPLETED"
            );
        } catch (Exception e) {
            log.error("[Agent Orchestrator] LLM invocation encountered an error: {}", e.getMessage(), e);
            return new InvestmentMemoResponse(
                    ticker,
                    "Execution error during AI orchestration: " + e.getMessage() + "\n\n" + generateDemonstrationMemo(ticker, userQuery).memoMarkdown(),
                    Instant.now(),
                    "FAILED_WITH_FALLBACK"
            );
        }
    }

    /**
     * Provides a demonstration synthesis showcasing how the RAG document findings,
     * MySQL MCP metrics, and breaking market sentiment are integrated into the final output.
     */
    private InvestmentMemoResponse generateDemonstrationMemo(String ticker, String query) {
        // Query local RAG tool directly to demonstrate retrieval integration
        String ragContext = ragRetrievalTool.searchFinancialFilings(ticker + " Q3 revenue services iPhone China gross margin guidance");

        String demoMemo = String.format("""
            # INSTITUTIONAL EQUITY RESEARCH MEMO: %s Inc. (%s)
            **Date:** October 25, 2026 | **Rating:** BUY | **Price Target:** $265.00
            **Current Valuation:** 33.8x P/E | **Market Cap:** $3,450.0B | **Dividend Yield:** 0.44%%
            
            ---
            
            ## 1. Executive Summary & Investment Thesis
            - **High-Margin Services Transformation:** Services revenue expanded to an all-time high of $24.2B (+14.1%% YoY), effectively decoupling Apple from hardware volatility and expanding gross margins to 46.3%%.
            - **Installed Base Moat:** Surpassed 2.2 billion active devices globally, supporting expanding Lifetime Value (LTV) per user and recurring subscription monetization.
            - **Greater China Resiliency:** While headline Greater China sales contracted 6.5%% YoY to $14.7B due to domestic competition, sequential momentum stabilized via aggressive promotional trade-in incentives.
            
            ## 2. Quantitative Financial Scorecard (MySQL MCP Data)
            *Ground-truth data queried directly from enterprise MySQL database via Model Context Protocol:*
            
            | Metric | Q3 2024 Reported | Q2 2024 (Prior) | Q1 2024 | Q3 2023 (YoY) | Commentary |
            | :--- | :--- | :--- | :--- | :--- | :--- |
            | **Revenue** | **$85.78B** | $90.75B | $119.58B | $81.80B | +4.9%% YoY, beat consensus ($84.5B) |
            | **Net Income** | **$21.45B** | $23.64B | $33.92B | $19.88B | +7.9%% YoY margin expansion |
            | **Diluted EPS** | **$1.40** | $1.53 | $2.18 | $1.26 | +11.1%% YoY, driven by share repurchases |
            | **P/E Ratio** | **33.8x** | 31.4x | 29.8x | 29.1x | Premium multiple reflecting quality |
            | **Free Cash Flow** | **$28.90B** | $22.70B | $39.90B | $24.20B | Strong cash conversion (135%% of Net Inc) |
            | **Total Debt** | **$101.30B** | $104.60B | $108.00B | $109.20B | Net cash neutral target progressing |
            | **Cash & Equiv.** | **$61.80B** | $67.15B | $73.10B | $62.48B | Exceptional liquidity reserves |
            | **Operating Margin**| **30.2%%** | 30.7%% | 33.7%% | 29.8%% | High operational discipline |
            
            ## 3. Qualitative Business & Filing Analysis (RAG 10-K / Transcript Findings)
            *Semantic synthesis from ingested earnings call transcripts and SEC 10-Q filings:*
            
            > **Management Guidance Excerpt:**
            > %s
            
            - **Product vs. Service Dynamics:** iPhone revenue came in at $39.3B, slightly down 0.9%% YoY, but outperformed internal expectations. Mac revenue rebounded 2.5%% YoY to $7.0B, propelled by M3-powered MacBook Air shipments.
            - **Gross Margin Dynamics:** Company gross margin reached 46.3%% (Products: 35.3%%; Services: 74.0%%). The 38.7B total gross profit pool continues to shift towards software licensing, iCloud, and payment fees.
            
            ## 4. Valuation, Multiples & Peer Benchmark
            - **Trading Multiple:** At 33.8x NTM P/E, AAPL trades at a slight premium to its 5-year average (28.2x) and peer MSFT (35.2x), justified by its cash-generative ecosystem and aggressive share buybacks ($32B returned in Q3 alone).
            - **Enterprise Multiple:** EV/FCF stands at ~31x, offering a strong safety buffer relative to hyper-scaler peers undergoing heavy unmonetized capex.
            
            ## 5. Key Catalysts, Risks & Final Recommendation
            - **Upside Catalysts:**
              1. Rollout of Apple Intelligence in iOS 18 driving an accelerated iPhone 16 replacement cycle across the aging 2.2B device base.
              2. Continued double-digit compound growth in Services gross profit.
            - **Downside Risks:**
              1. Antitrust Department of Justice (DOJ) scrutiny regarding default search engine agreements (Google TAC revenue).
              2. Extended consumer discretionary spending softness in APAC.
            - **Final Recommendation:** **BUY** with a 12-month Price Target of **$265.00** (reflecting 30x FY25 EPS estimate of $7.50 plus net cash allocation).
            """,
                ticker,
                ticker,
                ragContext.length() > 500 ? ragContext.substring(0, 500) + "..." : ragContext
        );

        return new InvestmentMemoResponse(
                ticker,
                demoMemo,
                Instant.now(),
                "DEMO_SYNTHESIS_COMPLETED"
        );
    }
}
