package com.example.PortfolioManagement.controller;

import com.example.PortfolioManagement.entity.Portfolio;
import com.example.PortfolioManagement.exception.PortfolioNotFoundException;
import com.example.PortfolioManagement.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PortfolioController {
    private final PortfolioService portfolioService;
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }


    @GetMapping("/portfolios")
    public ResponseEntity<?> getPortfolios() {
        return ResponseEntity.ok(portfolioService.viewAllPortfolios());
    }

    @GetMapping("/portfolios/{id}")
    public ResponseEntity<?> getPortfolioById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(portfolioService.viewPortfolioById(id));
        } catch (PortfolioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/portfolios")
    public ResponseEntity<?> addPortfolio(@RequestBody Portfolio portfolio) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.addPortfolio(portfolio));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
