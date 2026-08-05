package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.model.FundSummary;
import com.portfolio.portfolio_management.service.FundService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/funds")
public class FundController {

    private final FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }


    // Get total funds received by an investor
    // Example: GET /funds/investor/101/total
    @GetMapping("/investor/{investorId}/total")
    public ResponseEntity<FundSummary> getTotalFundsByInvestor(
            @PathVariable int investorId) {

        return ResponseEntity.ok(
                fundService.getTotalFundsByInvestorId(investorId)
        );
    }


    // Add a new funding round for an investor
    // Example: POST /funds/investor/101
    @PostMapping("/investor/{investorId}")
    public ResponseEntity<Fund> addNewFund(
            @PathVariable int investorId,
            @RequestBody Fund fund) {


        Fund newFund = new Fund(
                0,
                investorId,
                fund.fundName(),
                fund.amountReceived(),
                fund.receivedDate(),
                fund.status()
        );


        return ResponseEntity.ok(
                fundService.addnewFund(newFund)
        );
    }


    // Update a particular fund record
    // Example: PUT /funds/5
    @PutMapping("/{fundId}")
    public ResponseEntity<Fund> updateFund(
            @PathVariable int fundId,
            @RequestBody Fund fund) {


        Fund updatedFund = new Fund(
                fundId,
                fund.investorId(),
                fund.fundName(),
                fund.amountReceived(),
                fund.receivedDate(),
                fund.status()
        );


        return ResponseEntity.ok(
                fundService.updateFund(updatedFund)
        );
    }


    // Delete a particular fund record
    // Example: DELETE /funds/5
    @DeleteMapping("/{fundId}")
    public ResponseEntity<Void> deleteFund(
            @PathVariable int fundId) {

        fundService.deleteFund(fundId);

        return ResponseEntity.noContent().build();
    }
}