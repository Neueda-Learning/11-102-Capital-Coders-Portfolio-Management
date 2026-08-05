package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.exception.DuplicateInvestorEmailException;
import com.portfolio.portfolio_management.exception.GlobalExceptionHandler;
import com.portfolio.portfolio_management.exception.InvestorNotFoundException;
import com.portfolio.portfolio_management.model.Investor;
import com.portfolio.portfolio_management.service.InvestorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

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

@WebMvcTest(InvestorController.class)
@Import(GlobalExceptionHandler.class)
class InvestorControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    InvestorControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @MockitoBean
    @SuppressWarnings("unused")
    private InvestorService investorService;

    private Investor sampleInvestor(int id, String email) {
        return new Investor(id, "Investor A", email);
    }

    @Test
    @DisplayName("GET /investors returns all investors")
    void getAllInvestors_returnsList() throws Exception {
        given(investorService.getAllInvestors()).willReturn(List.of(
                sampleInvestor(1, "a@investor.com"),
                sampleInvestor(2, "b@investor.com")
        ));

        mockMvc.perform(get("/investors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].investorId").value(2));
    }

    @Test
    @DisplayName("GET /investors/{id} returns 404 when investor does not exist")
    void getInvestorById_whenMissing_returnsNotFound() throws Exception {
        given(investorService.getInvestorById(9))
                .willThrow(new InvestorNotFoundException(9));

        mockMvc.perform(get("/investors/9"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Investor with ID 9 not found."));
    }

    @Test
    @DisplayName("POST /investors creates investor")
    void addInvestor_whenValid_returnsCreatedInvestor() throws Exception {
        Investor request = sampleInvestor(0, "new@investor.com");
        Investor created = sampleInvestor(10, "new@investor.com");
        given(investorService.addInvestor(any(Investor.class))).willReturn(created);

        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investorId").value(10));
    }

    @Test
    @DisplayName("POST /investors returns 409 when email already exists")
    void addInvestor_whenDuplicateEmail_returnsConflict() throws Exception {
        Investor request = sampleInvestor(0, "dup@investor.com");
        given(investorService.addInvestor(any(Investor.class)))
                .willThrow(new DuplicateInvestorEmailException("dup@investor.com"));

        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Investor with email 'dup@investor.com' already exists."));
    }

    @Test
    @DisplayName("POST /investors returns 400 for malformed JSON")
    void addInvestor_whenMalformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad-json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(investorService);
    }

    @Test
    @DisplayName("PUT /investors/{id} updates investor")
    void updateInvestor_whenValid_returnsUpdatedInvestor() throws Exception {
        Investor request = sampleInvestor(0, "updated@investor.com");
        Investor updated = sampleInvestor(3, "updated@investor.com");
        given(investorService.updateInvestor(eq(3), any(Investor.class))).willReturn(updated);

        mockMvc.perform(put("/investors/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investorId").value(3));
    }

    @Test
    @DisplayName("PUT /investors/{id} returns 404 when investor does not exist")
    void updateInvestor_whenMissing_returnsNotFound() throws Exception {
        Investor request = sampleInvestor(0, "updated@investor.com");
        given(investorService.updateInvestor(eq(88), any(Investor.class)))
                .willThrow(new InvestorNotFoundException(88));

        mockMvc.perform(put("/investors/88")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Investor with ID 88 not found."));
    }

    @Test
    @DisplayName("DELETE /investors/{id} returns success message")
    void deleteInvestor_whenFound_returnsSuccessMessage() throws Exception {
        doNothing().when(investorService).deleteInvestor(4);

        mockMvc.perform(delete("/investors/4"))
                .andExpect(status().isOk())
                .andExpect(content().string("Investor deleted successfully."));
    }

    @Test
    @DisplayName("DELETE /investors/{id} returns 404 when investor does not exist")
    void deleteInvestor_whenMissing_returnsNotFound() throws Exception {
        doThrow(new InvestorNotFoundException(55)).when(investorService).deleteInvestor(55);

        mockMvc.perform(delete("/investors/55"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Investor with ID 55 not found."));
    }
}

