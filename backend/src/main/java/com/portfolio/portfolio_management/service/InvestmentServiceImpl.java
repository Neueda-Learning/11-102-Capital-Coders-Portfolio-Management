package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.InvestmentNotFoundException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Asset;
import com.portfolio.portfolio_management.model.Investment;
import com.portfolio.portfolio_management.model.InvestmentListItem;
import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.model.PortfolioSummary;
import com.portfolio.portfolio_management.model.TradeRequest;
import com.portfolio.portfolio_management.model.TransactionHistory;
import com.portfolio.portfolio_management.repository.AssetRepository;
import com.portfolio.portfolio_management.repository.InvestmentRepository;
import com.portfolio.portfolio_management.repository.PortfolioRepository;
import com.portfolio.portfolio_management.repository.TransactionHistoryRepository;
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
    private static final String BUY = "BUY";
    private static final String SELL = "SELL";

    private final InvestmentRepository investmentRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final MarketPriceService marketPriceService;

    public InvestmentServiceImpl(InvestmentRepository investmentRepository,
                                 PortfolioRepository portfolioRepository,
                                 AssetRepository assetRepository,
                                 TransactionHistoryRepository transactionHistoryRepository,
                                 MarketPriceService marketPriceService) {
        this.investmentRepository = investmentRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.marketPriceService = marketPriceService;
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

    @Override
    @Transactional
    public Investment buy(Integer portfolioId, TradeRequest tradeRequest) {
        Portfolio portfolio = ensurePortfolioExists(portfolioId);
        double quantity = validateTradeRequest(tradeRequest);

        Asset asset = assetRepository.getAssetById(tradeRequest.assetId())
                .orElseThrow(() -> new IllegalArgumentException("Selected asset does not exist."));

        double price = marketPriceService.getLatestPrice(asset.tickerSymbol());
        double cost = round2(price * quantity);

        // PortfolioSummary.availableFunds() isn't populated by the repository query (always 0),
        // so it's derived here from totalFunds - usedFunds instead.
        PortfolioSummary summary = portfolioRepository.getPortfolioSummary(portfolio.portfolioId())
                .orElseThrow(() -> new IllegalArgumentException("Unable to calculate portfolio funds."));
        double availableFunds = summary.totalFunds() - summary.usedFunds();

        if (cost > availableFunds) {
            throw new IllegalArgumentException(
                    "Insufficient available funds: this purchase costs " + cost
                            + " but only " + round2(availableFunds) + " is available.");
        }

        Investment existing = investmentRepository
                .getInvestmentByPortfolioIdAndAssetId(portfolioId, tradeRequest.assetId())
                .orElse(null);

        Investment saved;
        if (existing == null) {
            Investment toCreate = new Investment(
                    0, portfolioId, tradeRequest.assetId(), cost, cost, quantity, LocalDate.now());
            saved = investmentRepository.addInvestment(toCreate);
        } else {
            double newQuantity = existing.quantity() + quantity;
            double newAmountInvested = round2(existing.amountInvested() + cost);
            double newCurrentValue = round2(newQuantity * price);

            Investment toUpdate = new Investment(
                    existing.investmentId(), portfolioId, existing.assetId(),
                    newAmountInvested, newCurrentValue, newQuantity, existing.purchaseDate());

            saved = investmentRepository.updateInvestment(existing.investmentId(), portfolioId, toUpdate);
        }

        transactionHistoryRepository.addTransaction(
                new TransactionHistory(0, saved.investmentId(), BUY, cost, quantity, price, LocalDate.now()));

        return saved;
    }

    @Override
    @Transactional
    public Investment sell(Integer portfolioId, TradeRequest tradeRequest) {
        ensurePortfolioExists(portfolioId);
        double quantity = validateTradeRequest(tradeRequest);

        Investment existing = investmentRepository
                .getInvestmentByPortfolioIdAndAssetId(portfolioId, tradeRequest.assetId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "This portfolio does not currently hold that asset."));

        if (quantity > existing.quantity()) {
            throw new IllegalArgumentException(
                    "Cannot sell " + quantity + " units - only " + existing.quantity() + " are held.");
        }

        Asset asset = assetRepository.getAssetById(existing.assetId())
                .orElseThrow(() -> new IllegalArgumentException("Selected asset does not exist."));

        double price = marketPriceService.getLatestPrice(asset.tickerSymbol());
        double proceeds = round2(price * quantity);

        double costBasisPerUnit = existing.quantity() == 0 ? 0 : existing.amountInvested() / existing.quantity();
        double remainingQuantity = existing.quantity() - quantity;
        double remainingAmountInvested = round2(remainingQuantity * costBasisPerUnit);
        double remainingCurrentValue = round2(remainingQuantity * price);

        Investment toUpdate = new Investment(
                existing.investmentId(), portfolioId, existing.assetId(),
                remainingAmountInvested, remainingCurrentValue, remainingQuantity, existing.purchaseDate());

        Investment saved = investmentRepository.updateInvestment(existing.investmentId(), portfolioId, toUpdate);

        transactionHistoryRepository.addTransaction(
                new TransactionHistory(0, saved.investmentId(), SELL, proceeds, quantity, price, LocalDate.now()));

        return saved;
    }

    @Override
    public List<TransactionHistory> getTransactionHistory(Integer portfolioId, Integer investmentId) {
        ensurePortfolioExists(portfolioId);
        getInvestmentById(portfolioId, investmentId); // validates the investment belongs to this portfolio
        return transactionHistoryRepository.getByInvestmentId(investmentId);
    }

    private double validateTradeRequest(TradeRequest tradeRequest) {
        if (tradeRequest.assetId() == null) {
            throw new IllegalArgumentException("Asset is required.");
        }
        if (tradeRequest.quantity() == null || tradeRequest.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        return tradeRequest.quantity();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
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
