package com.portfolio.portfolio_management.exception;

public class DuplicateEmployeeEmailException extends RuntimeException {

    public DuplicateEmployeeEmailException(String email) {
        super("Employee with email '" + email + "' already exists.");
    }
}


