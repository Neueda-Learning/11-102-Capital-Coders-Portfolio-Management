package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.InvestmentNotFoundException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Asset;
import com.portfolio.portfolio_management.model.Investment;
import com.portfolio.portfolio_management.model.InvestmentListItem;
import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.model.PortfolioSummary;
import com.portfolio.portfolio_management.repository.AssetRepository;
import com.portfolio.portfolio_management.repository.InvestmentRepository;
import com.portfolio.portfolio_management.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AssetRepository assetRepository;

    private InvestmentServiceImpl investmentService;

    @BeforeEach
    void setUp() {
        investmentService = new InvestmentServiceImpl(investmentRepository, portfolioRepository, assetRepository);
    }

    private Portfolio samplePortfolio(int portfolioId) {
        return new Portfolio(portfolioId, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));
    }

    private Investment sampleInvestment(int investmentId, int portfolioId, int assetId, double amount, double currentValue, LocalDate date) {
        return new Investment(investmentId, portfolioId, assetId, amount, currentValue, date);
    }

    private PortfolioSummary sampleSummary(double totalFunds) {
        return new PortfolioSummary(0, 0, 0, 0, totalFunds, 0, 0, 0, 0, "Neutral");
    }

    @Test
    @DisplayName("getInvestmentsByPortfolioId throws when portfolio does not exist")
    void getInvestmentsByPortfolioId_whenPortfolioMissing_throwsNotFound() {
        when(portfolioRepository.getPortfolioById(77)).thenReturn(Optional.empty());

        PortfolioNotFoundException ex = assertThrows(
                PortfolioNotFoundException.class,
                () -> investmentService.getInvestmentsByPortfolioId(77)
        );

        assertEquals("Portfolio with ID 77 not found", ex.getMessage());
        verify(investmentRepository, never()).getInvestmentsByPortfolioId(77);
    }

    @Test
    @DisplayName("getInvestmentsByPortfolioId returns list when portfolio exists")
    void getInvestmentsByPortfolioId_whenPortfolioExists_returnsList() {
        InvestmentListItem item = new InvestmentListItem(1, 3, 8, "AAPL", "stocks", 2000.0, 2300.0, LocalDate.of(2026, 1, 20));
        when(portfolioRepository.getPortfolioById(3)).thenReturn(Optional.of(samplePortfolio(3)));
        when(investmentRepository.getInvestmentsByPortfolioId(3)).thenReturn(List.of(item));

        List<InvestmentListItem> result = investmentService.getInvestmentsByPortfolioId(3);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).investmentId());
    }

    @Test
    @DisplayName("getInvestmentById throws when investment is missing in portfolio")
    void getInvestmentById_whenMissing_throwsNotFound() {
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));
        when(investmentRepository.getInvestmentByIdAndPortfolioId(404, 1)).thenReturn(Optional.empty());

        InvestmentNotFoundException ex = assertThrows(
                InvestmentNotFoundException.class,
                () -> investmentService.getInvestmentById(1, 404)
        );

        assertEquals("Investment with ID 404 not found in portfolio 1", ex.getMessage());
    }

    @Test
    @DisplayName("addInvestment throws when purchase date is null")
    void addInvestment_whenPurchaseDateNull_throwsBadRequest() {
        Investment candidate = sampleInvestment(0, 1, 3, 1000.0, 1100.0, null);
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> investmentService.addInvestment(1, candidate)
        );

        assertEquals("Purchase date is required.", ex.getMessage());
    }

    @Test
    @DisplayName("addInvestment throws when purchase date is in the future")
    void addInvestment_whenFutureDate_throwsBadRequest() {
        Investment candidate = sampleInvestment(0, 1, 3, 1000.0, 1100.0, LocalDate.now().plusDays(1));
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> investmentService.addInvestment(1, candidate)
        );

        assertEquals("Purchase date cannot be later than today.", ex.getMessage());
    }

    @Test
    @DisplayName("addInvestment throws when amount invested is negative")
    void addInvestment_whenNegativeAmount_throwsBadRequest() {
        Investment candidate = sampleInvestment(0, 1, 3, -1.0, 5.0, LocalDate.of(2026, 1, 10));
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> investmentService.addInvestment(1, candidate)
        );

        assertEquals("Amount invested cannot be negative.", ex.getMessage());
    }

    @Test
    @DisplayName("addInvestment throws when selected asset does not exist")
    void addInvestment_whenAssetMissing_throwsBadRequest() {
        Investment candidate = sampleInvestment(0, 1, 9, 1000.0, 1100.0, LocalDate.of(2026, 1, 10));
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));
        when(assetRepository.getAssetById(9)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> investmentService.addInvestment(1, candidate)
        );

        assertEquals("Selected asset does not exist.", ex.getMessage());
    }

    @Test
    @DisplayName("addInvestment throws when selected asset type is unsupported")
    void addInvestment_whenUnsupportedAssetType_throwsBadRequest() {
        Investment candidate = sampleInvestment(0, 1, 9, 1000.0, 1100.0, LocalDate.of(2026, 1, 10));
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));
        when(assetRepository.getAssetById(9)).thenReturn(Optional.of(new Asset(9, "Crypto", "BTC", "crypto")));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> investmentService.addInvestment(1, candidate)
        );

        assertEquals("Selected asset type is not supported for this portfolio.", ex.getMessage());
    }

    @Test
    @DisplayName("addInvestment throws when candidate exceeds available funds")
    void addInvestment_whenExceedsFunds_throwsBadRequest() {
        Investment candidate = sampleInvestment(0, 1, 3, 1500.0, 1500.0, LocalDate.of(2026, 1, 10));
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));
        when(assetRepository.getAssetById(3)).thenReturn(Optional.of(new Asset(3, "Apple", "AAPL", "stocks")));
        when(portfolioRepository.getPortfolioSummary(1)).thenReturn(Optional.of(sampleSummary(3000.0)));
        when(investmentRepository.getInvestmentsByPortfolioId(1)).thenReturn(List.of(
                new InvestmentListItem(1, 1, 3, "AAPL", "stocks", 2000.0, 2200.0, LocalDate.of(2026, 1, 1))
        ));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> investmentService.addInvestment(1, candidate)
        );

        assertEquals("Investment exceeds available portfolio funds.", ex.getMessage());
        verify(investmentRepository, never()).addInvestment(candidate);
    }

    @Test
    @DisplayName("addInvestment stores investment when rules pass")
    void addInvestment_whenValid_addsInvestment() {
        Investment candidate = sampleInvestment(0, 1, 3, 1000.0, 1100.0, LocalDate.of(2026, 1, 10));
        Investment created = sampleInvestment(8, 1, 3, 1000.0, 1100.0, LocalDate.of(2026, 1, 10));

        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));
        when(assetRepository.getAssetById(3)).thenReturn(Optional.of(new Asset(3, "Apple", "AAPL", "stocks")));
        when(portfolioRepository.getPortfolioSummary(1)).thenReturn(Optional.of(sampleSummary(5000.0)));
        when(investmentRepository.getInvestmentsByPortfolioId(1)).thenReturn(List.of(
                new InvestmentListItem(1, 1, 3, "AAPL", "stocks", 2000.0, 2200.0, LocalDate.of(2026, 1, 1))
        ));
        when(investmentRepository.addInvestment(candidate)).thenReturn(created);

        Investment result = investmentService.addInvestment(1, candidate);

        assertEquals(8, result.investmentId());
        verify(investmentRepository).addInvestment(candidate);
    }

    @Test
    @DisplayName("updateInvestment allows replacing existing amount without false exceed")
    void updateInvestment_whenValid_updatesInvestment() {
        Investment candidate = sampleInvestment(5, 1, 3, 3500.0, 3600.0, LocalDate.of(2026, 2, 1));
        Investment existing = sampleInvestment(5, 1, 3, 4000.0, 3900.0, LocalDate.of(2026, 1, 1));

        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));
        when(investmentRepository.getInvestmentByIdAndPortfolioId(5, 1)).thenReturn(Optional.of(existing));
        when(assetRepository.getAssetById(3)).thenReturn(Optional.of(new Asset(3, "Apple", "AAPL", "stocks")));
        when(portfolioRepository.getPortfolioSummary(1)).thenReturn(Optional.of(sampleSummary(9000.0)));
        when(investmentRepository.getInvestmentsByPortfolioId(1)).thenReturn(List.of(
                new InvestmentListItem(5, 1, 3, "AAPL", "stocks", 4000.0, 3900.0, LocalDate.of(2026, 1, 1)),
                new InvestmentListItem(6, 1, 4, "MSFT", "stocks", 5000.0, 5100.0, LocalDate.of(2026, 1, 1))
        ));
        when(investmentRepository.updateInvestment(5, 1, candidate)).thenReturn(candidate);

        Investment result = investmentService.updateInvestment(1, 5, candidate);

        assertEquals(3500.0, result.amountInvested());
        verify(investmentRepository).updateInvestment(5, 1, candidate);
    }

    @Test
    @DisplayName("deleteInvestment throws when target investment does not exist")
    void deleteInvestment_whenMissing_throwsNotFound() {
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));
        when(investmentRepository.getInvestmentByIdAndPortfolioId(7, 1)).thenReturn(Optional.empty());

        InvestmentNotFoundException ex = assertThrows(
                InvestmentNotFoundException.class,
                () -> investmentService.deleteInvestment(1, 7)
        );

        assertEquals("Investment with ID 7 not found in portfolio 1", ex.getMessage());
        verify(investmentRepository, never()).deleteInvestment(7, 1);
    }

    @Test
    @DisplayName("deleteInvestment deletes when portfolio and investment exist")
    void deleteInvestment_whenFound_deletes() {
        when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(samplePortfolio(1)));
        when(investmentRepository.getInvestmentByIdAndPortfolioId(7, 1))
                .thenReturn(Optional.of(sampleInvestment(7, 1, 3, 1200.0, 1300.0, LocalDate.of(2026, 1, 1))));

        investmentService.deleteInvestment(1, 7);

        verify(investmentRepository).deleteInvestment(7, 1);
    }
}

