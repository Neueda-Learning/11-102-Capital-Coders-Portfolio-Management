package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.exception.DuplicatePortfolioException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Portfolio;
import com.portfolio.portfolio_management.model.PortfolioSummary;
import com.portfolio.portfolio_management.service.PortfolioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
@Import(com.portfolio.portfolio_management.exception.GlobalExceptionHandler.class)
class PortfolioControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    PortfolioControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @MockitoBean
    @SuppressWarnings("unused")
    private PortfolioService portfolioService;

    private Portfolio samplePortfolio(int portfolioId, int employeeId, int investorId, String name) {
        return new Portfolio(
                portfolioId,
                employeeId,
                investorId,
                name,
                "Portfolio description",
                "MEDIUM",
                50000.0,
                LocalDate.of(2026, 1, 1)
        );
    }

    @Test
    @DisplayName("GET /portfolios returns all portfolios")
    void getAllPortfolios_returnsOkAndList() throws Exception {
        Portfolio p1 = samplePortfolio(1, 10, 100, "Growth");
        Portfolio p2 = samplePortfolio(2, 11, 101, "Income");

        given(portfolioService.getAllPortfolios()).willReturn(List.of(p1, p2));

        mockMvc.perform(get("/portfolios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].portfolioId").value(1))
                .andExpect(jsonPath("$[0].portfolioName").value("Growth"));
    }

    @Test
    @DisplayName("GET /portfolios/{id} returns 404 when portfolio is missing")
    void getPortfolioById_whenMissing_returnsNotFound() throws Exception {
        given(portfolioService.getPortfolioById(99))
                .willThrow(new PortfolioNotFoundException("Portfolio with ID 99 not found"));

        mockMvc.perform(get("/portfolios/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Portfolio with ID 99 not found"));
    }

    @Test
    @DisplayName("GET /portfolios/employees/{employeeId} returns empty list when no portfolios")
    void getPortfoliosByEmployeeId_whenNone_returnsEmptyList() throws Exception {
        given(portfolioService.getPortfoliosByEmployeeId(45)).willReturn(List.of());

        mockMvc.perform(get("/portfolios/employees/45"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("POST /portfolios creates portfolio successfully")
    void addPortfolio_whenValid_returnsCreatedPortfolio() throws Exception {
        Portfolio request = samplePortfolio(0, 10, 100, "Growth");
        Portfolio created = samplePortfolio(7, 10, 100, "Growth");

        given(portfolioService.addPortfolio(any(Portfolio.class))).willReturn(created);

        mockMvc.perform(post("/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolioId").value(7))
                .andExpect(jsonPath("$.portfolioName").value("Growth"))
                .andExpect(jsonPath("$.investorId").value(100));
    }

    @Test
    @DisplayName("POST /portfolios duplicate investor returns 409 with message")
    void addPortfolio_duplicateInvestor_returnsConflict() throws Exception {
        Portfolio request = samplePortfolio(0, 10, 100, "Growth");

        given(portfolioService.addPortfolio(any(Portfolio.class)))
                .willThrow(new DuplicatePortfolioException("Investor already has a portfolio"));

        mockMvc.perform(post("/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Investor already has a portfolio"));
    }

    @Test
    @DisplayName("POST /portfolios returns 400 for malformed JSON")
    void addPortfolio_whenMalformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(portfolioService);
    }

    @Test
    @DisplayName("PUT /portfolios/{id} updates portfolio successfully")
    void updatePortfolio_whenValid_returnsUpdatedPortfolio() throws Exception {
        Portfolio request = samplePortfolio(0, 10, 100, "Growth Updated");
        Portfolio updated = samplePortfolio(1, 10, 100, "Growth Updated");

        given(portfolioService.updatePortfolio(eq(1), any(Portfolio.class))).willReturn(updated);

        mockMvc.perform(put("/portfolios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolioId").value(1))
                .andExpect(jsonPath("$.portfolioName").value("Growth Updated"));
    }

    @Test
    @DisplayName("PUT /portfolios/{id} returns 404 when portfolio is missing")
    void updatePortfolio_whenMissing_returnsNotFound() throws Exception {
        Portfolio request = samplePortfolio(0, 10, 100, "Growth Updated");

        given(portfolioService.updatePortfolio(eq(88), any(Portfolio.class)))
                .willThrow(new PortfolioNotFoundException("Portfolio with ID 88 not found"));

        mockMvc.perform(put("/portfolios/88")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Portfolio with ID 88 not found"));
    }

    @Test
    @DisplayName("DELETE /portfolios/{id} returns success message")
    void deletePortfolio_returnsSuccessMessage() throws Exception {
        doNothing().when(portfolioService).deletePortfolio(eq(1));

        mockMvc.perform(delete("/portfolios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Portfolio deleted successfully."));
    }

    @Test
    @DisplayName("DELETE /portfolios/{id} returns 404 when portfolio is missing")
    void deletePortfolio_whenMissing_returnsNotFound() throws Exception {
        doThrow(new PortfolioNotFoundException("Portfolio with ID 77 not found"))
                .when(portfolioService)
                .deletePortfolio(77);

        mockMvc.perform(delete("/portfolios/77"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Portfolio with ID 77 not found"));
    }

    @Test
    @DisplayName("GET /portfolios/{id}/investor-id returns map with IDs")
    void getInvestorIdByPortfolioId_returnsInvestorMap() throws Exception {
        given(portfolioService.getInvestorIdByPortfolioId(3)).willReturn(301);

        mockMvc.perform(get("/portfolios/3/investor-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolioId").value(3))
                .andExpect(jsonPath("$.investorId").value(301));
    }

    @Test
    @DisplayName("GET /portfolios/{id}/investor-id returns 404 when portfolio is missing")
    void getInvestorIdByPortfolioId_whenMissing_returnsNotFound() throws Exception {
        given(portfolioService.getInvestorIdByPortfolioId(99))
                .willThrow(new PortfolioNotFoundException("Portfolio with ID 99 not found"));

        mockMvc.perform(get("/portfolios/99/investor-id"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Portfolio with ID 99 not found"));
    }

    @Test
    @DisplayName("GET /portfolios/{id}/summary returns derived summary payload")
    void getPortfolioSummary_returnsSummary() throws Exception {
        PortfolioSummary summary = new PortfolioSummary(
                1000,
                2000,
                1500,
                500,
                20000,
                5000,
                15000,
                600,
                12,
                "Gain"
        );
        given(portfolioService.getPortfolioSummary(5)).willReturn(summary);

        mockMvc.perform(get("/portfolios/5/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFunds").value(20000.0))
                .andExpect(jsonPath("$.usedFunds").value(5000.0))
                .andExpect(jsonPath("$.gainLossLabel").value("Gain"));
    }

    @Test
    @DisplayName("GET /portfolios/{id}/summary returns 404 when portfolio is missing")
    void getPortfolioSummary_whenMissing_returnsNotFound() throws Exception {
        given(portfolioService.getPortfolioSummary(77))
                .willThrow(new PortfolioNotFoundException("Portfolio with ID 77 not found"));

        mockMvc.perform(get("/portfolios/77/summary"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Portfolio with ID 77 not found"));
    }
}