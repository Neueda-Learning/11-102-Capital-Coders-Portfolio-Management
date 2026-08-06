package com.portfolio.portfolio_management.model;

public record LiveMarketPrice(
        int assetId,
        String tickerSymbol,
        double price
) {
}

