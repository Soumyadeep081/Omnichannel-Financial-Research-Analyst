package com.omnichannel.financial.agent.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.client.McpSyncClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Spring AI MCP Client Configuration.
 * Coordinates with the spring-ai-mcp-client starter which connects via HTTP+SSE
 * to the remote Database MCP Server at http://localhost:8081/mcp/sse.
 *
 * Exposes a consolidated ToolCallbackProvider containing all dynamically discovered
 * remote MCP tools for easy binding to the ChatClient.
 */
@Configuration
public class McpClientConfig {

    private static final Logger log = LoggerFactory.getLogger(McpClientConfig.class);

    @Bean
    public ToolCallbackProvider mcpRemoteToolCallbackProvider(
            ObjectProvider<List<ToolCallbackProvider>> toolCallbackProviders,
            ObjectProvider<List<McpSyncClient>> mcpClients) {

        return () -> {
            List<ToolCallback> callbacks = new ArrayList<>();

            // 1. Collect from auto-configured ToolCallbackProviders provided by spring-ai-mcp-client
            List<ToolCallbackProvider> providers = toolCallbackProviders.getIfAvailable();
            if (providers != null) {
                for (ToolCallbackProvider provider : providers) {
                    Collections.addAll(callbacks, provider.getToolCallbacks());
                }
            }

            log.info("Aggregated {} remote MCP tools from Model Context Protocol connections.", callbacks.size());
            return callbacks.toArray(new ToolCallback[0]);
        };
    }
}
