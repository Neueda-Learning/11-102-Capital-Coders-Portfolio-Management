/* =====================================================================
   PORTFOLIO PERFORMANCE SEED DATA
   ---------------------------------------------------------------------
   Purpose : Gives PortfolioServiceImpl.getPortfolioPerformance() enough
             history to produce meaningful, non-flat charts for all
             three buckets it builds:
                 - monthly   -> last 6 months
                 - quarterly -> last 4 quarters
                 - yearly    -> last 5 years

   How it works with the existing code:
     - getPerformanceTransactions() replays every transaction_history
       row (dated on/before each period's end) to rebuild invested
       capital and quantity held, per asset, as of that date.
     - getCurrentPricePerAsset() derives "latest known price" as
       investment.current_value / investment.quantity. That single
       price is used to value the holding at EVERY period end (there's
       no historical price feed), so we set current_value/quantity on
       the investment rows to realistic-looking "current" prices for
       AAPL / MSFT / VOO. These are synthetic test prices, not real
       market data.

   This script is idempotent (safe to re-run) and additive: it creates
   ONE new investor/employee/fund/portfolio dedicated to performance
   testing, so it won't disturb the portfolios already seeded in
   data.sql. Run it after schema.sql + data.sql.
   ===================================================================== */

USE portfolio_management;

/* =====================================================
   EMPLOYEE
   ===================================================== */

INSERT INTO employee (employee_name, email, department)
SELECT 'Grace Kim', 'grace.kim@companya.com', 'Investment Analysis'
WHERE NOT EXISTS (
    SELECT 1 FROM employee WHERE email = 'grace.kim@companya.com'
);


/* =====================================================
   INVESTOR
   ===================================================== */

INSERT INTO investor (investor_name, contact_email)
SELECT 'Meridian Performance Partners', 'contact@meridianperformance.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'contact@meridianperformance.com'
);


/* =====================================================
   FUND
   ===================================================== */

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT investor_id, 'Meridian Growth Fund', 150000, '2022-01-05', 'ACTIVE'
FROM investor
WHERE contact_email = 'contact@meridianperformance.com'
AND NOT EXISTS (
    SELECT 1 FROM fund WHERE fund_name = 'Meridian Growth Fund'
);


/* =====================================================
   PORTFOLIO
   ===================================================== */

INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'grace.kim@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'contact@meridianperformance.com'),
    'Performance Test Portfolio',
    'Long-running portfolio seeded specifically to exercise the 6-month / 4-quarter / 5-year performance charts',
    'MEDIUM',
    120000.00,
    '2022-01-10'
WHERE NOT EXISTS (
    SELECT 1 FROM portfolio
    WHERE investor_id = (
        SELECT investor_id FROM investor WHERE contact_email = 'contact@meridianperformance.com'
    )
);


/* =====================================================
   INVESTMENT
   One row per (portfolio, asset). amount_invested / current_value /
   quantity below reflect the NET result of all transactions inserted
   further down (buys minus sells), so the portfolio summary screen
   and the performance screen agree with each other.

   "Current price" implied by current_value / quantity:
       AAPL -> $250.00   MSFT -> $440.00   VOO -> $525.00
   ===================================================== */

-- AAPL: net qty 165, net invested $30,250, valued at $250/unit -> $41,250
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, quantity, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Performance Test Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL'),
    30250.00,
    41250.00,
    165,
    '2022-03-15'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Performance Test Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL')
);

-- MSFT: net qty 98, net invested $34,965, valued at $440/unit -> $43,120
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, quantity, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Performance Test Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'MSFT'),
    34965.00,
    43120.00,
    98,
    '2022-09-10'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Performance Test Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'MSFT')
);

-- VOO: net qty 62, net invested $28,280, valued at $525/unit -> $32,550
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, quantity, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Performance Test Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'VOO'),
    28280.00,
    32550.00,
    62,
    '2023-08-05'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Performance Test Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'VOO')
);


/* =====================================================
   TRANSACTION HISTORY
   Spans 2022 -> today so the 5-year yearly view has 5 distinct data
   points, the 4-quarter view has real quarter-over-quarter movement,
   and the 6-month view (Mar 2026 - Aug 2026) has multiple buys/sells.
   ===================================================== */

-- ---------- AAPL ----------
INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 8000.00, 50, 160.00, '2022-03-15'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'AAPL'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2022-03-15' AND th.amount = 8000.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 6000.00, 40, 150.00, '2023-02-20'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'AAPL'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2023-02-20' AND th.amount = 6000.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 5700.00, 30, 190.00, '2024-07-22'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'AAPL'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2024-07-22' AND th.amount = 5700.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'SELL', 4500.00, 20, 225.00, '2024-11-30'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'AAPL'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2024-11-30' AND th.amount = 4500.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 8050.00, 35, 230.00, '2025-10-05'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'AAPL'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2025-10-05' AND th.amount = 8050.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 5875.00, 25, 235.00, '2026-02-14'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'AAPL'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-02-14' AND th.amount = 5875.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 4800.00, 20, 240.00, '2026-05-20'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'AAPL'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-05-20' AND th.amount = 4800.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'SELL', 3675.00, 15, 245.00, '2026-07-08'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'AAPL'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-07-08' AND th.amount = 3675.00
);

-- ---------- MSFT ----------
INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 7500.00, 30, 250.00, '2022-09-10'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'MSFT'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2022-09-10' AND th.amount = 7500.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 7400.00, 20, 370.00, '2024-01-15'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'MSFT'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2024-01-15' AND th.amount = 7400.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 10375.00, 25, 415.00, '2025-06-18'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'MSFT'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2025-06-18' AND th.amount = 10375.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'SELL', 4300.00, 10, 430.00, '2025-12-20'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'MSFT'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2025-12-20' AND th.amount = 4300.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 6300.00, 15, 420.00, '2026-03-05'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'MSFT'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-03-05' AND th.amount = 6300.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 4250.00, 10, 425.00, '2026-06-15'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'MSFT'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-06-15' AND th.amount = 4250.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 3440.00, 8, 430.00, '2026-08-02'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'MSFT'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-08-02' AND th.amount = 3440.00
);

-- ---------- VOO ----------
INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 10000.00, 25, 400.00, '2023-08-05'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'VOO'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2023-08-05' AND th.amount = 10000.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 7200.00, 15, 480.00, '2025-03-10'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'VOO'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2025-03-10' AND th.amount = 7200.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 10000.00, 20, 500.00, '2026-01-10'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'VOO'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-01-10' AND th.amount = 10000.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'SELL', 5100.00, 10, 510.00, '2026-04-12'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'VOO'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-04-12' AND th.amount = 5100.00
);

INSERT INTO transaction_history (investment_id, transaction_type, amount, quantity, price_per_unit, transaction_date)
SELECT i.investment_id, 'BUY', 6180.00, 12, 515.00, '2026-07-25'
FROM investment i
JOIN portfolio p ON p.portfolio_id = i.portfolio_id AND p.portfolio_name = 'Performance Test Portfolio'
JOIN asset a ON a.asset_id = i.asset_id AND a.ticker_symbol = 'VOO'
WHERE NOT EXISTS (
    SELECT 1 FROM transaction_history th
    WHERE th.investment_id = i.investment_id AND th.transaction_date = '2026-07-25' AND th.amount = 6180.00
);

/* =====================================================
   Quick sanity check after running this script:

   SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Performance Test Portfolio';
   -- then call GET /api/portfolios/{that_id}/performance
   ===================================================== */