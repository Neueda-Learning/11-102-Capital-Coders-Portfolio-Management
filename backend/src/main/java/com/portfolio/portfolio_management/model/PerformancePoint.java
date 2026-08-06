package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

/**
 * Represents portfolio performance as of the end of a single period
 * (a month, a quarter, or a year).
 *
 * invested    - net capital deployed (buys minus sells) up to periodEnd
 * currentValue- value of everything still held, priced at the latest
 *               known market price for each asset
 * gainLoss    - currentValue - invested
 * returnPercent - gainLoss expressed as a % of invested
 */
public record PerformancePoint(
        String label,
        LocalDate periodStart,
        LocalDate periodEnd,
        double invested,
        double currentValue,
        double gainLoss,
        double returnPercent
) {
}