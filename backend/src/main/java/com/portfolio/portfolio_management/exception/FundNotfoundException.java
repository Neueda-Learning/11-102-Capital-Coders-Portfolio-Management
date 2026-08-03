package com.portfolio.portfolio_management.exception;

public class FundNotfoundException extends RuntimeException {
    public FundNotfoundException(int fundId) {
        super("Fund with ID " + fundId + " not found.");
    }
}
