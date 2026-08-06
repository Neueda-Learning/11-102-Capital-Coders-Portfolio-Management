package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.exception.DuplicatePortfolioException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.model.PortfolioSummary;
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
public class PortfolioServiceTest {

	@Mock
	private PortfolioRepository portfolioRepository;

	private PortfolioServiceImpl portfolioService;

	@BeforeEach
	void setUp() {
		portfolioService = new PortfolioServiceImpl(portfolioRepository);
	}

	@Test
	@DisplayName("getAllPortfolios returns all repository portfolios")
	void getAllPortfolios_returnsRepositoryResult() {
		Portfolio p1 = new Portfolio(1, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));
		Portfolio p2 = new Portfolio(2, 11, 101, "Income", "Dividend", "LOW", 30000.0, LocalDate.of(2026, 2, 1));
		when(portfolioRepository.getAllPortfolios()).thenReturn(List.of(p1, p2));

		List<Portfolio> result = portfolioService.getAllPortfolios();

		assertEquals(2, result.size());
		assertEquals("Growth", result.get(0).portfolioName());
	}

	@Test
	@DisplayName("getPortfolioById returns portfolio when found")
	void getPortfolioById_whenFound_returnsPortfolio() {
		Portfolio portfolio = new Portfolio(1, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));
		when(portfolioRepository.getPortfolioById(1)).thenReturn(Optional.of(portfolio));

		Portfolio result = portfolioService.getPortfolioById(1);

		assertEquals(1, result.portfolioId());
		assertEquals("Growth", result.portfolioName());
	}

	@Test
	@DisplayName("getPortfolioById throws not found when missing")
	void getPortfolioById_whenMissing_throwsNotFound() {
		when(portfolioRepository.getPortfolioById(99)).thenReturn(Optional.empty());

		PortfolioNotFoundException ex = assertThrows(
				PortfolioNotFoundException.class,
				() -> portfolioService.getPortfolioById(99)
		);

		assertEquals("Portfolio with ID 99 not found", ex.getMessage());
	}

	@Test
	@DisplayName("addPortfolio throws conflict when investor already has one")
	void addPortfolio_whenDuplicateInvestor_throwsDuplicateException() {
		Portfolio request = new Portfolio(0, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));
		when(portfolioRepository.existsByInvestorId(100)).thenReturn(true);

		DuplicatePortfolioException ex = assertThrows(
				DuplicatePortfolioException.class,
				() -> portfolioService.addPortfolio(request)
		);

		assertEquals("Investor already has a portfolio", ex.getMessage());
		verify(portfolioRepository, never()).addPortfolio(request);
	}

	@Test
	@DisplayName("addPortfolio persists portfolio when investor is unique")
	void addPortfolio_whenUniqueInvestor_addsPortfolio() {
		Portfolio request = new Portfolio(0, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));
		Portfolio saved = new Portfolio(1, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));

		when(portfolioRepository.existsByInvestorId(100)).thenReturn(false);
		when(portfolioRepository.addPortfolio(request)).thenReturn(saved);

		Portfolio result = portfolioService.addPortfolio(request);

		assertEquals(1, result.portfolioId());
		verify(portfolioRepository).addPortfolio(request);
	}

	@Test
	@DisplayName("updatePortfolio throws not found when target does not exist")
	void updatePortfolio_whenMissing_throwsNotFound() {
		Portfolio request = new Portfolio(0, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));
		when(portfolioRepository.getPortfolioById(7)).thenReturn(Optional.empty());

		PortfolioNotFoundException ex = assertThrows(
				PortfolioNotFoundException.class,
				() -> portfolioService.updatePortfolio(7, request)
		);

		assertEquals("Portfolio with ID 7 not found", ex.getMessage());
		verify(portfolioRepository, never()).updatePortfolio(7, request);
	}

	@Test
	@DisplayName("updatePortfolio updates and returns portfolio when target exists")
	void updatePortfolio_whenFound_updatesPortfolio() {
		Portfolio existing = new Portfolio(5, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));
		Portfolio request = new Portfolio(0, 10, 100, "Growth Updated", "Long-term", "MEDIUM", 52000.0, LocalDate.of(2026, 1, 1));
		Portfolio updated = new Portfolio(5, 10, 100, "Growth Updated", "Long-term", "MEDIUM", 52000.0, LocalDate.of(2026, 1, 1));

		when(portfolioRepository.getPortfolioById(5)).thenReturn(Optional.of(existing));
		when(portfolioRepository.updatePortfolio(5, request)).thenReturn(updated);

		Portfolio result = portfolioService.updatePortfolio(5, request);

		assertEquals("Growth Updated", result.portfolioName());
		verify(portfolioRepository).updatePortfolio(5, request);
	}

	@Test
	@DisplayName("deletePortfolio calls repository delete when found")
	void deletePortfolio_whenFound_deletesPortfolio() {
		Portfolio existing = new Portfolio(2, 11, 101, "Income", "Dividend", "LOW", 30000.0, LocalDate.of(2026, 2, 1));
		when(portfolioRepository.getPortfolioById(2)).thenReturn(Optional.of(existing));

		portfolioService.deletePortfolio(2);

		verify(portfolioRepository).deletePortfolio(2);
	}

	@Test
	@DisplayName("deletePortfolio throws not found when target does not exist")
	void deletePortfolio_whenMissing_throwsNotFound() {
		when(portfolioRepository.getPortfolioById(88)).thenReturn(Optional.empty());

		PortfolioNotFoundException ex = assertThrows(
				PortfolioNotFoundException.class,
				() -> portfolioService.deletePortfolio(88)
		);

		assertEquals("Portfolio with ID 88 not found", ex.getMessage());
		verify(portfolioRepository, never()).deletePortfolio(88);
	}

	@Test
	@DisplayName("getPortfoliosByEmployeeId returns employee portfolios")
	void getPortfoliosByEmployeeId_returnsList() {
		Portfolio p1 = new Portfolio(1, 10, 100, "Growth", "Long-term", "MEDIUM", 50000.0, LocalDate.of(2026, 1, 1));
		Portfolio p2 = new Portfolio(2, 10, 101, "Income", "Dividend", "LOW", 30000.0, LocalDate.of(2026, 2, 1));
		when(portfolioRepository.getPortfoliosByEmployeeId(10)).thenReturn(List.of(p1, p2));

		List<Portfolio> result = portfolioService.getPortfoliosByEmployeeId(10);

		assertEquals(2, result.size());
	}

	@Test
	@DisplayName("getInvestorIdByPortfolioId returns investor id when found")
	void getInvestorIdByPortfolioId_whenFound_returnsInvestorId() {
		when(portfolioRepository.getInvestorIdByPortfolioId(3)).thenReturn(Optional.of(301));

		Integer result = portfolioService.getInvestorIdByPortfolioId(3);

		assertEquals(301, result);
	}

	@Test
	@DisplayName("getInvestorIdByPortfolioId throws not found when missing")
	void getInvestorIdByPortfolioId_whenMissing_throwsNotFound() {
		when(portfolioRepository.getInvestorIdByPortfolioId(44)).thenReturn(Optional.empty());

		PortfolioNotFoundException ex = assertThrows(
				PortfolioNotFoundException.class,
				() -> portfolioService.getInvestorIdByPortfolioId(44)
		);

		assertEquals("Portfolio with ID 44 not found", ex.getMessage());
	}

	@Test
	@DisplayName("getPortfolioSummary computes available funds, return percent, and gain label")
	void getPortfolioSummary_computesDerivedFields() {
		PortfolioSummary base = new PortfolioSummary(1000, 500, 300, 200, 10000, 5000, 0, 500, 0, "Neutral");
		when(portfolioRepository.getPortfolioSummary(1)).thenReturn(Optional.of(base));

		PortfolioSummary result = portfolioService.getPortfolioSummary(1);

		assertEquals(5000, result.availableFunds());
		assertEquals(10.0, result.returnPercent());
		assertEquals("Gain", result.gainLossLabel());
	}

	@Test
	@DisplayName("getPortfolioSummary returns zero return percent when used funds is zero")
	void getPortfolioSummary_whenUsedFundsZero_returnsZeroPercent() {
		PortfolioSummary base = new PortfolioSummary(0, 0, 0, 0, 10000, 0, 0, 250, 0, "Neutral");
		when(portfolioRepository.getPortfolioSummary(9)).thenReturn(Optional.of(base));

		PortfolioSummary result = portfolioService.getPortfolioSummary(9);

		assertEquals(0.0, result.returnPercent(), 0.0001);
		assertEquals("Gain", result.gainLossLabel());
	}

	@Test
	@DisplayName("getPortfolioSummary sets loss label when gainLoss is negative")
	void getPortfolioSummary_whenNegativeGainLoss_setsLossLabel() {
		PortfolioSummary base = new PortfolioSummary(100, 100, 100, 100, 2000, 1000, 0, -120, 0, "Neutral");
		when(portfolioRepository.getPortfolioSummary(10)).thenReturn(Optional.of(base));

		PortfolioSummary result = portfolioService.getPortfolioSummary(10);

		assertEquals("Loss", result.gainLossLabel());
		assertEquals(-12.0, result.returnPercent(), 0.0001);
	}

	@Test
	@DisplayName("getPortfolioSummary keeps neutral label when gainLoss is zero")
	void getPortfolioSummary_whenZeroGainLoss_setsNeutralLabel() {
		PortfolioSummary base = new PortfolioSummary(100, 100, 100, 100, 2000, 1000, 0, 0, 0, "Neutral");
		when(portfolioRepository.getPortfolioSummary(11)).thenReturn(Optional.of(base));

		PortfolioSummary result = portfolioService.getPortfolioSummary(11);

		assertEquals("Neutral", result.gainLossLabel());
		assertEquals(0.0, result.returnPercent(), 0.0001);
	}

	@Test
	@DisplayName("getPortfolioSummary throws not found when summary is missing")
	void getPortfolioSummary_whenMissing_throwsNotFound() {
		when(portfolioRepository.getPortfolioSummary(404)).thenReturn(Optional.empty());

		PortfolioNotFoundException ex = assertThrows(
				PortfolioNotFoundException.class,
				() -> portfolioService.getPortfolioSummary(404)
		);

		assertEquals("Portfolio with ID 404 not found", ex.getMessage());
	}
}
