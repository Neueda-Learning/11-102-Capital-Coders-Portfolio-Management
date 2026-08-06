package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.model.Investment;
import com.portfolio.portfolio_management.model.InvestmentListItem;
import com.portfolio.portfolio_management.model.InvestmentRequest;
import com.portfolio.portfolio_management.model.TradeRequest;
import com.portfolio.portfolio_management.model.TransactionHistory;
import com.portfolio.portfolio_management.service.InvestmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/portfolios/{portfolioId}/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping
    public List<InvestmentListItem> getInvestmentsByPortfolioId(@PathVariable Integer portfolioId) {
        return investmentService.getInvestmentsByPortfolioId(portfolioId);
    }

    @GetMapping("/{investmentId}")
    public Investment getInvestmentById(@PathVariable Integer portfolioId,
                                        @PathVariable Integer investmentId) {
        return investmentService.getInvestmentById(portfolioId, investmentId);
    }

    @PostMapping
    public Investment addInvestment(@PathVariable Integer portfolioId,
                                    @RequestBody InvestmentRequest investmentRequest) {

        Investment investmentToCreate = new Investment(
                0,
                portfolioId,
                investmentRequest.assetId(),
                investmentRequest.amountInvested(),
                investmentRequest.currentValue(),
                investmentRequest.quantity() == null ? 0 : investmentRequest.quantity(),
                investmentRequest.purchaseDate()
        );

        return investmentService.addInvestment(portfolioId, investmentToCreate);
    }

    @PutMapping("/{investmentId}")
    public Investment updateInvestment(@PathVariable Integer portfolioId,
                                       @PathVariable Integer investmentId,
                                       @RequestBody InvestmentRequest investmentRequest) {

        Investment investmentToUpdate = new Investment(
                investmentId,
                portfolioId,
                investmentRequest.assetId(),
                investmentRequest.amountInvested(),
                investmentRequest.currentValue(),
                investmentRequest.quantity() == null ? 0 : investmentRequest.quantity(),
                investmentRequest.purchaseDate()
        );

        return investmentService.updateInvestment(portfolioId, investmentId, investmentToUpdate);
    }

    @DeleteMapping("/{investmentId}")
    public String deleteInvestment(@PathVariable Integer portfolioId,
                                   @PathVariable Integer investmentId) {

        investmentService.deleteInvestment(portfolioId, investmentId);
        return "Investment deleted successfully.";
    }

    @PostMapping("/buy")
    public Investment buy(@PathVariable Integer portfolioId,
                          @RequestBody TradeRequest tradeRequest) {
        return investmentService.buy(portfolioId, tradeRequest);
    }

    @PostMapping("/sell")
    public Investment sell(@PathVariable Integer portfolioId,
                           @RequestBody TradeRequest tradeRequest) {
        return investmentService.sell(portfolioId, tradeRequest);
    }

    @GetMapping("/{investmentId}/transactions")
    public List<TransactionHistory> getTransactions(@PathVariable Integer portfolioId,
                                                     @PathVariable Integer investmentId) {
        return investmentService.getTransactionHistory(portfolioId, investmentId);
    }
}
