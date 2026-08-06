-- Run this ONCE against an existing portfolio_management database that was
-- created before buy/sell support was added. A brand-new database created
-- from schema.sql already has these columns and does not need this file.

USE portfolio_management;

ALTER TABLE investment
    ADD COLUMN IF NOT EXISTS quantity DECIMAL(18,6) NOT NULL DEFAULT 0;

ALTER TABLE investment
    ADD CONSTRAINT uq_portfolio_asset UNIQUE (portfolio_id, asset_id);

ALTER TABLE transaction_history
    ADD COLUMN IF NOT EXISTS quantity DECIMAL(18,6),
    ADD COLUMN IF NOT EXISTS price_per_unit DECIMAL(15,4);
