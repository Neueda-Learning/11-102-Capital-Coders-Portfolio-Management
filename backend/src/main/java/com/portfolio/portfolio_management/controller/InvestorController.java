package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.model.Investor;
import com.portfolio.portfolio_management.service.InvestorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/investors")
public class InvestorController {

    private final InvestorService investorService;

    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }

    @GetMapping
    public List<Investor> getInvestors() {
        return investorService.getInvestors();
    }

    @GetMapping("/{investorId}")
    public Investor getInvestorById(@PathVariable int investorId) {
        return investorService.getInvestorById(investorId);
    }

    @PostMapping
    public ResponseEntity<Investor> addInvestor(@Valid @RequestBody Investor investor) {
        Investor savedInvestor = investorService.addInvestor(investor);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{investorId}")
                .buildAndExpand(savedInvestor.getInvestorId())
                .toUri();

        return ResponseEntity.created(location).body(savedInvestor);
    }

    @PutMapping("/{investorId}")
    public Investor updateInvestor(@PathVariable int investorId, @Valid @RequestBody Investor investor) {
        investor.setInvestorId(investorId);
        return investorService.updateInvestor(investorId, investor);
    }

    @DeleteMapping("/{investorId}")
    public ResponseEntity<Void> deleteInvestor(@PathVariable int investorId) {
        investorService.deleteInvestor(investorId);
        return ResponseEntity.noContent().build();
    }
}

