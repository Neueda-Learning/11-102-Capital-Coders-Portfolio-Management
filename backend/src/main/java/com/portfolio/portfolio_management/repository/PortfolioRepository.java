package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.model.PortfolioSummary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PortfolioRepository {

    private final JdbcTemplate jdbcTemplate;

    public PortfolioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Portfolio> PORTFOLIO_ROW_MAPPER = (rs, rowNum) ->
            new Portfolio(
                    rs.getInt("portfolio_id"),
                    rs.getInt("employee_id"),
                    rs.getInt("investor_id"),
                    rs.getString("portfolio_name"),
                    rs.getString("description"),
                    rs.getString("risk_level"),
                    rs.getDouble("allocated_amount"),
                    rs.getDate("created_date").toLocalDate()
            );

    public List<Portfolio> getAllPortfolios() {
        String sql = "SELECT * FROM portfolio";
        return jdbcTemplate.query(sql, PORTFOLIO_ROW_MAPPER);
    }

    public Optional<Portfolio> getPortfolioById(Integer portfolioId) {

        String sql = "SELECT * FROM portfolio WHERE portfolio_id = ?";

        List<Portfolio> portfolios =
                jdbcTemplate.query(sql, PORTFOLIO_ROW_MAPPER, portfolioId);

        return portfolios.stream().findFirst();
    }

    public Portfolio addPortfolio(Portfolio portfolio) {

        String sql = """
                INSERT INTO portfolio
                (employee_id,
                 investor_id,
                 portfolio_name,
                 description,
                 risk_level,
                 allocated_amount,
                 created_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                portfolio.empId(),
                portfolio.investorId(),
                portfolio.portfolioName(),
                portfolio.portfolioDescription(),
                portfolio.riskLevel(),
                portfolio.allocatedAmount(),
                portfolio.createdDate()
        );

        Integer id = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );

        return getPortfolioById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not created"));
    }

    public Portfolio updatePortfolio(Integer portfolioId, Portfolio portfolio) {

        String sql = """
                UPDATE portfolio
                SET employee_id=?,
                    investor_id=?,
                    portfolio_name=?,
                    description=?,
                    risk_level=?,
                    allocated_amount=?,
                    created_date=?
                WHERE portfolio_id=?
                """;

        jdbcTemplate.update(
                sql,
                portfolio.empId(),
                portfolio.investorId(),
                portfolio.portfolioName(),
                portfolio.portfolioDescription(),
                portfolio.riskLevel(),
                portfolio.allocatedAmount(),
                portfolio.createdDate(),
                portfolioId
        );

        return getPortfolioById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    }

    public void deletePortfolio(Integer portfolioId) {

        String sql = "DELETE FROM portfolio WHERE portfolio_id = ?";

        jdbcTemplate.update(sql, portfolioId);
    }

    public List<Portfolio> getPortfoliosByEmployeeId(Integer employeeId) {

        String sql =
                "SELECT * FROM portfolio WHERE employee_id = ?";

        return jdbcTemplate.query(
                sql,
                PORTFOLIO_ROW_MAPPER,
                employeeId
        );
    }

    public boolean existsByInvestorId(Integer investorId) {

        String sql =
                "SELECT COUNT(*) FROM portfolio WHERE investor_id = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                investorId
        );

        return count != null && count > 0;
    }

    public Optional<Integer> getInvestorIdByPortfolioId(Integer portfolioId) {

        String sql = "SELECT investor_id FROM portfolio WHERE portfolio_id = ?";

        List<Integer> investorIds = jdbcTemplate.queryForList(
                sql,
                Integer.class,
                portfolioId
        );

        return investorIds.stream().findFirst();
    }

    public Optional<PortfolioSummary> getPortfolioSummary(Integer portfolioId) {
        String sql = """
                SELECT
                    COALESCE(i_summary.stocks_invested, 0) AS stocks_invested,
                    COALESCE(i_summary.bonds_invested, 0) AS bonds_invested,
                    COALESCE(i_summary.mutual_funds_invested, 0) AS mutual_funds_invested,
                    COALESCE(i_summary.cash_invested, 0) AS cash_invested,
                    COALESCE(f_summary.total_funds, 0) AS total_funds,
                    COALESCE(i_summary.used_funds, 0) AS used_funds,
                    COALESCE(i_summary.gain_loss, 0) AS gain_loss
                FROM portfolio p
                LEFT JOIN (
                    SELECT
                        f.investor_id,
                        SUM(f.amount_received) AS total_funds
                    FROM fund f
                    GROUP BY f.investor_id
                ) f_summary ON f_summary.investor_id = p.investor_id
                LEFT JOIN (
                    SELECT
                        i.portfolio_id,
                        SUM(CASE WHEN LOWER(a.asset_type) = 'stocks' THEN i.amount_invested ELSE 0 END) AS stocks_invested,
                        SUM(CASE WHEN LOWER(a.asset_type) = 'bonds' THEN i.amount_invested ELSE 0 END) AS bonds_invested,
                        SUM(CASE WHEN LOWER(a.asset_type) = 'mutual funds' THEN i.amount_invested ELSE 0 END) AS mutual_funds_invested,
                        SUM(CASE WHEN LOWER(a.asset_type) = 'cash' THEN i.amount_invested ELSE 0 END) AS cash_invested,
                        SUM(i.amount_invested) AS used_funds,
                        SUM(i.current_value - i.amount_invested) AS gain_loss
                    FROM investment i
                    LEFT JOIN asset a ON a.asset_id = i.asset_id
                    GROUP BY i.portfolio_id
                ) i_summary ON i_summary.portfolio_id = p.portfolio_id
                WHERE p.portfolio_id = ?
                """;

        List<PortfolioSummary> summaries = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new PortfolioSummary(
                        rs.getDouble("stocks_invested"),
                        rs.getDouble("bonds_invested"),
                        rs.getDouble("mutual_funds_invested"),
                        rs.getDouble("cash_invested"),
                        rs.getDouble("total_funds"),
                        rs.getDouble("used_funds"),
                        0,
                        rs.getDouble("gain_loss"),
                        0,
                        "Neutral"
                ),
                portfolioId
        );

        return summaries.stream().findFirst();
    }

//    public List<Portfolio> getPortfoliosByFundId(Integer fundId) {
//
//        String sql =
//                "SELECT * FROM portfolio WHERE fund_id = ?";
//
//        return jdbcTemplate.query(
//                sql,
//                PORTFOLIO_ROW_MAPPER,
//                fundId
//        );
//    }
}