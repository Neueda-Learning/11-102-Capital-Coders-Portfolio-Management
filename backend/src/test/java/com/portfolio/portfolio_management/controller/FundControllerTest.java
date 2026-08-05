package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.exception.DuplicateFundException;
import com.portfolio.portfolio_management.exception.FundNotfoundException;
import com.portfolio.portfolio_management.exception.GlobalExceptionHandler;
import com.portfolio.portfolio_management.model.Fund;
import com.portfolio.portfolio_management.model.FundSummary;
import com.portfolio.portfolio_management.service.FundService;
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

import static org.mockito.ArgumentMatchers.any;
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

@WebMvcTest(FundController.class)
@Import(GlobalExceptionHandler.class)
class FundControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    FundControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @MockitoBean
    @SuppressWarnings("unused")
    private FundService fundService;

    private Fund sampleFund(int id, int investorId, String name) {
        return new Fund(id, investorId, name, 10000.0, LocalDate.of(2026, 1, 10), "RECEIVED");
    }

    @Test
    @DisplayName("GET /funds/investor/{id}/total returns total funds")
    void getTotalFundsByInvestor_returnsSummary() throws Exception {
        FundSummary summary = new FundSummary(101, 85000.0);
        given(fundService.getTotalFundsByInvestorId(101)).willReturn(summary);

        mockMvc.perform(get("/funds/investor/101/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investorId").value(101))
                .andExpect(jsonPath("$.totalFundsReceived").value(85000.0));
    }

    @Test
    @DisplayName("GET /funds/investor/{id}/total returns 404 when investor has no fund record")
    void getTotalFundsByInvestor_whenMissing_returnsNotFound() throws Exception {
        given(fundService.getTotalFundsByInvestorId(404))
                .willThrow(new FundNotfoundException(404));

        mockMvc.perform(get("/funds/investor/404/total"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Fund with ID 404 not found."));
    }

    @Test
    @DisplayName("POST /funds/investor/{id} creates fund using path investor id")
    void addNewFund_whenValid_returnsCreatedFund() throws Exception {
        Fund requestBody = sampleFund(0, 999, "Series A");
        Fund created = sampleFund(8, 101, "Series A");
        given(fundService.addnewFund(any(Fund.class))).willReturn(created);

        mockMvc.perform(post("/funds/investor/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fundId").value(8))
                .andExpect(jsonPath("$.investorId").value(101))
                .andExpect(jsonPath("$.fundName").value("Series A"));
    }

    @Test
    @DisplayName("POST /funds/investor/{id} returns 409 for duplicate fund name")
    void addNewFund_whenDuplicateName_returnsConflict() throws Exception {
        Fund requestBody = sampleFund(0, 101, "Series A");
        given(fundService.addnewFund(any(Fund.class)))
                .willThrow(new DuplicateFundException("Series A"));

        mockMvc.perform(post("/funds/investor/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Fund with name 'Series A' already exists"));
    }

    @Test
    @DisplayName("POST /funds/investor/{id} returns 400 for malformed JSON")
    void addNewFund_whenMalformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/funds/investor/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad-json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fundService);
    }

    @Test
    @DisplayName("PUT /funds/{id} updates fund using path fund id")
    void updateFund_whenValid_returnsUpdatedFund() throws Exception {
        Fund requestBody = sampleFund(0, 101, "Series B");
        Fund updated = sampleFund(5, 101, "Series B");
        given(fundService.updateFund(any(Fund.class))).willReturn(updated);

        mockMvc.perform(put("/funds/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fundId").value(5))
                .andExpect(jsonPath("$.fundName").value("Series B"));
    }

    @Test
    @DisplayName("PUT /funds/{id} returns 404 when fund does not exist")
    void updateFund_whenMissing_returnsNotFound() throws Exception {
        Fund requestBody = sampleFund(0, 101, "Series B");
        given(fundService.updateFund(any(Fund.class)))
                .willThrow(new FundNotfoundException(5));

        mockMvc.perform(put("/funds/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Fund with ID 5 not found."));
    }

    @Test
    @DisplayName("DELETE /funds/{id} returns 204 no content")
    void deleteFund_whenFound_returnsNoContent() throws Exception {
        doNothing().when(fundService).deleteFund(6);

        mockMvc.perform(delete("/funds/6"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /funds/{id} returns 404 when fund does not exist")
    void deleteFund_whenMissing_returnsNotFound() throws Exception {
        doThrow(new FundNotfoundException(7)).when(fundService).deleteFund(7);

        mockMvc.perform(delete("/funds/7"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Fund with ID 7 not found."));
    }
}

