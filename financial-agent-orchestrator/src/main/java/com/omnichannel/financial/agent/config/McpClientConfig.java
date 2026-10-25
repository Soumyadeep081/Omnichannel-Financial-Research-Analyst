package com.omnichannel.financial.agent.config;

import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
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
 * remote MCP tools for easy binding to the ChatClient without circular dependency warnings.
 */
@Configuration
public class McpClientConfig {

    private static final Logger log = LoggerFactory.getLogger(McpClientConfig.class);

    @Bean
    public ToolCallbackProvider mcpRemoteToolCallbackProvider(
            ObjectProvider<List<SyncMcpToolCallbackProvider>> syncMcpToolCallbackProviders,
            ObjectProvider<List<McpSyncClient>> mcpClients) {

        return () -> {
            List<ToolCallback> callbacks = new ArrayList<>();

            // 1. Collect from auto-configured SyncMcpToolCallbackProviders provided by spring-ai-mcp-client
            List<SyncMcpToolCallbackProvider> providers = syncMcpToolCallbackProviders.getIfAvailable();
            if (providers != null) {
                for (SyncMcpToolCallbackProvider provider : providers) {
                    Collections.addAll(callbacks, provider.getToolCallbacks());
                }
            }

            // 2. Fallback to programmatic client wrapping if clients exist but provider was not auto-configured
            if (callbacks.isEmpty()) {
                List<McpSyncClient> clients = mcpClients.getIfAvailable();
                if (clients != null && !clients.isEmpty()) {
                    try {
                        SyncMcpToolCallbackProvider fallbackProvider = new SyncMcpToolCallbackProvider(clients);
                        Collections.addAll(callbacks, fallbackProvider.getToolCallbacks());
                    } catch (Exception e) {
                        log.warn("Could not aggregate tools from McpSyncClients: {}", e.getMessage());
                    }
                }
            }

            log.info("Aggregated {} remote MCP tools from Model Context Protocol connections.", callbacks.size());
            return callbacks.toArray(new ToolCallback[0]);
        };
    }
}
