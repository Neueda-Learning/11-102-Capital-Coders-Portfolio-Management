package com.example.PortfolioManagement.repository;

import com.example.PortfolioManagement.entity.Portfolio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PortfolioRepository {

    private final JdbcTemplate jdbcTemplate;

    public PortfolioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Portfolio> PORTFOLIO_ROW_MAPPER = (rs, rowNum) -> {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(rs.getLong("portfolio_id"));
        portfolio.setPortfolioName(rs.getString("portfolio_name"));
        portfolio.setPortfolioDescription(rs.getString("portfolio_description"));
        portfolio.setRiskLevel(rs.getString("risk_level"));
        return portfolio;
    };

    // method to get all portfolios
    public List<Portfolio> getAllPortfolios() {
        String sql = "SELECT * FROM portfolios";
        return jdbcTemplate.query(sql, PORTFOLIO_ROW_MAPPER);
    }

    // method to get portfolio by id
    public Portfolio getPortfolioById(Long portfolioId) {
        String sql = "SELECT * FROM portfolios WHERE portfolio_id = ?";
        List<Portfolio> results = jdbcTemplate.query(sql, PORTFOLIO_ROW_MAPPER, portfolioId);
        return results.isEmpty() ? null : results.get(0);
    }

    // method to add portfolio
    public void addPortfolio(Portfolio portfolio) {
        String sql = "INSERT INTO portfolios (portfolio_name, portfolio_description, risk_level) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, portfolio.getPortfolioName(), portfolio.getPortfolioDescription(), portfolio.getRiskLevel());
    }

    // method to update portfolio
    public void updatePortfolio(Portfolio portfolio) {
        String sql = "UPDATE portfolios SET portfolio_name = ?, portfolio_description = ?, risk_level = ? WHERE portfolio_id = ?";
        jdbcTemplate.update(sql, portfolio.getPortfolioName(), portfolio.getPortfolioDescription(), portfolio.getRiskLevel(), portfolio.getPortfolioId());
    }

    // method to delete portfolio
    public void deletePortfolio(Long portfolioId) {
        String sql = "DELETE FROM portfolios WHERE portfolio_id = ?";
        jdbcTemplate.update(sql, portfolioId);
    }
}