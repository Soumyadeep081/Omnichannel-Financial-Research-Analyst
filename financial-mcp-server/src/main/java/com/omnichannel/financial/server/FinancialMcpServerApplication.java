package com.omnichannel.financial.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class FinancialMcpServerApplication {

    private static final Logger log = LoggerFactory.getLogger(FinancialMcpServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FinancialMcpServerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8081");
        String ssePath = env.getProperty("spring.ai.mcp.server.webmvc.sse-endpoint", "/mcp/sse");
        String msgPath = env.getProperty("spring.ai.mcp.server.webmvc.message-endpoint", "/mcp/message");

        log.info("=================================================================");
        log.info("  Financial Database MCP Server is LIVE on port {}", port);
        log.info("  SSE Connection Endpoint: http://localhost:{}{}", port, ssePath);
        log.info("  MCP Message Endpoint:   http://localhost:{}{}", port, msgPath);
        log.info("  Registered Tools: getCompanyFinancials, compareTickers, searchFinancialNews");
        log.info("=================================================================");
    }
}
