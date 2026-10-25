package com.omnichannel.financial.server.dto;

import java.time.Instant;
import java.util.List;

/**
 * Structured response for breaking financial market news and sentiment.
 */
public record NewsSearchResultDto(
    String query,
    String targetTicker,
    Instant timestamp,
    List<NewsItem> articles,
    String overallMarketSentiment
) {
    public record NewsItem(
        String title,
        String source,
        String publishedDate,
        String snippet,
        String url
    ) {}
}
