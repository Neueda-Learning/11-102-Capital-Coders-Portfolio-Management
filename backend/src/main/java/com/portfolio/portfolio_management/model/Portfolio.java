package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record Portfolio(int portfolioId, int empId, int fundId, String portfolioName, String portfolioDescription, String riskLevel, double allocatedAmount, LocalDate createdDate) {
}
