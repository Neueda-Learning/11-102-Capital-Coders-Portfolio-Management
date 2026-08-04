package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Investment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InvestmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public InvestmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Investment> INVESTMENT_ROW_MAPPER = (rs, rowNum) ->
            new Investment(
                    rs.getInt("investment_id"),
                    rs.getInt("portfolio_id"),
                    rs.getInt("asset_id"),
                    rs.getDouble("amount_invested"),
                    rs.getDouble("current_value"),
                    rs.getDate("purchase_date") == null
                            ? null
                            : rs.getDate("purchase_date").toLocalDate()
            );

    public List<Investment> getInvestmentsByPortfolioId(Integer portfolioId) {
        String sql = "SELECT * FROM investment WHERE portfolio_id = ?";
        return jdbcTemplate.query(sql, INVESTMENT_ROW_MAPPER, portfolioId);
    }

    public Optional<Investment> getInvestmentByIdAndPortfolioId(Integer investmentId,
                                                                 Integer portfolioId) {
        String sql = "SELECT * FROM investment WHERE investment_id = ? AND portfolio_id = ?";

        List<Investment> investments = jdbcTemplate.query(
                sql,
                INVESTMENT_ROW_MAPPER,
                investmentId,
                portfolioId
        );

        return investments.stream().findFirst();
    }

    public Investment addInvestment(Investment investment) {
        String sql = """
                INSERT INTO investment
                (portfolio_id,
                 asset_id,
                 amount_invested,
                 current_value,
                 purchase_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                investment.portfolioId(),
                investment.assetId(),
                investment.amountInvested(),
                investment.currentValue(),
                investment.purchaseDate()
        );

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        return getInvestmentByIdAndPortfolioId(id, investment.portfolioId())
                .orElseThrow(() -> new RuntimeException("Investment not created"));
    }

    public Investment updateInvestment(Integer investmentId,
                                       Integer portfolioId,
                                       Investment investment) {

        String sql = """
                UPDATE investment
                SET asset_id=?,
                    amount_invested=?,
                    current_value=?,
                    purchase_date=?
                WHERE investment_id=? AND portfolio_id=?
                """;

        jdbcTemplate.update(
                sql,
                investment.assetId(),
                investment.amountInvested(),
                investment.currentValue(),
                investment.purchaseDate(),
                investmentId,
                portfolioId
        );

        return getInvestmentByIdAndPortfolioId(investmentId, portfolioId)
                .orElseThrow(() -> new RuntimeException("Investment not found"));
    }

    public void deleteInvestment(Integer investmentId, Integer portfolioId) {
        String sql = "DELETE FROM investment WHERE investment_id = ? AND portfolio_id = ?";
        jdbcTemplate.update(sql, investmentId, portfolioId);
    }
}
