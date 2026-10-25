-- DDL for Corporate Financials Database
CREATE TABLE IF NOT EXISTS company_financials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticker VARCHAR(10) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    fiscal_year INT NOT NULL,
    fiscal_quarter VARCHAR(5) NOT NULL,
    revenue DECIMAL(19, 2),
    net_income DECIMAL(19, 2),
    eps DECIMAL(10, 2),
    pe_ratio DOUBLE,
    free_cash_flow DECIMAL(19, 2),
    total_debt DECIMAL(19, 2),
    cash_and_equivalents DECIMAL(19, 2),
    market_cap DECIMAL(19, 2),
    operating_margin DOUBLE,
    dividend_yield DOUBLE,
    report_date DATE
);

CREATE INDEX IF NOT EXISTS idx_cf_ticker_period ON company_financials(ticker, fiscal_year, fiscal_quarter);
