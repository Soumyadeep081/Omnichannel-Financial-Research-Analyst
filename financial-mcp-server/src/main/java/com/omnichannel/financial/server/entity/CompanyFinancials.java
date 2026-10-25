package com.omnichannel.financial.server.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA Entity representing corporate financial metrics stored in MySQL.
 */
@Entity
@Table(name = "company_financials", indexes = {
    @Index(name = "idx_ticker_period", columnList = "ticker, fiscalYear, fiscalQuarter")
})
public class CompanyFinancials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String ticker;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false)
    private Integer fiscalYear;

    @Column(nullable = false, length = 5)
    private String fiscalQuarter; // e.g., "Q1", "Q2", "Q3", "Q4"

    @Column(precision = 19, scale = 2)
    private BigDecimal revenue; // in USD (billions or exact)

    @Column(precision = 19, scale = 2)
    private BigDecimal netIncome;

    @Column(precision = 10, scale = 2)
    private BigDecimal eps; // Earnings Per Share

    @Column(name = "pe_ratio")
    private Double peRatio;

    @Column(precision = 19, scale = 2)
    private BigDecimal freeCashFlow;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalDebt;

    @Column(precision = 19, scale = 2)
    private BigDecimal cashAndEquivalents;

    @Column(precision = 19, scale = 2)
    private BigDecimal marketCap;

    private Double operatingMargin; // percentage, e.g. 30.2

    private Double dividendYield; // percentage, e.g. 0.45

    private LocalDate reportDate;

    public CompanyFinancials() {
    }

    public CompanyFinancials(Long id, String ticker, String companyName, Integer fiscalYear, String fiscalQuarter,
                             BigDecimal revenue, BigDecimal netIncome, BigDecimal eps, Double peRatio,
                             BigDecimal freeCashFlow, BigDecimal totalDebt, BigDecimal cashAndEquivalents,
                             BigDecimal marketCap, Double operatingMargin, Double dividendYield, LocalDate reportDate) {
        this.id = id;
        this.ticker = ticker;
        this.companyName = companyName;
        this.fiscalYear = fiscalYear;
        this.fiscalQuarter = fiscalQuarter;
        this.revenue = revenue;
        this.netIncome = netIncome;
        this.eps = eps;
        this.peRatio = peRatio;
        this.freeCashFlow = freeCashFlow;
        this.totalDebt = totalDebt;
        this.cashAndEquivalents = cashAndEquivalents;
        this.marketCap = marketCap;
        this.operatingMargin = operatingMargin;
        this.dividendYield = dividendYield;
        this.reportDate = reportDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker != null ? ticker.toUpperCase() : null;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getFiscalQuarter() {
        return fiscalQuarter;
    }

    public void setFiscalQuarter(String fiscalQuarter) {
        this.fiscalQuarter = fiscalQuarter;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(BigDecimal netIncome) {
        this.netIncome = netIncome;
    }

    public BigDecimal getEps() {
        return eps;
    }

    public void setEps(BigDecimal eps) {
        this.eps = eps;
    }

    public Double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(Double peRatio) {
        this.peRatio = peRatio;
    }

    public BigDecimal getFreeCashFlow() {
        return freeCashFlow;
    }

    public void setFreeCashFlow(BigDecimal freeCashFlow) {
        this.freeCashFlow = freeCashFlow;
    }

    public BigDecimal getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(BigDecimal totalDebt) {
        this.totalDebt = totalDebt;
    }

    public BigDecimal getCashAndEquivalents() {
        return cashAndEquivalents;
    }

    public void setCashAndEquivalents(BigDecimal cashAndEquivalents) {
        this.cashAndEquivalents = cashAndEquivalents;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public Double getOperatingMargin() {
        return operatingMargin;
    }

    public void setOperatingMargin(Double operatingMargin) {
        this.operatingMargin = operatingMargin;
    }

    public Double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(Double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    @Override
    public String toString() {
        return "CompanyFinancials{" +
                "ticker='" + ticker + '\'' +
                ", period=" + fiscalYear + " " + fiscalQuarter +
                ", revenue=" + revenue +
                ", eps=" + eps +
                ", peRatio=" + peRatio +
                ", netIncome=" + netIncome +
                '}';
    }
}
