package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.DuplicateEmployeeEmailException;
import com.portfolio.portfolio_management.exception.EmployeeNotFoundException;
import com.portfolio.portfolio_management.model.Employee;
import com.portfolio.portfolio_management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }

    @Override
    public Employee getEmployeeById(Integer employeeId) {
        return employeeRepository
                .getEmployeeById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId)); // 404
    }

    @Override
    public Employee addEmployee(Employee employee) {
        // 409 - duplicate email check
        employeeRepository.findByEmail(employee.empEmail())
                .ifPresent(existing -> {
                    throw new DuplicateEmployeeEmailException(employee.empEmail());
                });

        return employeeRepository.addEmployee(employee);
    }

    @Override
    public Employee updateEmployee(Integer employeeId, Employee employee) {
        // 404 - check employee exists
        employeeRepository.getEmployeeById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        // 409 - check no other employee has the same email
        employeeRepository.findByEmail(employee.empEmail())
                .ifPresent(existing -> {
                    if (!existing.empId().equals(employeeId)) {
                        throw new DuplicateEmployeeEmailException(employee.empEmail());
                    }
                });

        return employeeRepository.updateEmployee(employeeId, employee);
    }

    @Override
    public void deleteEmployee(Integer employeeId) {
        // 404 - check employee exists
        employeeRepository.getEmployeeById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        employeeRepository.deleteEmployee(employeeId);
    }
}