package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.exception.GlobalExceptionHandler;
import com.portfolio.portfolio_management.exception.InvestmentNotFoundException;
import com.portfolio.portfolio_management.exception.PortfolioNotFoundException;
import com.portfolio.portfolio_management.model.Investment;
import com.portfolio.portfolio_management.model.InvestmentListItem;
import com.portfolio.portfolio_management.model.InvestmentRequest;
import com.portfolio.portfolio_management.service.InvestmentService;
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

@WebMvcTest(InvestmentController.class)
@Import(GlobalExceptionHandler.class)
class InvestmentControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    InvestmentControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @MockitoBean
    @SuppressWarnings("unused")
    private InvestmentService investmentService;

    private InvestmentRequest sampleRequest() {
        return new InvestmentRequest(3, 2500.0, 2700.0, 0.0, LocalDate.of(2026, 1, 20));
    }

    private Investment sampleInvestment(int id, int portfolioId) {
        return new Investment(id, portfolioId, 3, 2500.0, 2700.0, 0, LocalDate.of(2026, 1, 20));
    }

    @Test
    @DisplayName("GET /portfolios/{id}/investments returns list")
    void getInvestmentsByPortfolioId_returnsList() throws Exception {
        InvestmentListItem item = new InvestmentListItem(
                9,
                1,
                3,
                "AAPL",
                "stocks",
                2500.0,
                2700.0,
                0,
                LocalDate.of(2026, 1, 20)
        );
        given(investmentService.getInvestmentsByPortfolioId(1)).willReturn(List.of(item));

        mockMvc.perform(get("/portfolios/1/investments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].investmentId").value(9));
    }

    @Test
    @DisplayName("GET /portfolios/{id}/investments/{investmentId} returns 404 when missing")
    void getInvestmentById_whenMissing_returnsNotFound() throws Exception {
        given(investmentService.getInvestmentById(1, 404))
                .willThrow(new InvestmentNotFoundException("Investment with ID 404 not found in portfolio 1"));

        mockMvc.perform(get("/portfolios/1/investments/404"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Investment with ID 404 not found in portfolio 1"));
    }

    @Test
    @DisplayName("POST /portfolios/{id}/investments creates investment")
    void addInvestment_whenValid_returnsCreatedInvestment() throws Exception {
        Investment created = sampleInvestment(12, 1);
        given(investmentService.addInvestment(eq(1), any(Investment.class))).willReturn(created);

        mockMvc.perform(post("/portfolios/1/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investmentId").value(12))
                .andExpect(jsonPath("$.portfolioId").value(1));
    }

    @Test
    @DisplayName("POST /portfolios/{id}/investments returns 400 for business rule violation")
    void addInvestment_whenRuleFails_returnsBadRequest() throws Exception {
        given(investmentService.addInvestment(eq(1), any(Investment.class)))
                .willThrow(new IllegalArgumentException("Investment exceeds available portfolio funds."));

        mockMvc.perform(post("/portfolios/1/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Investment exceeds available portfolio funds."));
    }

    @Test
    @DisplayName("POST /portfolios/{id}/investments returns 400 for malformed JSON")
    void addInvestment_whenMalformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/portfolios/1/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad-json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(investmentService);
    }

    @Test
    @DisplayName("PUT /portfolios/{id}/investments/{investmentId} updates investment")
    void updateInvestment_whenValid_returnsUpdatedInvestment() throws Exception {
        Investment updated = sampleInvestment(8, 1);
        given(investmentService.updateInvestment(eq(1), eq(8), any(Investment.class))).willReturn(updated);

        mockMvc.perform(put("/portfolios/1/investments/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investmentId").value(8));
    }

    @Test
    @DisplayName("PUT /portfolios/{id}/investments/{investmentId} returns 404 when portfolio is missing")
    void updateInvestment_whenPortfolioMissing_returnsNotFound() throws Exception {
        given(investmentService.updateInvestment(eq(1), eq(8), any(Investment.class)))
                .willThrow(new PortfolioNotFoundException("Portfolio with ID 1 not found"));

        mockMvc.perform(put("/portfolios/1/investments/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Portfolio with ID 1 not found"));
    }

    @Test
    @DisplayName("DELETE /portfolios/{id}/investments/{investmentId} returns success message")
    void deleteInvestment_whenFound_returnsSuccessMessage() throws Exception {
        doNothing().when(investmentService).deleteInvestment(1, 8);

        mockMvc.perform(delete("/portfolios/1/investments/8"))
                .andExpect(status().isOk())
                .andExpect(content().string("Investment deleted successfully."));
    }

    @Test
    @DisplayName("DELETE /portfolios/{id}/investments/{investmentId} returns 404 when investment missing")
    void deleteInvestment_whenMissing_returnsNotFound() throws Exception {
        doThrow(new InvestmentNotFoundException("Investment with ID 8 not found in portfolio 1"))
                .when(investmentService)
                .deleteInvestment(1, 8);

        mockMvc.perform(delete("/portfolios/1/investments/8"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Investment with ID 8 not found in portfolio 1"));
    }
}

