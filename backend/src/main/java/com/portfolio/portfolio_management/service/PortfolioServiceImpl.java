package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.getAllPortfolios();
    }

    @Override
    public Portfolio getPortfolioById(Integer portfolioId) {
        return portfolioRepository
                .getPortfolioById(portfolioId)
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio with ID " + portfolioId + " not found"));
    }

    @Override
    public Portfolio addPortfolio(Portfolio portfolio) {
        return portfolioRepository.addPortfolio(portfolio);
    }

    @Override
    public Portfolio updatePortfolio(Integer portfolioId, Portfolio portfolio) {

        portfolioRepository.getPortfolioById(portfolioId)
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio with ID " + portfolioId + " not found"));

        return portfolioRepository.updatePortfolio(portfolioId, portfolio);
    }

    @Override
    public void deletePortfolio(Integer portfolioId) {

        portfolioRepository.getPortfolioById(portfolioId)
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio with ID " + portfolioId + " not found"));

        portfolioRepository.deletePortfolio(portfolioId);
    }

    @Override
    public List<Portfolio> getPortfoliosByEmployeeId(Integer employeeId) {
        return portfolioRepository.getPortfoliosByEmployeeId(employeeId);
    }

//    @Override
//    public List<Portfolio> getPortfoliosByFundId(Integer fundId) {
//        return portfolioRepository.getPortfoliosByFundId(fundId);
//    }
}