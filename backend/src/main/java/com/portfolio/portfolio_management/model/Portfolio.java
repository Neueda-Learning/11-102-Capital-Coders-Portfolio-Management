package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record Portfolio(
        Integer portfolioId,
        Integer employeeId,
        Integer fundId,
        String portfolioName,
        String portfolioDescription,
        String riskLevel,
        Double allocatedAmount,
        LocalDate createdDate
) {
}
