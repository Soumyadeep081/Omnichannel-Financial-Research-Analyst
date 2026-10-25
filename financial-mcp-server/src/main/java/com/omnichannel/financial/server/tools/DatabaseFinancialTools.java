package com.omnichannel.financial.server.tools;

import com.omnichannel.financial.server.dto.FinancialMetricsDto;
import com.omnichannel.financial.server.entity.CompanyFinancials;
import com.omnichannel.financial.server.repository.CompanyFinancialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP-compliant Tool Provider exposing read-only MySQL financial metrics
 * to Spring AI Model Context Protocol clients.
 */
@Service
public class DatabaseFinancialTools {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFinancialTools.class);
    private final CompanyFinancialsRepository repository;

    public DatabaseFinancialTools(CompanyFinancialsRepository repository) {
        this.repository = repository;
    }

    /**
     * MCP Tool: Retrieve latest reported financial metrics and quarterly performance for a ticker.
     */
    @Tool(description = "Retrieve the latest quantitative corporate financials (Revenue, EPS, Net Income, P/E ratio, FCF, Debt, Margins) for a stock ticker symbol (e.g., AAPL, MSFT, NVDA) from the MySQL database.")
    @Transactional(readOnly = true)
    public FinancialMetricsDto getCompanyFinancials(
            @ToolParam(description = "The stock ticker symbol in uppercase, for example: AAPL, MSFT, NVDA, GOOGL") 
            String ticker) {

        log.info("[MCP Tool] Executing getCompanyFinancials for ticker: {}", ticker);
        if (ticker == null || ticker.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticker cannot be null or empty");
        }

        String cleanTicker = ticker.trim().toUpperCase();
        List<CompanyFinancials> history = repository.findByTickerIgnoreCaseOrderByFiscalYearDescFiscalQuarterDesc(cleanTicker);

        if (history.isEmpty()) {
            log.warn("[MCP Tool] No financial records found in database for ticker: {}", cleanTicker);
            return new FinancialMetricsDto(
                    cleanTicker,
                    "Unknown Entity (No record in MySQL)",
                    "N/A",
                    0,
                    null, null, null, null, null, null, null, null, null, null, null,
                    Collections.emptyList()
            );
        }

        CompanyFinancials latest = history.get(0);

        List<FinancialMetricsDto.QuarterlySummary> quarterlySummaries = history.stream()
                .limit(4)
                .map(q -> new FinancialMetricsDto.QuarterlySummary(
                        q.getFiscalQuarter(),
                        q.getFiscalYear(),
                        q.getRevenue(),
                        q.getNetIncome(),
                        q.getEps()
                ))
                .collect(Collectors.toList());

        return new FinancialMetricsDto(
                latest.getTicker(),
                latest.getCompanyName(),
                latest.getFiscalQuarter(),
                latest.getFiscalYear(),
                latest.getRevenue(),
                latest.getNetIncome(),
                latest.getEps(),
                latest.getPeRatio(),
                latest.getFreeCashFlow(),
                latest.getTotalDebt(),
                latest.getCashAndEquivalents(),
                latest.getMarketCap(),
                latest.getOperatingMargin(),
                latest.getDividendYield(),
                latest.getReportDate(),
                quarterlySummaries
        );
    }

    /**
     * MCP Tool: Compare key valuation multiples and metrics across multiple tickers.
     */
    @Tool(description = "Compare corporate financial valuation metrics (P/E ratio, Operating Margin, Revenue, Market Cap) across multiple stock tickers from MySQL.")
    @Transactional(readOnly = true)
    public List<CompanyFinancials> compareTickers(
            @ToolParam(description = "List of stock ticker symbols to compare, e.g. ['AAPL', 'MSFT']") 
            List<String> tickers) {

        log.info("[MCP Tool] Executing compareTickers for: {}", tickers);
        if (tickers == null || tickers.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> upperTickers = tickers.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        return repository.findLatestForTickers(upperTickers);
    }
}
