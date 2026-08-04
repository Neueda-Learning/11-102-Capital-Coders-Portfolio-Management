package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Employee;

import java.util.List;
public interface EmployeeService {

        List<Employee> getAllEmployees();

        Employee getEmployeeById(Integer employeeId);

        Employee addEmployee(Employee employee);

        Employee updateEmployee(Integer employeeId, Employee employee);

        void deleteEmployee(Integer employeeId);
}
