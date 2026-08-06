package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.model.FundSummary;

import java.util.List;
import java.util.Optional;

public interface FundRepository {

    Optional<Fund> getFundById(int fundId);

    List<Fund> getFundsByInvestorId(int investorId);

    Optional<Fund> getFundByName(String fundName);

    Fund addnewFund(Fund fund);

    Fund updateFund(Fund fund);

    void deleteFund(int fundId);


    // Gives only total amount received by investor
    FundSummary getTotalFundsByInvestorId(int investorId);
}