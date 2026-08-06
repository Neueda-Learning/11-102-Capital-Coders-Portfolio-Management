package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.DuplicatePortfolioException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.PerformancePoint;
import com.portfolio.portfolio_management.model.PerformanceTransaction;
import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.model.PortfolioPerformance;
import com.portfolio.portfolio_management.model.PortfolioSummary;
import com.portfolio.portfolio_management.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private static final DateTimeFormatter MONTH_LABEL_FORMAT =
            DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    @Override
    public PortfolioPerformance getPortfolioPerformance(Integer portfolioId) {

        portfolioRepository.getPortfolioById(portfolioId)
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio with ID " + portfolioId + " not found"));

        List<PerformanceTransaction> transactions =
                portfolioRepository.getPerformanceTransactions(portfolioId);

        Map<Integer, Double> latestPriceByAsset =
                portfolioRepository.getCurrentPricePerAsset(portfolioId);

        // Fallback price for assets fully sold off (quantity now 0, so they
        // don't appear in latestPriceByAsset): use the price of their most
        // recent transaction instead.
        Map<Integer, Double> lastTransactionPriceByAsset = new HashMap<>();
        for (PerformanceTransaction t : transactions) {
            if (t.pricePerUnit() > 0) {
                lastTransactionPriceByAsset.put(t.assetId(), t.pricePerUnit());
            }
        }

        return new PortfolioPerformance(
                buildMonthlyPoints(transactions, latestPriceByAsset, lastTransactionPriceByAsset),
                buildQuarterlyPoints(transactions, latestPriceByAsset, lastTransactionPriceByAsset),
                buildYearlyPoints(transactions, latestPriceByAsset, lastTransactionPriceByAsset)
        );
    }

    private List<PerformancePoint> buildMonthlyPoints(List<PerformanceTransaction> transactions,
                                                      Map<Integer, Double> latestPriceByAsset,
                                                      Map<Integer, Double> lastTransactionPriceByAsset) {
        List<PerformancePoint> points = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(today).minusMonths(i);
            LocalDate periodStart = yearMonth.atDay(1);
            LocalDate periodEnd = (i == 0) ? today : yearMonth.atEndOfMonth();
            String label = periodStart.format(MONTH_LABEL_FORMAT);

            points.add(computePoint(label, periodStart, periodEnd,
                    transactions, latestPriceByAsset, lastTransactionPriceByAsset));
        }

        return points;
    }

    private List<PerformancePoint> buildQuarterlyPoints(List<PerformanceTransaction> transactions,
                                                        Map<Integer, Double> latestPriceByAsset,
                                                        Map<Integer, Double> lastTransactionPriceByAsset) {
        List<PerformancePoint> points = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int currentQuarterIndex = (today.getMonthValue() - 1) / 3; // 0-based: 0..3
        int currentQuarterOrdinal = today.getYear() * 4 + currentQuarterIndex;

        for (int i = 3; i >= 0; i--) {
            int quarterOrdinal = currentQuarterOrdinal - i;
            int year = Math.floorDiv(quarterOrdinal, 4);
            int quarterIndex = Math.floorMod(quarterOrdinal, 4);
            int startMonth = quarterIndex * 3 + 1;

            LocalDate periodStart = LocalDate.of(year, startMonth, 1);
            LocalDate quarterEnd = periodStart.plusMonths(3).minusDays(1);
            LocalDate periodEnd = (i == 0) ? today : quarterEnd;
            String label = "Q" + (quarterIndex + 1) + " " + year;

            points.add(computePoint(label, periodStart, periodEnd,
                    transactions, latestPriceByAsset, lastTransactionPriceByAsset));
        }

        return points;
    }

    private List<PerformancePoint> buildYearlyPoints(List<PerformanceTransaction> transactions,
                                                     Map<Integer, Double> latestPriceByAsset,
                                                     Map<Integer, Double> lastTransactionPriceByAsset) {
        List<PerformancePoint> points = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 4; i >= 0; i--) {
            int year = today.getYear() - i;
            LocalDate periodStart = LocalDate.of(year, 1, 1);
            LocalDate periodEnd = (i == 0) ? today : LocalDate.of(year, 12, 31);
            String label = String.valueOf(year);

            points.add(computePoint(label, periodStart, periodEnd,
                    transactions, latestPriceByAsset, lastTransactionPriceByAsset));
        }

        return points;
    }

    /**
     * Reconstructs invested capital and holding value as of periodEnd by
     * replaying every transaction dated on/before periodEnd. Holdings are
     * valued at the latest known market price for each asset (there is no
     * historical price feed), so currentValue reflects "what it would be
     * worth today", not the true value on that historical date.
     */
    private PerformancePoint computePoint(String label,
                                          LocalDate periodStart,
                                          LocalDate periodEnd,
                                          List<PerformanceTransaction> transactions,
                                          Map<Integer, Double> latestPriceByAsset,
                                          Map<Integer, Double> lastTransactionPriceByAsset) {

        double invested = 0;
        Map<Integer, Double> quantityByAsset = new HashMap<>();

        for (PerformanceTransaction t : transactions) {
            if (t.transactionDate() == null || t.transactionDate().isAfter(periodEnd)) {
                continue;
            }

            boolean isSell = "SELL".equalsIgnoreCase(t.transactionType());
            double signedAmount = isSell ? -t.amount() : t.amount();
            double signedQuantity = isSell ? -t.quantity() : t.quantity();

            invested += signedAmount;
            quantityByAsset.merge(t.assetId(), signedQuantity, Double::sum);
        }

        double currentValue = 0;
        for (Map.Entry<Integer, Double> entry : quantityByAsset.entrySet()) {
            double quantity = entry.getValue();
            if (quantity <= 0) {
                continue;
            }

            int assetId = entry.getKey();
            Double price = latestPriceByAsset.get(assetId);
            if (price == null || price == 0) {
                price = lastTransactionPriceByAsset.getOrDefault(assetId, 0.0);
            }

            currentValue += quantity * price;
        }

        invested = round2(Math.max(invested, 0));
        currentValue = round2(currentValue);

        double gainLoss = round2(currentValue - invested);
        double returnPercent = invested == 0 ? 0 : round2((gainLoss / invested) * 100);

        return new PerformancePoint(label, periodStart, periodEnd, invested, currentValue, gainLoss, returnPercent);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

//    @Override
//    public List<Portfolio> getPortfoliosByFundId(Integer fundId) {
//        return portfolioRepository.getPortfoliosByFundId(fundId);
//    }
}