package com.example.PortfolioManagement.controller;

import com.example.PortfolioManagement.exception.PortfolioNotFoundException;
import com.example.PortfolioManagement.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
