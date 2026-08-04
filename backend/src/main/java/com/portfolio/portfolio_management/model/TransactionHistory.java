package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record TransactionHistory(
        int transactionId,
        int investmentId,
        String transactionType,
        double transactionAmount,
        LocalDate transactionDate
) {
}
