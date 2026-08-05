package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record InvestmentRequest(
        int assetId,
        double amountInvested,
        double currentValue,
        LocalDate purchaseDate
) {
}

