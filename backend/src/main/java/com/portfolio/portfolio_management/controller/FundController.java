package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.service.FundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FundController {
    private FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @GetMapping("/funds/{fundId}")
    public ResponseEntity<Fund> getFundById(@PathVariable int fundId) {
        return fundService.getFundById(fundId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/funds")
    public Fund addnewFund(@RequestBody Fund fund) {
        return fundService.addnewFund(fund);
    }

    @PutMapping("/funds/{fundId}")
    public Fund updateFund(@PathVariable int fundId, @RequestBody Fund fund) {
        return fundService.updateFund(new Fund(fundId, fund.investorId(), fund.fundName(), fund.amountReceived(), fund.receivedDate(), fund.status()));
    }

    @DeleteMapping("/funds/{fundId}")
    public ResponseEntity<Void> deleteFund(@PathVariable int fundId) {
        fundService.deleteFund(fundId);
        return ResponseEntity.noContent().build();
    }
}
