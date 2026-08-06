package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.TransactionHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TransactionHistory> TRANSACTION_ROW_MAPPER = (rs, rowNum) ->
            new TransactionHistory(
                    rs.getInt("transaction_id"),
                    rs.getInt("investment_id"),
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getDouble("quantity"),
                    rs.getDouble("price_per_unit"),
                    rs.getDate("transaction_date") == null
                            ? null
                            : rs.getDate("transaction_date").toLocalDate()
            );

    public TransactionHistory addTransaction(TransactionHistory transaction) {
        String sql = """
                INSERT INTO transaction_history
                (investment_id,
                 transaction_type,
                 amount,
                 quantity,
                 price_per_unit,
                 transaction_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                transaction.investmentId(),
                transaction.transactionType(),
                transaction.transactionAmount(),
                transaction.quantity(),
                transaction.pricePerUnit(),
                transaction.transactionDate()
        );

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        return getById(id);
    }

    public TransactionHistory getById(Integer transactionId) {
        String sql = "SELECT * FROM transaction_history WHERE transaction_id = ?";
        return jdbcTemplate.queryForObject(sql, TRANSACTION_ROW_MAPPER, transactionId);
    }

    public List<TransactionHistory> getByInvestmentId(Integer investmentId) {
        String sql = "SELECT * FROM transaction_history WHERE investment_id = ? ORDER BY transaction_date DESC, transaction_id DESC";
        return jdbcTemplate.query(sql, TRANSACTION_ROW_MAPPER, investmentId);
    }
}
