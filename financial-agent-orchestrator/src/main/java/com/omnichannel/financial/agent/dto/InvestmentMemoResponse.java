package com.omnichannel.financial.agent.dto;

import java.time.Instant;

public record InvestmentMemoResponse(
    String ticker,
    String memoMarkdown,
    Instant generatedAt,
    String status
) {}
