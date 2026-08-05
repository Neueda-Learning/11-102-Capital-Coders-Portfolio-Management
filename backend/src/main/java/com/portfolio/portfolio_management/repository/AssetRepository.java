package com.portfolio.portfolio_management.repository;

import com.portfolio.portfolio_management.model.Asset;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AssetRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Asset> ASSET_ROW_MAPPER = (rs, rowNum) ->
            new Asset(
                    rs.getInt("asset_id"),
                    rs.getString("asset_name"),
                    rs.getString("ticker_symbol"),
                    rs.getString("asset_type")
            );

    public List<Asset> getAllAssets() {
        String sql = "SELECT * FROM asset ORDER BY asset_name";
        return jdbcTemplate.query(sql, ASSET_ROW_MAPPER);
    }

    public Optional<Asset> getAssetById(Integer assetId) {
        String sql = "SELECT * FROM asset WHERE asset_id = ?";
        List<Asset> assets = jdbcTemplate.query(sql, ASSET_ROW_MAPPER, assetId);
        return assets.stream().findFirst();
    }
}

