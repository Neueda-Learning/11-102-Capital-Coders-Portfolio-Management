package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

/**
 * Internal row used only to compute PortfolioPerformance - a single
 * BUY/SELL transaction joined with the asset it belongs to.
 * Not exposed directly through the API.
 */
public record PerformanceTransaction(
        int assetId,
        String transactionType,
        double amount,
        double quantity,
        double pricePerUnit,
        LocalDate transactionDate
) {
}
