package com.portfolio.portfolio_management.controller;

import com.portfolio.portfolio_management.model.Asset;
import com.portfolio.portfolio_management.service.AssetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssetController.class)
class AssetControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    AssetControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @MockitoBean
    @SuppressWarnings("unused")
    private AssetService assetService;

    @Test
    @DisplayName("GET /assets returns all assets")
    void getAllAssets_returnsAssets() throws Exception {
        Asset stocks = new Asset(1, "Apple", "AAPL", "stocks");
        Asset cash = new Asset(2, "USD Cash", "USD", "cash");
        given(assetService.getAllAssets()).willReturn(List.of(stocks, cash));

        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].assetName").value("Apple"));
    }

    @Test
    @DisplayName("GET /assets returns empty list when none exist")
    void getAllAssets_whenEmpty_returnsEmptyList() throws Exception {
        given(assetService.getAllAssets()).willReturn(List.of());

        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}

