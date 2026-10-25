package com.omnichannel.financial.server.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Structured response containing both the latest quarter financial metrics
 * and historical quarterly trend data for context.
 */
public record FinancialMetricsDto(
    String ticker,
    String companyName,
    String latestQuarter,
    Integer latestYear,
    BigDecimal revenueInBillions,
    BigDecimal netIncomeInBillions,
    BigDecimal eps,
    Double peRatio,
    BigDecimal freeCashFlowInBillions,
    BigDecimal totalDebtInBillions,
    BigDecimal cashAndEquivalentsInBillions,
    BigDecimal marketCapInBillions,
    Double operatingMarginPercent,
    Double dividendYieldPercent,
    LocalDate reportDate,
    List<QuarterlySummary> historicalQuarterlyTrend
) {
    public record QuarterlySummary(
        String quarter,
        Integer year,
        BigDecimal revenue,
        BigDecimal netIncome,
        BigDecimal eps
    ) {}
}
