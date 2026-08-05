package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.DuplicateEmployeeEmailException;
import com.portfolio.portfolio_management.exception.EmployeeNotFoundException;
import com.portfolio.portfolio_management.model.Employee;
import com.portfolio.portfolio_management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository);
    }

    private Employee sampleEmployee(Integer id, String email) {
        return new Employee(id, "Alex", email, "Investments");
    }

    @Test
    @DisplayName("getAllEmployees returns repository list")
    void getAllEmployees_returnsList() {
        when(employeeRepository.getAllEmployees()).thenReturn(List.of(
                sampleEmployee(1, "a@company.com"),
                sampleEmployee(2, "b@company.com")
        ));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        verify(employeeRepository).getAllEmployees();
    }

    @Test
    @DisplayName("getEmployeeById throws when employee is missing")
    void getEmployeeById_whenMissing_throwsNotFound() {
        when(employeeRepository.getEmployeeById(99)).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(99)
        );

        assertEquals("Employee with ID 99 not found.", ex.getMessage());
    }

    @Test
    @DisplayName("addEmployee throws conflict when email exists")
    void addEmployee_whenDuplicateEmail_throwsDuplicate() {
        Employee request = sampleEmployee(null, "dup@company.com");
        when(employeeRepository.findByEmail("dup@company.com")).thenReturn(Optional.of(sampleEmployee(7, "dup@company.com")));

        DuplicateEmployeeEmailException ex = assertThrows(
                DuplicateEmployeeEmailException.class,
                () -> employeeService.addEmployee(request)
        );

        assertEquals("Employee with email 'dup@company.com' already exists.", ex.getMessage());
        verify(employeeRepository, never()).addEmployee(request);
    }

    @Test
    @DisplayName("addEmployee stores employee when email is unique")
    void addEmployee_whenUnique_addsEmployee() {
        Employee request = sampleEmployee(null, "new@company.com");
        Employee created = sampleEmployee(5, "new@company.com");
        when(employeeRepository.findByEmail("new@company.com")).thenReturn(Optional.empty());
        when(employeeRepository.addEmployee(request)).thenReturn(created);

        Employee result = employeeService.addEmployee(request);

        assertEquals(5, result.empId());
        verify(employeeRepository).addEmployee(request);
    }

    @Test
    @DisplayName("updateEmployee throws when target employee is missing")
    void updateEmployee_whenTargetMissing_throwsNotFound() {
        Employee request = sampleEmployee(null, "update@company.com");
        when(employeeRepository.getEmployeeById(20)).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(20, request)
        );

        assertEquals("Employee with ID 20 not found.", ex.getMessage());
        verify(employeeRepository, never()).updateEmployee(20, request);
    }

    @Test
    @DisplayName("updateEmployee throws conflict when email belongs to another employee")
    void updateEmployee_whenEmailBelongsToAnother_throwsDuplicate() {
        Employee request = sampleEmployee(null, "dup@company.com");
        when(employeeRepository.getEmployeeById(4)).thenReturn(Optional.of(sampleEmployee(4, "old@company.com")));
        when(employeeRepository.findByEmail("dup@company.com")).thenReturn(Optional.of(sampleEmployee(9, "dup@company.com")));

        DuplicateEmployeeEmailException ex = assertThrows(
                DuplicateEmployeeEmailException.class,
                () -> employeeService.updateEmployee(4, request)
        );

        assertEquals("Employee with email 'dup@company.com' already exists.", ex.getMessage());
        verify(employeeRepository, never()).updateEmployee(4, request);
    }

    @Test
    @DisplayName("updateEmployee allows same email for same employee")
    void updateEmployee_whenEmailBelongsToSameEmployee_updates() {
        Employee request = sampleEmployee(null, "same@company.com");
        Employee updated = sampleEmployee(4, "same@company.com");

        when(employeeRepository.getEmployeeById(4)).thenReturn(Optional.of(sampleEmployee(4, "same@company.com")));
        when(employeeRepository.findByEmail("same@company.com")).thenReturn(Optional.of(sampleEmployee(4, "same@company.com")));
        when(employeeRepository.updateEmployee(4, request)).thenReturn(updated);

        Employee result = employeeService.updateEmployee(4, request);

        assertEquals(4, result.empId());
        verify(employeeRepository).updateEmployee(4, request);
    }

    @Test
    @DisplayName("deleteEmployee throws when target employee is missing")
    void deleteEmployee_whenMissing_throwsNotFound() {
        when(employeeRepository.getEmployeeById(33)).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(33)
        );

        assertEquals("Employee with ID 33 not found.", ex.getMessage());
        verify(employeeRepository, never()).deleteEmployee(33);
    }

    @Test
    @DisplayName("deleteEmployee calls repository when employee exists")
    void deleteEmployee_whenFound_deletes() {
        when(employeeRepository.getEmployeeById(3)).thenReturn(Optional.of(sampleEmployee(3, "a@company.com")));

        employeeService.deleteEmployee(3);

        verify(employeeRepository).deleteEmployee(3);
    }
}

