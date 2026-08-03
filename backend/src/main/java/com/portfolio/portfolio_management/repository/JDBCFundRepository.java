package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Fund;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.Optional;

@Repository
public class JDBCFundRepository implements FundRepository {
    private final JdbcTemplate jdbc;

    public JDBCFundRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Fund> fundRowMapper = (rs, rowNum) -> new Fund(
            rs.getInt("fund_id"),
            rs.getInt("investor_id"),
            rs.getString("fund_name"),
            rs.getDouble("amount_received"),
            rs.getDate("received_date").toLocalDate(),
            rs.getString("status")
    );

    @Override
    public Optional<Fund> getFundById(int fundId) {
        var results = jdbc.query(
                "SELECT * FROM fund WHERE fund_id = ?",
                fundRowMapper,
                fundId // bound to first ?
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Fund> getFundByName(String fundName) {
        var results = jdbc.query(
                "SELECT * FROM fund WHERE fund_name = ?",
                fundRowMapper,
                fundName // bound to first ?
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Fund addnewFund(Fund fund) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, fund.investorId());
            ps.setString(2, fund.fundName());
            ps.setDouble(3, fund.amountReceived());
            ps.setDate(4, java.sql.Date.valueOf(fund.receivedDate()));
            ps.setString(5, fund.status());
            return ps;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        return new Fund(id, fund.investorId(), fund.fundName(), fund.amountReceived(), fund.receivedDate(), fund.status());
    }

    @Override
    public Fund updateFund(Fund fund) {
        jdbc.update(
                "UPDATE fund SET investor_id = ?, fund_name = ?, amount_received = ?, received_date = ?, status = ? WHERE fund_id = ?",
                fund.investorId(),
                fund.fundName(),
                fund.amountReceived(),
                java.sql.Date.valueOf(fund.receivedDate()),
                fund.status(),
                fund.fundId()
        );
        return fund;
    }

    @Override
    public void deleteFund(int fundId) {
        jdbc.update(
                "DELETE FROM fund WHERE fund_id = ?",
                fundId
        );
    }
}
