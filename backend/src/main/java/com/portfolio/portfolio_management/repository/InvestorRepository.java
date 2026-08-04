package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Investor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InvestorRepository {

    private final JdbcTemplate jdbcTemplate;

    public InvestorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Investor> INVESTOR_ROW_MAPPER = (rs, rowNum) ->
            new Investor(
                    rs.getInt("investor_id"),
                    rs.getString("investor_name"),
                    rs.getString("contact_email")
            );

    public List<Investor> getAllInvestors() {
        String sql = "SELECT * FROM investor";
        return jdbcTemplate.query(sql, INVESTOR_ROW_MAPPER);
    }

    public Optional<Investor> getInvestorById(Integer investorId) {
        String sql = "SELECT * FROM investor WHERE investor_id = ?";
        List<Investor> investors = jdbcTemplate.query(sql, INVESTOR_ROW_MAPPER, investorId);
        return investors.stream().findFirst();
    }

    public Optional<Investor> findByEmail(String investorEmail) {
        String sql = "SELECT * FROM investor WHERE contact_email = ?";
        List<Investor> investors = jdbcTemplate.query(sql, INVESTOR_ROW_MAPPER, investorEmail);
        return investors.stream().findFirst();
    }

    public Investor addInvestor(Investor investor) {
        String sql = """
                INSERT INTO investor
                (investor_name, contact_email)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                investor.investorName(),
                investor.investorEmail()
        );

        Integer id = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );

        return getInvestorById(id)
                .orElseThrow(() -> new RuntimeException("Investor not created"));
    }

    public Investor updateInvestor(Integer investorId, Investor investor) {
        String sql = """
                UPDATE investor
                SET investor_name = ?,
                    contact_email = ?
                WHERE investor_id = ?
                """;

        jdbcTemplate.update(
                sql,
                investor.investorName(),
                investor.investorEmail(),
                investorId
        );

        return getInvestorById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
    }

    public void deleteInvestor(Integer investorId) {
        String sql = "DELETE FROM investor WHERE investor_id = ?";
        jdbcTemplate.update(sql, investorId);
    }
}
