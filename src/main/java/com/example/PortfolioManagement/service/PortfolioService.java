package com.example.PortfolioManagement.service;

import com.example.PortfolioManagement.entity.Portfolio;

import java.util.List;
import java.util.Optional;

public interface PortfolioService {
    // view all portfolios
    List<Portfolio> viewAllPortfolios();
    // view portfolio by id
    Optional<Portfolio> viewPortfolioById(Long portfolioId);
    // add portfolio
    void addPortfolio();
    // update portfolio
    void updatePortfolio(Long portfolioId);
    // delete portfolio
    void deletePortfolio(Long portfolioId);
}
