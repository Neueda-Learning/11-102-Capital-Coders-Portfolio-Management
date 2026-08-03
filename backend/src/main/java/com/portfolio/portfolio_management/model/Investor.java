package com.portfolio.portfolio_management.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("investor")
public class Investor {

	@Id
	@Column("investor_id")
	@JsonProperty("investor_id")
	@JsonAlias("investorId")
	private Integer investorId;

	@NotBlank(message = "Investor name is required")
	@Size(max = 100, message = "Investor name must be at most 100 characters")
	@Column("investor_name")
	@JsonProperty("investor_name")
	@JsonAlias("investorName")
	private String investorName;

	@Email(message = "Contact email must be valid")
	@Size(max = 100, message = "Contact email must be at most 100 characters")
	@Column("contact_email")
	@JsonProperty("contact_email")
	@JsonAlias("contactEmail")
	private String contactEmail;

	public Investor() {
	}

	public Investor(Integer investorId, String investorName, String contactEmail) {
		this.investorId = investorId;
		this.investorName = investorName;
		this.contactEmail = contactEmail;
	}

	public Integer getInvestorId() {
		return investorId;
	}

	public void setInvestorId(Integer investorId) {
		this.investorId = investorId;
	}

	public String getInvestorName() {
		return investorName;
	}

	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
}
