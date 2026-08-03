package com.portfolio.portfolio_management.exception;

public class PortfolioNotFoundException extends RuntimeException {

    public PortfolioNotFoundException(String message) {
        super(message);
    }
}