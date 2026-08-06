package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Asset;
import com.portfolio.portfolio_management.model.LiveMarketPrice;
import com.portfolio.portfolio_management.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final MarketPriceService marketPriceService;

    public AssetServiceImpl(AssetRepository assetRepository,
                            MarketPriceService marketPriceService) {
        this.assetRepository = assetRepository;
        this.marketPriceService = marketPriceService;
    }

    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.getAllAssets();
    }

    @Override
    public LiveMarketPrice getLivePriceByAssetId(Integer assetId) {
        Asset asset = assetRepository.getAssetById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Selected asset does not exist."));
        double livePrice = marketPriceService.getLatestPrice(asset.tickerSymbol());
        return new LiveMarketPrice(asset.assetId(), asset.tickerSymbol(), livePrice);
    }
}

