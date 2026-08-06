package com.portfolio.portfolio_management.model;

import java.util.List;

/**
 * Time-series performance of a portfolio, bucketed three ways:
 *  - monthly:   last 6 months
 *  - quarterly: last 4 quarters
 *  - yearly:    last 5 years
 */
public record PortfolioPerformance(
        List<PerformancePoint> monthly,
        List<PerformancePoint> quarterly,
        List<PerformancePoint> yearly
) {
}