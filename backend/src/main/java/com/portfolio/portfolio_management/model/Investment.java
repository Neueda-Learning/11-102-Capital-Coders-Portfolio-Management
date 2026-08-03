package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record Investment(
        int investmentId,
        int portfolioId,
        int assetId,
        double amountInvested,
        double currentValue,
        LocalDate purchaseDate
) {
}

