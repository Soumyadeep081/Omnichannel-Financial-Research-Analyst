package com.omnichannel.financial.agent.controller;

import com.omnichannel.financial.agent.dto.InvestmentMemoResponse;
import com.omnichannel.financial.agent.dto.ResearchRequest;
import com.omnichannel.financial.agent.orchestrator.InvestmentMemoService;
import com.omnichannel.financial.agent.rag.RagRetrievalTool;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analyst")
public class InvestmentMemoController {

    private final InvestmentMemoService memoService;
    private final RagRetrievalTool ragRetrievalTool;

    public InvestmentMemoController(InvestmentMemoService memoService, RagRetrievalTool ragRetrievalTool) {
        this.memoService = memoService;
        this.ragRetrievalTool = ragRetrievalTool;
    }

    /**
     * Generate an institutional-grade investment research memo synthesizing
     * MySQL data via MCP and qualitative 10-K filings via RAG.
     */
    @PostMapping("/memo")
    public ResponseEntity<InvestmentMemoResponse> generateMemo(@RequestBody ResearchRequest request) {
        InvestmentMemoResponse response = memoService.generateInvestmentMemo(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Convenience GET endpoint for fast testing in browser or curl.
     * Example: GET /api/v1/analyst/memo/AAPL
     */
    @GetMapping("/memo/{ticker}")
    public ResponseEntity<InvestmentMemoResponse> getMemoForTicker(
            @PathVariable("ticker") String ticker,
            @RequestParam(value = "query", required = false) String query) {
        
        String effectiveQuery = (query != null && !query.isBlank()) 
                ? query 
                : "Analyze " + ticker + "'s latest quarterly performance, valuation, and forward outlook";
        
        ResearchRequest request = new ResearchRequest(ticker, effectiveQuery);
        return ResponseEntity.ok(memoService.generateInvestmentMemo(request));
    }

    /**
     * Direct test endpoint for the RAG Vector Store similarity search.
     */
    @GetMapping("/rag/search")
    public ResponseEntity<Map<String, String>> searchRag(@RequestParam("query") String query) {
        String result = ragRetrievalTool.searchFinancialFilings(query);
        return ResponseEntity.ok(Map.of("query", query, "result", result));
    }
}
