package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Investment;
import com.portfolio.portfolio_management.model.InvestmentListItem;

import java.util.List;

public interface InvestmentService {

    List<InvestmentListItem> getInvestmentsByPortfolioId(Integer portfolioId);

    Investment getInvestmentById(Integer portfolioId, Integer investmentId);

    Investment addInvestment(Integer portfolioId, Investment investment);

    Investment updateInvestment(Integer portfolioId, Integer investmentId, Investment investment);

    void deleteInvestment(Integer portfolioId, Integer investmentId);
}

