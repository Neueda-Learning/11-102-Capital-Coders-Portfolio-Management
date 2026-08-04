package com.portfolio.portfolio_management.exception;

public class DuplicateFundException extends RuntimeException {
    public DuplicateFundException(String fundName) {
        super("Fund with name '" + fundName + "' already exists");
    }
}
