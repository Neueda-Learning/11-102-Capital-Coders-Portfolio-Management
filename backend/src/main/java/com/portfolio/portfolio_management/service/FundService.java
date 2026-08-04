package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Fund;

import java.util.Optional;

public interface FundService {
    Optional<Fund> getFundById(int fundId);

    Fund addnewFund(Fund fund);

    Fund updateFund(Fund fund);

    void deleteFund(int fundId);
}
