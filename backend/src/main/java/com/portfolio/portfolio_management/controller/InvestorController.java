package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.model.Investor;
import com.portfolio.portfolio_management.service.InvestorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/investors")
public class InvestorController {

    private final InvestorService investorService;

    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }

    @GetMapping
    public List<Investor> getAllInvestors() {
        return investorService.getAllInvestors();
    }

    @GetMapping("/{investorId}")
    public Investor getInvestorById(@PathVariable Integer investorId) {
        return investorService.getInvestorById(investorId);
    }

    @PostMapping
    public Investor addInvestor(@RequestBody Investor investor) {
        return investorService.addInvestor(investor);
    }

    @PutMapping("/{investorId}")
    public Investor updateInvestor(@PathVariable Integer investorId,
                                   @RequestBody Investor investor) {
        return investorService.updateInvestor(investorId, investor);
    }

    @DeleteMapping("/{investorId}")
    public String deleteInvestor(@PathVariable Integer investorId) {
        investorService.deleteInvestor(investorId);
        return "Investor deleted successfully.";
    }
}
