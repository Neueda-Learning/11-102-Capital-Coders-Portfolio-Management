package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.InvestmentNotFoundException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Asset;
import com.portfolio.portfolio_management.model.Investment;
import com.portfolio.portfolio_management.model.InvestmentListItem;
import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.model.PortfolioSummary;
import com.portfolio.portfolio_management.repository.AssetRepository;
import com.portfolio.portfolio_management.repository.InvestmentRepository;
import com.portfolio.portfolio_management.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class InvestmentServiceImpl implements InvestmentService {

    private static final String STOCKS = "stocks";
    private static final String BONDS = "bonds";
    private static final String MUTUAL_FUNDS = "mutual funds";
    private static final String CASH = "cash";

    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;

    public InvestmentServiceImpl(InvestmentRepository investmentRepository,
                                 PortfolioRepository portfolioRepository,
                                 AssetRepository assetRepository) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public List<InvestmentListItem> getInvestmentsByPortfolioId(Integer portfolioId) {
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
        Portfolio portfolio = ensurePortfolioExists(portfolioId);
        validateInvestmentRules(portfolio, investment, null);
        return investmentRepository.addInvestment(investment);
    }

    @Override
    public Investment updateInvestment(Integer portfolioId,
                                       Integer investmentId,
                                       Investment investment) {

        Portfolio portfolio = ensurePortfolioExists(portfolioId);
        Investment existingInvestment = getInvestmentById(portfolioId, investmentId);
        validateInvestmentRules(portfolio, investment, existingInvestment);

        return investmentRepository.updateInvestment(investmentId, portfolioId, investment);
    }

    @Override
    @Transactional
    public void deleteInvestment(Integer portfolioId, Integer investmentId) {
        ensurePortfolioExists(portfolioId);
        getInvestmentById(portfolioId, investmentId);
        investmentRepository.deleteInvestment(investmentId, portfolioId);
    }

    private Portfolio ensurePortfolioExists(Integer portfolioId) {
        return portfolioRepository.getPortfolioById(portfolioId)
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio with ID " + portfolioId + " not found"));
    }

    private void validateInvestmentRules(Portfolio portfolio,
                                         Investment candidate,
                                         Investment existingForUpdate) {
        if (candidate.purchaseDate() == null) {
            throw new IllegalArgumentException("Purchase date is required.");
        }

        if (candidate.purchaseDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Purchase date cannot be later than today.");
        }

        if (candidate.amountInvested() < 0) {
            throw new IllegalArgumentException("Amount invested cannot be negative.");
        }

        if (candidate.currentValue() < 0) {
            throw new IllegalArgumentException("Current value cannot be negative.");
        }

        Asset candidateAsset = assetRepository.getAssetById(candidate.assetId())
                .orElseThrow(() -> new IllegalArgumentException("Selected asset does not exist."));

        String candidateType = normalizeAssetType(candidateAsset.assetType());
        if (candidateType == null) {
            throw new IllegalArgumentException("Selected asset type is not supported for this portfolio.");
        }

        PortfolioSummary portfolioSummary = portfolioRepository.getPortfolioSummary(portfolio.portfolioId())
                .orElseThrow(() -> new IllegalArgumentException("Unable to calculate portfolio funds."));
        double totalFunds = portfolioSummary.totalFunds();
        List<InvestmentListItem> currentInvestments =
                investmentRepository.getInvestmentsByPortfolioId(portfolio.portfolioId());

        double usedFunds = currentInvestments.stream()
                .mapToDouble(InvestmentListItem::amountInvested)
                .sum();

        double proposedUsedFunds = usedFunds + candidate.amountInvested();

        if (existingForUpdate != null) {
            proposedUsedFunds -= existingForUpdate.amountInvested();
        }

        if (proposedUsedFunds > totalFunds) {
            throw new IllegalArgumentException("Investment exceeds available portfolio funds.");
        }
    }

    private String normalizeAssetType(String rawType) {
        if (rawType == null) {
            return null;
        }

        String normalized = rawType.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case STOCKS, "stock" -> STOCKS;
            case BONDS, "bond" -> BONDS;
            case MUTUAL_FUNDS, "mutual fund" -> MUTUAL_FUNDS;
            case CASH -> CASH;
            default -> null;
        };
    }
}
