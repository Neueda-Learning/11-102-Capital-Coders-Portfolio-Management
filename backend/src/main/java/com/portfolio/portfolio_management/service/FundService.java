package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.model.FundSummary;

import java.util.List;

public interface FundService {

    // Fetch all funds of an investor (optional: for history)
    List<Fund> getFundsByInvestorId(int investorId);


    // Fetch total funds received by an investor
    FundSummary getTotalFundsByInvestorId(int investorId);


    // Add new funding round
    Fund addnewFund(Fund fund);


    // Update a particular fund
    Fund updateFund(Fund fund);


    // Delete a particular fund
    void deleteFund(int fundId);
}