package com.portfolio.portfolio_management.service;

import com.portfolio.portfolio_management.model.Asset;
import com.portfolio.portfolio_management.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.getAllAssets();
    }
}

