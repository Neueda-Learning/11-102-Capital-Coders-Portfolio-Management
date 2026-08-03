package com.example.PortfolioManagement.service;

import com.example.PortfolioManagement.entity.Portfolio;
import com.example.PortfolioManagement.exception.PortfolioNotFoundException;
import com.example.PortfolioManagement.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PorfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PorfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public List<Portfolio> viewAllPortfolios() {
        return portfolioRepository.getAllPortfolios();
    }

    @Override
    public Optional<Portfolio> viewPortfolioById(Long portfolioId) {
        // Throw if missing, otherwise return as Optional
        return Optional.of(
                portfolioRepository.getPortfolioById(portfolioId)
                        .orElseThrow(() -> new PortfolioNotFoundException(portfolioId))
        );
    }

    @Override
    public Portfolio addPortfolio(Portfolio portfolio) {
        Portfolio existingPortfolio = portfolioRepository.getPortfolioById(portfolio.getPortfolioId()).orElse(null);
        if (existingPortfolio != null) {
            throw new IllegalArgumentException("Portfolio with ID " + portfolio.getPortfolioId() + " already exists.");
        }
        return portfolioRepository.addPortfolio(portfolio);
    }

    @Override
    public void updatePortfolio(Long portfolioId) {

    }

    @Override
    public void deletePortfolio(Long portfolioId) {

    }
}
