package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Employee> EMPLOYEE_ROW_MAPPER = (rs, rowNum) ->
            new Employee(
                    rs.getInt("employee_id"),
                    rs.getString("employee_name"),
                    rs.getString("email"),
                    rs.getString("department")
            );
    public List<Employee> getAllEmployees() {
        String sql = "SELECT * FROM employee";
        return jdbcTemplate.query(sql, EMPLOYEE_ROW_MAPPER);
    }

    public Optional<Employee> getEmployeeById(Integer employeeId) {
        String sql = "SELECT * FROM employee WHERE employee_id = ?";
        List<Employee> employees = jdbcTemplate.query(sql, EMPLOYEE_ROW_MAPPER, employeeId);
        return employees.stream().findFirst();
    }

    public Optional<Employee> findByEmail(String email) {
        String sql = "SELECT * FROM employee WHERE email = ?";
        List<Employee> employees = jdbcTemplate.query(sql, EMPLOYEE_ROW_MAPPER, email);
        return employees.stream().findFirst();
    }

    public Employee addEmployee(Employee employee) {
        String sql = """
                INSERT INTO employee
                (employee_name, email, department)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                employee.empName(),
                employee.empEmail(),
                employee.department()
        );

        Integer id = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );

        return getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Employee not created"));
    }

    public Employee updateEmployee(Integer employeeId, Employee employee) {
        String sql = """
                UPDATE employee
                SET employee_name = ?,
                    email = ?,
                    department = ?
                WHERE employee_id = ?
                """;

        jdbcTemplate.update(
                sql,
                employee.empName(),
                employee.empEmail(),
                employee.department(),
                employeeId
        );

        return getEmployeeById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public void deleteEmployee(Integer employeeId) {
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        jdbcTemplate.update(sql, employeeId);
    }
}
