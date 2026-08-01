package com.example.PortfolioManagement.entity;

public class Portfolio {
    private Long portfolioId;
    private String portfolioName;
    private String portfolioDescription;
    private String riskLevel;

    public Portfolio() {
    }

    public Portfolio(Long portfolioId, String portfolioName, String portfolioDescription, String riskLevel) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.portfolioDescription = portfolioDescription;
        this.riskLevel = riskLevel;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getPortfolioDescription() {
        return portfolioDescription;
    }

    public void setPortfolioDescription(String portfolioDescription) {
        this.portfolioDescription = portfolioDescription;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}
