package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record InvestmentListItem(
        int investmentId,
        int portfolioId,
        int assetId,
        String tickerSymbol,
        String assetType,
        double amountInvested,
        double currentValue,
        double quantity,
        LocalDate purchaseDate
) {
}

