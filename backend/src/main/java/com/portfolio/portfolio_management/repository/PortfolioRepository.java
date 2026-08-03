package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Portfolio;
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