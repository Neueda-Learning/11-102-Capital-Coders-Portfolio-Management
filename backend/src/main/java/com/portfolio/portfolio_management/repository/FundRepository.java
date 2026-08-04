package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Fund;

import java.util.Optional;

public interface FundRepository {
    Optional<Fund> getFundById(int fundId);

    Optional<Fund> getFundByName(String fundName);

    Fund addnewFund(Fund fund);

    Fund updateFund(Fund fund);

    void deleteFund(int fundId);
}
