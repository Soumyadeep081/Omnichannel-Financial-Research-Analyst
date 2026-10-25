package com.omnichannel.financial.server.repository;

import com.omnichannel.financial.server.entity.CompanyFinancials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyFinancialsRepository extends JpaRepository<CompanyFinancials, Long> {

    List<CompanyFinancials> findByTickerIgnoreCaseOrderByFiscalYearDescFiscalQuarterDesc(String ticker);

    Optional<CompanyFinancials> findTop1ByTickerIgnoreCaseOrderByFiscalYearDescFiscalQuarterDesc(String ticker);

    Optional<CompanyFinancials> findByTickerIgnoreCaseAndFiscalYearAndFiscalQuarter(String ticker, Integer fiscalYear, String fiscalQuarter);

    List<CompanyFinancials> findByTickerIgnoreCaseAndFiscalYearOrderByFiscalQuarterAsc(String ticker, Integer fiscalYear);

    @Query("SELECT f FROM CompanyFinancials f WHERE UPPER(f.ticker) IN :tickers " +
           "AND f.id IN (SELECT MAX(f2.id) FROM CompanyFinancials f2 WHERE UPPER(f2.ticker) IN :tickers GROUP BY f2.ticker)")
    List<CompanyFinancials> findLatestForTickers(@Param("tickers") List<String> tickers);
}
