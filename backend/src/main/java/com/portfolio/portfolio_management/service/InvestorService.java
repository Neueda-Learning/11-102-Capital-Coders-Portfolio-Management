package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Investor;

import java.util.List;

public interface InvestorService {

    List<Investor> getAllInvestors();

    Investor getInvestorById(Integer investorId);

    Investor addInvestor(Investor investor);

    Investor updateInvestor(Integer investorId, Investor investor);

    void deleteInvestor(Integer investorId);
}
