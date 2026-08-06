package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.FundNotfoundException;
import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.model.FundSummary;
import com.portfolio.portfolio_management.repository.FundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundServiceTest {

    @Mock
    private FundRepository fundRepository;

    private FundServiceImpl fundService;

    @BeforeEach
    void setUp() {
        fundService = new FundServiceImpl(fundRepository);
    }

    private Fund sampleFund(int fundId, int investorId, String name) {
        return new Fund(fundId, investorId, name, 10000.0, LocalDate.of(2026, 1, 10), "RECEIVED");
    }

    @Test
    @DisplayName("getFundsByInvestorId returns repository funds")
    void getFundsByInvestorId_returnsList() {
        List<Fund> funds = List.of(sampleFund(1, 101, "Series A"), sampleFund(2, 101, "Series B"));
        when(fundRepository.getFundsByInvestorId(101)).thenReturn(funds);

        List<Fund> result = fundService.getFundsByInvestorId(101);

        assertEquals(2, result.size());
        verify(fundRepository).getFundsByInvestorId(101);
    }

    @Test
    @DisplayName("getTotalFundsByInvestorId returns summary")
    void getTotalFundsByInvestorId_returnsSummary() {
        FundSummary summary = new FundSummary(101, 80000.0);
        when(fundRepository.getTotalFundsByInvestorId(101)).thenReturn(summary);

        FundSummary result = fundService.getTotalFundsByInvestorId(101);

        assertEquals(101, result.investorId());
        assertEquals(80000.0, result.totalFundsReceived());
    }

    @Test
    @DisplayName("addnewFund delegates to repository")
    void addnewFund_delegatesToRepository() {
        Fund request = sampleFund(0, 101, "Series C");
        Fund created = sampleFund(8, 101, "Series C");
        when(fundRepository.addnewFund(request)).thenReturn(created);

        Fund result = fundService.addnewFund(request);

        assertEquals(8, result.fundId());
        verify(fundRepository).addnewFund(request);
    }

    @Test
    @DisplayName("updateFund delegates to repository")
    void updateFund_delegatesToRepository() {
        Fund request = sampleFund(3, 101, "Series B Updated");
        when(fundRepository.updateFund(request)).thenReturn(request);

        Fund result = fundService.updateFund(request);

        assertEquals("Series B Updated", result.fundName());
        verify(fundRepository).updateFund(request);
    }

    @Test
    @DisplayName("deleteFund delegates to repository")
    void deleteFund_delegatesToRepository() {
        fundService.deleteFund(5);

        verify(fundRepository).deleteFund(5);
    }

    @Test
    @DisplayName("deleteFund propagates repository not-found error")
    void deleteFund_whenRepositoryThrows_propagatesException() {
        doThrow(new FundNotfoundException(77)).when(fundRepository).deleteFund(77);

        FundNotfoundException ex = assertThrows(
                FundNotfoundException.class,
                () -> fundService.deleteFund(77)
        );

        assertEquals("Fund with ID 77 not found.", ex.getMessage());
    }
}

