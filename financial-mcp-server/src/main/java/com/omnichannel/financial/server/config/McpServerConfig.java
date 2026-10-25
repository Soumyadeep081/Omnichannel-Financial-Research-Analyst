package com.omnichannel.financial.server.config;

import com.omnichannel.financial.server.tools.DatabaseFinancialTools;
import com.omnichannel.financial.server.tools.WebSearchTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI MCP Server Configuration.
 * Registers the Database Financial Tools and Web Search Tools with Spring AI's
 * ToolCallbackProvider, making them discoverable by the Model Context Protocol (MCP) WebMVC transport.
 */
@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider financialToolCallbackProvider(
            DatabaseFinancialTools databaseFinancialTools,
            WebSearchTools webSearchTools) {
        
        return MethodToolCallbackProvider.builder()
                .toolObjects(databaseFinancialTools, webSearchTools)
                .build();
    }
}
