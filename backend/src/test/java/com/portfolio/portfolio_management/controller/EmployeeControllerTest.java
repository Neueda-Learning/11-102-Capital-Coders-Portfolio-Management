package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.exception.DuplicateEmployeeEmailException;
import com.portfolio.portfolio_management.exception.EmployeeNotFoundException;
import com.portfolio.portfolio_management.exception.GlobalExceptionHandler;
import com.portfolio.portfolio_management.model.Employee;
import com.portfolio.portfolio_management.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    EmployeeControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @MockitoBean
    @SuppressWarnings("unused")
    private EmployeeService employeeService;

    private Employee sampleEmployee(Integer id, String email) {
        return new Employee(id, "Alex", email, "Investments");
    }

    @Test
    @DisplayName("GET /employees returns all employees")
    void getAllEmployees_returnsList() throws Exception {
        given(employeeService.getAllEmployees()).willReturn(List.of(
                sampleEmployee(1, "a@company.com"),
                sampleEmployee(2, "b@company.com")
        ));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].empId").value(1));
    }

    @Test
    @DisplayName("GET /employees/{id} returns 404 when employee does not exist")
    void getEmployeeById_whenMissing_returnsNotFound() throws Exception {
        given(employeeService.getEmployeeById(99))
                .willThrow(new EmployeeNotFoundException(99));

        mockMvc.perform(get("/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee with ID 99 not found."));
    }

    @Test
    @DisplayName("POST /employees creates employee")
    void addEmployee_whenValid_returnsCreatedEmployee() throws Exception {
        Employee request = sampleEmployee(null, "new@company.com");
        Employee created = sampleEmployee(7, "new@company.com");
        given(employeeService.addEmployee(any(Employee.class))).willReturn(created);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empId").value(7))
                .andExpect(jsonPath("$.empEmail").value("new@company.com"));
    }

    @Test
    @DisplayName("POST /employees returns 409 when email already exists")
    void addEmployee_whenDuplicateEmail_returnsConflict() throws Exception {
        Employee request = sampleEmployee(null, "dup@company.com");
        given(employeeService.addEmployee(any(Employee.class)))
                .willThrow(new DuplicateEmployeeEmailException("dup@company.com"));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Employee with email 'dup@company.com' already exists."));
    }

    @Test
    @DisplayName("POST /employees returns 400 for malformed JSON")
    void addEmployee_whenMalformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad-json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }

    @Test
    @DisplayName("PUT /employees/{id} updates employee")
    void updateEmployee_whenValid_returnsUpdatedEmployee() throws Exception {
        Employee request = sampleEmployee(null, "updated@company.com");
        Employee updated = sampleEmployee(5, "updated@company.com");
        given(employeeService.updateEmployee(eq(5), any(Employee.class))).willReturn(updated);

        mockMvc.perform(put("/employees/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empId").value(5));
    }

    @Test
    @DisplayName("PUT /employees/{id} returns 404 when employee does not exist")
    void updateEmployee_whenMissing_returnsNotFound() throws Exception {
        Employee request = sampleEmployee(null, "updated@company.com");
        given(employeeService.updateEmployee(eq(88), any(Employee.class)))
                .willThrow(new EmployeeNotFoundException(88));

        mockMvc.perform(put("/employees/88")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee with ID 88 not found."));
    }

    @Test
    @DisplayName("DELETE /employees/{id} returns success message")
    void deleteEmployee_whenFound_returnsSuccessMessage() throws Exception {
        doNothing().when(employeeService).deleteEmployee(3);

        mockMvc.perform(delete("/employees/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee deleted successfully."));
    }

    @Test
    @DisplayName("DELETE /employees/{id} returns 404 when employee does not exist")
    void deleteEmployee_whenMissing_returnsNotFound() throws Exception {
        doThrow(new EmployeeNotFoundException(66)).when(employeeService).deleteEmployee(66);

        mockMvc.perform(delete("/employees/66"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee with ID 66 not found."));
    }
}

