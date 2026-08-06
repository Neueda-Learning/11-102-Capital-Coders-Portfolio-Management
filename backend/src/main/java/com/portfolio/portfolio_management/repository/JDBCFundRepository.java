package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.model.FundSummary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JDBCFundRepository implements FundRepository {

    private final JdbcTemplate jdbc;


    public JDBCFundRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    // Converts database row into Fund object
    private final RowMapper<Fund> fundRowMapper = (rs, rowNum) -> new Fund(
            rs.getInt("fund_id"),
            rs.getInt("investor_id"),
            rs.getString("fund_name"),
            rs.getDouble("amount_received"),
            rs.getDate("received_date").toLocalDate(),
            rs.getString("status")
    );


    // Get one fund using fund_id
    @Override
    public Optional<Fund> getFundById(int fundId) {

        List<Fund> funds = jdbc.query(
                "SELECT * FROM fund WHERE fund_id = ?",
                fundRowMapper,
                fundId
        );

        return funds.isEmpty()
                ? Optional.empty()
                : Optional.of(funds.get(0));
    }


    // Check duplicate fund name
    @Override
    public List<Fund> getFundsByInvestorId(int investorId) {
        return jdbc.query(
                "SELECT * FROM fund WHERE investor_id = ? ORDER BY received_date DESC, fund_id DESC",
                fundRowMapper,
                investorId
        );
    }

    @Override
    public Optional<Fund> getFundByName(String fundName) {

        List<Fund> funds = jdbc.query(
                "SELECT * FROM fund WHERE fund_name = ?",
                fundRowMapper,
                fundName
        );

        return funds.isEmpty()
                ? Optional.empty()
                : Optional.of(funds.get(0));
    }


    // Add new funding round
    @Override
    public Fund addnewFund(Fund fund) {

        KeyHolder keyHolder = new GeneratedKeyHolder();


        jdbc.update(connection -> {

            var ps = connection.prepareStatement(
                    """
                    INSERT INTO fund
                    (investor_id, fund_name, amount_received, received_date, status)
                    VALUES (?, ?, ?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );


            ps.setInt(1, fund.investorId());
            ps.setString(2, fund.fundName());
            ps.setDouble(3, fund.amountReceived());
            ps.setDate(
                    4,
                    java.sql.Date.valueOf(fund.receivedDate())
            );
            ps.setString(5, fund.status());


            return ps;

        }, keyHolder);


        int generatedId = keyHolder
                .getKey()
                .intValue();


        return new Fund(
                generatedId,
                fund.investorId(),
                fund.fundName(),
                fund.amountReceived(),
                fund.receivedDate(),
                fund.status()
        );
    }


    // Update one funding round
    @Override
    public Fund updateFund(Fund fund) {

        jdbc.update(
                """
                UPDATE fund
                SET investor_id = ?,
                    fund_name = ?,
                    amount_received = ?,
                    received_date = ?,
                    status = ?
                WHERE fund_id = ?
                """,

                fund.investorId(),
                fund.fundName(),
                fund.amountReceived(),
                java.sql.Date.valueOf(fund.receivedDate()),
                fund.status(),
                fund.fundId()
        );


        return fund;
    }


    // Delete one funding round
    @Override
    public void deleteFund(int fundId) {

        jdbc.update(
                "DELETE FROM fund WHERE fund_id = ?",
                fundId
        );
    }


<<<<<<< Updated upstream
=======
    // Get all funding rounds of an investor
//    @Override
//    public List<Fund> getFundsByInvestorId(int investorId) {
//
//        return jdbc.query(
//                """
//                SELECT *
//                FROM fund
//                WHERE investor_id = ?
//                """,
//                fundRowMapper,
//                investorId
//        );
//    }


>>>>>>> Stashed changes
    // Get total money received from an investor
    @Override
    public FundSummary getTotalFundsByInvestorId(int investorId) {

        return jdbc.queryForObject(
                """
                SELECT investor_id,
                       SUM(amount_received) AS total_funds_received
                FROM fund
                WHERE investor_id = ?
                GROUP BY investor_id
                """,

                (rs, rowNum) -> new FundSummary(
                        rs.getInt("investor_id"),
                        rs.getDouble("total_funds_received")
                ),

                investorId
        );
    }
}