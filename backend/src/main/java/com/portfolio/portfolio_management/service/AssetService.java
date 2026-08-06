package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Asset;
import com.portfolio.portfolio_management.model.LiveMarketPrice;

import java.util.List;

public interface AssetService {

    List<Asset> getAllAssets();

    LiveMarketPrice getLivePriceByAssetId(Integer assetId);
}

