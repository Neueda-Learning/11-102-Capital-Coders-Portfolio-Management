package com.portfolio.portfolio_management.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("employee")
public class Employee {
	@Id
	@Column("employee_id")
	@JsonProperty("employee_id")
	@JsonAlias({"employeeId", "empId"})
	private Integer employeeId;

	@NotBlank(message = "Employee name is required")
	@Size(max = 100, message = "Employee name must be at most 100 characters")
	@Column("employee_name")
	@JsonProperty("employee_name")
	@JsonAlias({"employeeName", "empName"})
	private String employeeName;

	@NotBlank(message = "Employee email is required")
	@Email(message = "Employee email must be valid")
	@Size(max = 100, message = "Employee email must be at most 100 characters")
	@Column("email")
	@JsonProperty("email")
	@JsonAlias({"employeeEmail", "empEmail"})
	private String email;

	@NotBlank(message = "Department is required")
	@Size(max = 50, message = "Department must be at most 50 characters")
	@Column("department")
	private String department;

	public Employee() {
	}

	public Employee(Integer employeeId, String employeeName, String email, String department) {
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.email = email;
		this.department = department;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
}
