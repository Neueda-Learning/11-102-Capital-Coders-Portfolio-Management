package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.InvestmentNotFoundException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Investment;
import com.portfolio.portfolio_management.repository.InvestmentRepository;
import com.portfolio.portfolio_management.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;

    public InvestmentServiceImpl(InvestmentRepository investmentRepository,
                                 PortfolioRepository portfolioRepository) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public List<Investment> getInvestmentsByPortfolioId(Integer portfolioId) {
        ensurePortfolioExists(portfolioId);
        return investmentRepository.getInvestmentsByPortfolioId(portfolioId);
    }

    @Override
    public Investment getInvestmentById(Integer portfolioId, Integer investmentId) {
        ensurePortfolioExists(portfolioId);

        return investmentRepository.getInvestmentByIdAndPortfolioId(investmentId, portfolioId)
                .orElseThrow(() ->
                        new InvestmentNotFoundException(
                                "Investment with ID " + investmentId
                                        + " not found in portfolio " + portfolioId));
    }

    @Override
    public Investment addInvestment(Integer portfolioId, Investment investment) {
        ensurePortfolioExists(portfolioId);
        return investmentRepository.addInvestment(investment);
    }

    @Override
    public Investment updateInvestment(Integer portfolioId,
                                       Integer investmentId,
                                       Investment investment) {

        ensurePortfolioExists(portfolioId);
        getInvestmentById(portfolioId, investmentId);

        return investmentRepository.updateInvestment(investmentId, portfolioId, investment);
    }

    @Override
    public void deleteInvestment(Integer portfolioId, Integer investmentId) {
        ensurePortfolioExists(portfolioId);
        getInvestmentById(portfolioId, investmentId);
        investmentRepository.deleteInvestment(investmentId, portfolioId);
    }

    private void ensurePortfolioExists(Integer portfolioId) {
        portfolioRepository.getPortfolioById(portfolioId)
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio with ID " + portfolioId + " not found"));
    }
}

