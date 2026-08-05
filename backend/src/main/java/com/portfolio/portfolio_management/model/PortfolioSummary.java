package com.portfolio.portfolio_management.model;

public record PortfolioSummary(
        double stocksInvested,
        double bondsInvested,
        double mutualFundsInvested,
        double cashInvested,
        double totalFunds,
        double usedFunds,
        double availableFunds,
        double gainLoss,
        double returnPercent,
        String gainLossLabel
) {
}

