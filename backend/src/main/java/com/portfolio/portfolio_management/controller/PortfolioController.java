package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public List<Portfolio> getAllPortfolios() {
        return portfolioService.getAllPortfolios();
    }

    @GetMapping("/{portfolioId}")
    public Portfolio getPortfolioById(@PathVariable Integer portfolioId) {
        return portfolioService.getPortfolioById(portfolioId);
    }

    @PostMapping
    public Portfolio addPortfolio(@RequestBody Portfolio portfolio) {
        return portfolioService.addPortfolio(portfolio);
    }

    @PutMapping("/{portfolioId}")
    public Portfolio updatePortfolio(@PathVariable Integer portfolioId,
                                     @RequestBody Portfolio portfolio) {
        return portfolioService.updatePortfolio(portfolioId, portfolio);
    }

    @DeleteMapping("/{portfolioId}")
    public String deletePortfolio(@PathVariable Integer portfolioId) {

        portfolioService.deletePortfolio(portfolioId);

        return "Portfolio deleted successfully.";
    }

    @GetMapping("/employees/{employeeId}")
    public List<Portfolio> getPortfoliosByEmployeeId(@PathVariable Integer employeeId) {
        return portfolioService.getPortfoliosByEmployeeId(employeeId);
    }

//    @GetMapping("/funds/{fundId}")
//    public List<Portfolio> getPortfoliosByFundId(@PathVariable Integer fundId) {
//        return portfolioService.getPortfoliosByFundId(fundId);
//    }
}
