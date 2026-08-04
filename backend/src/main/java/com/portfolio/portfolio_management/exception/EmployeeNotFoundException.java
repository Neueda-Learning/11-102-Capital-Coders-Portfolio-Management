package com.portfolio.portfolio_management.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Integer employeeId) {
        super("Employee with ID " + employeeId + " not found.");
    }
}


