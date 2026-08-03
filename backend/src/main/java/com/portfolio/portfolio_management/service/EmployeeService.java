package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Employee;
import com.portfolio.portfolio_management.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found with id: " + id));
    }

    public Employee createEmployee(Employee employeeRequest) {
        employeeRequest.setEmployeeId(null);
        return employeeRepository.save(employeeRequest);
    }

    public Employee updateEmployee(int id, Employee employeeRequest) {
        getEmployeeById(id);
        employeeRequest.setEmployeeId(id);
        return employeeRepository.save(employeeRequest);
    }

    public void deleteEmployee(int id) {
        getEmployeeById(id);
        employeeRepository.deleteById(id);
    }
}


