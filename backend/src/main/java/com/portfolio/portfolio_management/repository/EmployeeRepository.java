package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Employee;
import org.springframework.data.repository.ListCrudRepository;

public interface EmployeeRepository extends ListCrudRepository<Employee, Integer> {
}

