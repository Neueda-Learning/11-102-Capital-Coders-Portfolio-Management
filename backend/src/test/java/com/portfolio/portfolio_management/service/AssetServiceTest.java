package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Asset;
import com.portfolio.portfolio_management.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    private AssetServiceImpl assetService;

    @BeforeEach
    void setUp() {
        assetService = new AssetServiceImpl(assetRepository);
    }

    @Test
    @DisplayName("getAllAssets returns repository assets")
    void getAllAssets_returnsList() {
        List<Asset> assets = List.of(
                new Asset(1, "Apple", "AAPL", "stocks"),
                new Asset(2, "US Treasury", "UST10Y", "bonds")
        );
        when(assetRepository.getAllAssets()).thenReturn(assets);

        List<Asset> result = assetService.getAllAssets();

        assertEquals(2, result.size());
        assertEquals("Apple", result.get(0).assetName());
        verify(assetRepository).getAllAssets();
    }

    @Test
    @DisplayName("getAllAssets returns empty list when repository is empty")
    void getAllAssets_whenEmpty_returnsEmptyList() {
        when(assetRepository.getAllAssets()).thenReturn(List.of());

        List<Asset> result = assetService.getAllAssets();

        assertEquals(0, result.size());
        verify(assetRepository).getAllAssets();
    }
}

