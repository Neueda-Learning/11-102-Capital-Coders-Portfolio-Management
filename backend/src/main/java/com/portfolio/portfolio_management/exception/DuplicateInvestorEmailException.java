package com.portfolio.portfolio_management.exception;

public class DuplicateInvestorEmailException extends RuntimeException {

    public DuplicateInvestorEmailException(String email) {
        super("Investor with email '" + email + "' already exists.");
    }
}