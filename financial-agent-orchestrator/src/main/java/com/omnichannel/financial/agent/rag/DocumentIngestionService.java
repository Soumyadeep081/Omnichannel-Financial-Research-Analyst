package com.omnichannel.financial.agent.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RAG Ingestion ETL Pipeline:
 * Reads unstructured financial filings (10-Ks, 10-Qs, Earnings Transcripts)
 * from 'classpath:docs/*', chunks them with TokenTextSplitter, and indexes them
 * into the Spring AI VectorStore.
 */
@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        // High-precision financial chunking: 800 tokens chunk size, 350 tokens overlap for context continuity
        this.tokenTextSplitter = new TokenTextSplitter(800, 350, 5, 10000, true);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ingestFinancialDocumentsOnStartup() {
        log.info("Starting RAG Ingestion Pipeline for financial filings in 'classpath:docs/*'...");
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:docs/*.*");

            if (resources.length == 0) {
                log.warn("No financial documents found in 'classpath:docs/'. Ingestion skipped.");
                return;
            }

            List<Document> allExtractedDocs = new ArrayList<>();

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) continue;

                log.info("Processing filing: {}", filename);

                if (filename.toLowerCase().endsWith(".pdf")) {
                    List<Document> pdfDocs = readPdfDocument(resource);
                    allExtractedDocs.addAll(pdfDocs);
                } else if (filename.toLowerCase().endsWith(".txt") || filename.toLowerCase().endsWith(".md")) {
                    List<Document> textDocs = readTextDocument(resource);
                    allExtractedDocs.addAll(textDocs);
                }
            }

            if (!allExtractedDocs.isEmpty()) {
                log.info("Chunking {} extracted document sections with TokenTextSplitter...", allExtractedDocs.size());
                List<Document> chunkedDocs = tokenTextSplitter.apply(allExtractedDocs);

                log.info("Writing {} chunked vectors to Spring AI VectorStore...", chunkedDocs.size());
                vectorStore.add(chunkedDocs);
                log.info("RAG Ingestion Pipeline completed successfully. Documents ready for semantic search.");
            }

        } catch (IOException e) {
            log.error("Failed to read financial documents during RAG ingestion", e);
        }
    }

    private List<Document> readPdfDocument(Resource resource) {
        try {
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                    .withPageTopMargin(0)
                    .withPageBottomMargin(0)
                    .build();

            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, config);
            List<Document> docs = pdfReader.read();
            log.info("Parsed {} pages from PDF: {}", docs.size(), resource.getFilename());
            return docs;
        } catch (Exception e) {
            log.error("Error reading PDF filing: {}", resource.getFilename(), e);
            return List.of();
        }
    }

    private List<Document> readTextDocument(Resource resource) {
        try {
            TextReader textReader = new TextReader(resource);
            textReader.getCustomMetadata().put("source_filename", resource.getFilename());
            textReader.getCustomMetadata().put("doc_type", "earnings_transcript");
            List<Document> docs = textReader.read();
            log.info("Parsed text filing: {} (length: {} chars)", resource.getFilename(), docs.get(0).getText().length());
            return docs;
        } catch (Exception e) {
            log.error("Error reading text filing: {}", resource.getFilename(), e);
            return List.of();
        }
    }
}
