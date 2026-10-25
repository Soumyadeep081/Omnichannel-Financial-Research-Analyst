package com.omnichannel.financial.agent.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Local RAG Tool exposed to the Spring AI ChatClient.
 * Performs similarity search against ingested 10-K, 10-Q, and earnings call documents.
 */
@Service
public class RagRetrievalTool {

    private static final Logger log = LoggerFactory.getLogger(RagRetrievalTool.class);
    private final VectorStore vectorStore;

    public RagRetrievalTool(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(description = "Search the Vector Store containing qualitative 10-K filings, 10-Q reports, and earnings call transcripts for strategic business details, management guidance, segment breakdown, and risk factors.")
    public String searchFinancialFilings(
            @ToolParam(description = "Qualitative query string regarding business operations, forward-looking guidance, segment performance (e.g. iPhone, Services), or macro risks.") 
            String query) {

        log.info("[Local RAG Tool] Executing similarity search for query: '{}'", query);

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(4)
                .similarityThreshold(0.65)
                .build();

        List<Document> results = vectorStore.similaritySearch(request);

        if (results.isEmpty()) {
            log.info("[Local RAG Tool] No high-confidence vectors found with threshold 0.65. Falling back to topK=3 without threshold.");
            results = vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(3).build());
        }

        if (results.isEmpty()) {
            return "No matching qualitative filings found in the local vector store.";
        }

        return results.stream()
                .map(doc -> String.format(
                        "--- DOCUMENT EXCERPT (Source: %s) ---\n%s\n",
                        doc.getMetadata().getOrDefault("source_filename", "Financial Filing"),
                        doc.getText()
                ))
                .collect(Collectors.joining("\n"));
    }
}
