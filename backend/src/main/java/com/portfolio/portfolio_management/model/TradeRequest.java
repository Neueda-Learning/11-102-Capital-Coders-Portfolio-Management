package com.portfolio.portfolio_management.model;

public record TradeRequest(
        Integer assetId,
        Double quantity
) {
}
