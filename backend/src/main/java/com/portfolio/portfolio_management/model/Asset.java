package com.portfolio.portfolio_management.model;

public record Asset(
        int assetId,
        String assetName,
        String tickerSymbol,
        String assetType
) {
}

