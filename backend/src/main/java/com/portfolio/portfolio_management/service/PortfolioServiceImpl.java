package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.DuplicatePortfolioException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.model.PortfolioSummary;
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

        if (portfolioRepository.existsByInvestorId(portfolio.investorId())) {

            throw new DuplicatePortfolioException(
                    "Investor already has a portfolio"
            );
        }

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

    @Override
    public Integer getInvestorIdByPortfolioId(Integer portfolioId) {
        return portfolioRepository.getInvestorIdByPortfolioId(portfolioId)
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio with ID " + portfolioId + " not found"));
    }

    @Override
    public PortfolioSummary getPortfolioSummary(Integer portfolioId) {
        PortfolioSummary baseSummary = portfolioRepository.getPortfolioSummary(portfolioId)
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio with ID " + portfolioId + " not found"));

        double usedFunds = baseSummary.usedFunds();
        double totalFunds = baseSummary.totalFunds();
        double availableFunds = totalFunds - usedFunds;
        double gainLoss = baseSummary.gainLoss();
        double returnPercent = usedFunds == 0
                ? 0
                : (gainLoss / usedFunds) * 100;

        String gainLossLabel;
        if (gainLoss > 0) {
            gainLossLabel = "Gain";
        } else if (gainLoss < 0) {
            gainLossLabel = "Loss";
        } else {
            gainLossLabel = "Neutral";
        }

        return new PortfolioSummary(
                baseSummary.stocksInvested(),
                baseSummary.bondsInvested(),
                baseSummary.mutualFundsInvested(),
                baseSummary.cashInvested(),
                totalFunds,
                usedFunds,
                availableFunds,
                gainLoss,
                returnPercent,
                gainLossLabel
        );
    }

//    @Override
//    public List<Portfolio> getPortfoliosByFundId(Integer fundId) {
//        return portfolioRepository.getPortfoliosByFundId(fundId);
//    }
}