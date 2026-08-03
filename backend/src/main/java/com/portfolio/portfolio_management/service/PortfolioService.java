package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Portfolio;

import java.util.List;

public interface PortfolioService {

    List<Portfolio> getAllPortfolios();

    Portfolio getPortfolioById(Integer portfolioId);

    Portfolio addPortfolio(Portfolio portfolio);

    Portfolio updatePortfolio(Integer portfolioId, Portfolio portfolio);

    void deletePortfolio(Integer portfolioId);

    List<Portfolio> getPortfoliosByEmployeeId(Integer employeeId);

    List<Portfolio> getPortfoliosByFundId(Integer fundId);
}
