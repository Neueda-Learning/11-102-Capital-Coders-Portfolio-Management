package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record Fund(
        int fundId,
        int investorId,
        String fundName,
        double amountReceived,
        LocalDate receivedDate,
        String status) {
}
