package com.portfolio.portfolio_management.model;

import java.time.LocalDate;

public record FundRequest(
        Integer investorId,
        String fundName,
        Double amountReceived,
        LocalDate receivedDate,
        String status) {
}

