package com.omnichannel.financial.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class FinancialAgentApplication {

    private static final Logger log = LoggerFactory.getLogger(FinancialAgentApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FinancialAgentApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String mcpServerUrl = env.getProperty("spring.ai.mcp.client.sse.connections.financial-db-server.url", "http://localhost:8081");

        log.info("=================================================================");
        log.info("  Financial Agent Orchestrator is LIVE on port {}", port);
        log.info("  Connected to Database MCP Server at: {}", mcpServerUrl);
        log.info("  RAG Pipeline Ingestion: Ready (classpath:docs/*)");
        log.info("  Generate Memo Endpoint: POST http://localhost:{}/api/v1/analyst/memo", port);
        log.info("  Quick Test Endpoint:    GET  http://localhost:{}/api/v1/analyst/memo/AAPL", port);
        log.info("=================================================================");
    }
}
