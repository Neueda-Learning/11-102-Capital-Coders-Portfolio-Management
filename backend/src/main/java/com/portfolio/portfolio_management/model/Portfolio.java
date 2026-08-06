package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record Portfolio(
        Integer portfolioId,
        int empId,
        int investorId,
        String portfolioName,
        String portfolioDescription,
        String riskLevel,
        Double allocatedAmount,
        LocalDate createdDate
) {
}
