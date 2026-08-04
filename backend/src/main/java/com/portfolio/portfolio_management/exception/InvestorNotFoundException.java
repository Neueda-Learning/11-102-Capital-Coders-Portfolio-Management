package com.portfolio.portfolio_management.exception;

public class InvestorNotFoundException extends RuntimeException {

    public InvestorNotFoundException(Integer investorId) {
        super("Investor with ID " + investorId + " not found.");
    }
}