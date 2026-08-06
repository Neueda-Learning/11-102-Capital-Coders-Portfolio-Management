package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Investment;
import com.portfolio.portfolio_management.model.InvestmentListItem;
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
                    rs.getDouble("quantity"),
                    rs.getDate("purchase_date") == null
                            ? null
                            : rs.getDate("purchase_date").toLocalDate()
            );

    private final RowMapper<InvestmentListItem> INVESTMENT_LIST_ROW_MAPPER = (rs, rowNum) ->
            new InvestmentListItem(
                    rs.getInt("investment_id"),
                    rs.getInt("portfolio_id"),
                    rs.getInt("asset_id"),
                    rs.getString("ticker_symbol"),
                    rs.getString("asset_type"),
                    rs.getDouble("amount_invested"),
                    rs.getDouble("current_value"),
                    rs.getDouble("quantity"),
                    rs.getDate("purchase_date") == null
                            ? null
                            : rs.getDate("purchase_date").toLocalDate()
            );

    public List<InvestmentListItem> getInvestmentsByPortfolioId(Integer portfolioId) {
        String sql = """
                SELECT i.investment_id,
                       i.portfolio_id,
                       i.asset_id,
                       a.ticker_symbol,
                       a.asset_type,
                       i.amount_invested,
                       i.current_value,
                       i.quantity,
                       i.purchase_date
                FROM investment i
                JOIN asset a ON a.asset_id = i.asset_id
                WHERE i.portfolio_id = ?
                ORDER BY i.investment_id
                """;
        return jdbcTemplate.query(sql, INVESTMENT_LIST_ROW_MAPPER, portfolioId);
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

    public Optional<Investment> getInvestmentByPortfolioIdAndAssetId(Integer portfolioId, Integer assetId) {
        String sql = "SELECT * FROM investment WHERE portfolio_id = ? AND asset_id = ?";

        List<Investment> investments = jdbcTemplate.query(
                sql,
                INVESTMENT_ROW_MAPPER,
                portfolioId,
                assetId
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
                 quantity,
                 purchase_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                investment.portfolioId(),
                investment.assetId(),
                investment.amountInvested(),
                investment.currentValue(),
                investment.quantity(),
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
                    quantity=?,
                    purchase_date=?
                WHERE investment_id=? AND portfolio_id=?
                """;

        jdbcTemplate.update(
                sql,
                investment.assetId(),
                investment.amountInvested(),
                investment.currentValue(),
                investment.quantity(),
                investment.purchaseDate(),
                investmentId,
                portfolioId
        );

        return getInvestmentByIdAndPortfolioId(investmentId, portfolioId)
                .orElseThrow(() -> new RuntimeException("Investment not found"));
    }

    public void deleteInvestment(Integer investmentId, Integer portfolioId) {
        String deleteTransactionsSql = "DELETE FROM transaction_history WHERE investment_id = ?";
        jdbcTemplate.update(deleteTransactionsSql, investmentId);

        String deleteInvestmentSql = "DELETE FROM investment WHERE investment_id = ? AND portfolio_id = ?";
        jdbcTemplate.update(deleteInvestmentSql, investmentId, portfolioId);
    }
}
