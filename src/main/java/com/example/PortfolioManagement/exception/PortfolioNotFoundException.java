package com.example.PortfolioManagement.exception;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(Long portfolioId) {
        super("Portfolio not found with id: " + portfolioId);
    }
}