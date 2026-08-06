USE portfolio_management;

/* =====================================================
   EMPLOYEE
   ===================================================== */

INSERT INTO employee (employee_name, email, department)
SELECT 'Alice Johnson', 'alice.johnson@companya.com', 'Portfolio Operations'
WHERE NOT EXISTS (
    SELECT 1 FROM employee
    WHERE email = 'alice.johnson@companya.com'
);


INSERT INTO employee (employee_name, email, department)
SELECT 'Brian Smith', 'brian.smith@companya.com', 'Risk Management'
WHERE NOT EXISTS (
    SELECT 1 FROM employee
    WHERE email = 'brian.smith@companya.com'
);


INSERT INTO employee (employee_name, email, department)
SELECT 'Catherine Lee', 'catherine.lee@companya.com', 'Client Services'
WHERE NOT EXISTS (
    SELECT 1 FROM employee
    WHERE email = 'catherine.lee@companya.com'
);


INSERT INTO employee (employee_name, email, department)
SELECT 'David Wilson', 'david.wilson@companya.com', 'Investment Analysis'
WHERE NOT EXISTS (
    SELECT 1 FROM employee
    WHERE email = 'david.wilson@companya.com'
);


INSERT INTO employee (employee_name, email, department)
SELECT 'Emma Davis', 'emma.davis@companya.com', 'Portfolio Management'
WHERE NOT EXISTS (
    SELECT 1 FROM employee
    WHERE email = 'emma.davis@companya.com'
);


/* =====================================================
   INVESTOR
   ===================================================== */


INSERT INTO investor (investor_name, contact_email)
SELECT 'BlueStone Capital', 'contact@bluestonecapital.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor
    WHERE contact_email='contact@bluestonecapital.com'
);


INSERT INTO investor (investor_name, contact_email)
SELECT 'NorthBridge Holdings', 'info@northbridgeholdings.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor
    WHERE contact_email='info@northbridgeholdings.com'
);


INSERT INTO investor (investor_name, contact_email)
SELECT 'Summit Equity Partners', 'hello@summitequity.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor
    WHERE contact_email='hello@summitequity.com'
);


INSERT INTO investor (investor_name, contact_email)
SELECT 'Pioneer Wealth Partners', 'contact@pioneerwealth.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor
    WHERE contact_email='contact@pioneerwealth.com'
);


INSERT INTO investor (investor_name, contact_email)
SELECT 'Vertex Capital Group', 'hello@vertexcapital.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor
    WHERE contact_email='hello@vertexcapital.com'
);


INSERT INTO investor (investor_name, contact_email)
SELECT 'Aurora Asset Management', 'info@auroraam.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor
    WHERE contact_email='info@auroraam.com'
);


INSERT INTO investor (investor_name, contact_email)
SELECT 'Crestline Global', 'support@crestlineglobal.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor
    WHERE contact_email='support@crestlineglobal.com'
);


INSERT INTO investor (investor_name, contact_email)
SELECT 'Horizon Strategic Funds', 'admin@horizonfunds.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor
    WHERE contact_email='admin@horizonfunds.com'
);



/* =====================================================
   ASSET
   ===================================================== */


INSERT INTO asset(asset_name,ticker_symbol,asset_type)
SELECT 'Apple Inc.','AAPL','stocks'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol='AAPL'
);


INSERT INTO asset(asset_name,ticker_symbol,asset_type)
SELECT 'Microsoft Corporation','MSFT','stocks'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol='MSFT'
);


INSERT INTO asset(asset_name,ticker_symbol,asset_type)
SELECT 'iShares 7-10 Year Treasury Bond ETF','IEF','bonds'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol='IEF'
);


INSERT INTO asset(asset_name,ticker_symbol,asset_type)
SELECT 'Vanguard S&P 500 ETF','VOO','mutual funds'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol='VOO'
);


INSERT INTO asset(asset_name,ticker_symbol,asset_type)
SELECT 'Gold ETF','GLD','mutual funds'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol='GLD'
);


INSERT INTO asset(asset_name,ticker_symbol,asset_type)
SELECT 'INR Cash Reserve','CASH-INR','cash'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol='CASH-INR'
);



/* =====================================================
   FUNDS
   ===================================================== */


INSERT INTO fund
(investor_id,fund_name,amount_received,received_date,status)

SELECT investor_id,'BlueStone Growth Fund',500000,'2026-01-15','ACTIVE'
FROM investor
WHERE contact_email='contact@bluestonecapital.com'
AND NOT EXISTS(
SELECT 1 FROM fund WHERE fund_name='BlueStone Growth Fund'
);



INSERT INTO fund
(investor_id,fund_name,amount_received,received_date,status)

SELECT investor_id,'NorthBridge Income Fund',350000,'2026-02-10','ACTIVE'
FROM investor
WHERE contact_email='info@northbridgeholdings.com'
AND NOT EXISTS(
SELECT 1 FROM fund WHERE fund_name='NorthBridge Income Fund'
);



INSERT INTO fund
(investor_id,fund_name,amount_received,received_date,status)

SELECT investor_id,'Summit Balanced Fund',420000,'2026-03-05','ACTIVE'
FROM investor
WHERE contact_email='hello@summitequity.com'
AND NOT EXISTS(
SELECT 1 FROM fund WHERE fund_name='Summit Balanced Fund'
);



INSERT INTO fund
(investor_id,fund_name,amount_received,received_date,status)

SELECT investor_id,'Pioneer Alpha Fund',280000,'2026-03-12','ACTIVE'
FROM investor
WHERE contact_email='contact@pioneerwealth.com'
AND NOT EXISTS(
SELECT 1 FROM fund WHERE fund_name='Pioneer Alpha Fund'
);



INSERT INTO fund
(investor_id,fund_name,amount_received,received_date,status)

SELECT investor_id,'Vertex Dividend Fund',310000,'2026-03-20','ACTIVE'
FROM investor
WHERE contact_email='hello@vertexcapital.com'
AND NOT EXISTS(
SELECT 1 FROM fund WHERE fund_name='Vertex Dividend Fund'
);



INSERT INTO fund
(investor_id,fund_name,amount_received,received_date,status)

SELECT investor_id,'Aurora Dynamic Fund',260000,'2026-04-02','ACTIVE'
FROM investor
WHERE contact_email='info@auroraam.com'
AND NOT EXISTS(
SELECT 1 FROM fund WHERE fund_name='Aurora Dynamic Fund'
);



INSERT INTO fund
(investor_id,fund_name,amount_received,received_date,status)

SELECT investor_id,'Crestline Value Fund',295000,'2026-04-08','ACTIVE'
FROM investor
WHERE contact_email='support@crestlineglobal.com'
AND NOT EXISTS(
SELECT 1 FROM fund WHERE fund_name='Crestline Value Fund'
);



INSERT INTO fund
(investor_id,fund_name,amount_received,received_date,status)

SELECT investor_id,'Horizon Stability Fund',330000,'2026-04-15','ACTIVE'
FROM investor
WHERE contact_email='admin@horizonfunds.com'
AND NOT EXISTS(
SELECT 1 FROM fund WHERE fund_name='Horizon Stability Fund'
);

/* =====================================================
   PORTFOLIO
   ===================================================== */

INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'alice.johnson@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'contact@bluestonecapital.com'),
    'BlueStone Growth Portfolio',
    'Aggressive growth strategy focused on equities',
    'HIGH',
    450000.00,
    '2026-01-20'
WHERE NOT EXISTS (
    SELECT 1
    FROM portfolio
    WHERE investor_id = (
        SELECT investor_id
        FROM investor
        WHERE contact_email = 'contact@bluestonecapital.com'
    )
);


INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'brian.smith@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'info@northbridgeholdings.com'),
    'NorthBridge Income Portfolio',
    'Steady income generation through bonds and dividends',
    'LOW',
    320000.00,
    '2026-02-15'
WHERE NOT EXISTS (
    SELECT 1
    FROM portfolio
    WHERE investor_id = (
        SELECT investor_id
        FROM investor
        WHERE contact_email = 'info@northbridgeholdings.com'
    )
);


INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'catherine.lee@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'hello@summitequity.com'),
    'Summit Balanced Portfolio',
    'Balanced mix of equities and fixed income',
    'MEDIUM',
    390000.00,
    '2026-03-10'
WHERE NOT EXISTS (
    SELECT 1
    FROM portfolio
    WHERE investor_id = (
        SELECT investor_id
        FROM investor
        WHERE contact_email = 'hello@summitequity.com'
    )
);


INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'david.wilson@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'contact@pioneerwealth.com'),
    'Pioneer Alpha Portfolio',
    'High alpha strategy targeting outperformance',
    'HIGH',
    250000.00,
    '2026-03-18'
WHERE NOT EXISTS (
    SELECT 1
    FROM portfolio
    WHERE investor_id = (
        SELECT investor_id
        FROM investor
        WHERE contact_email = 'contact@pioneerwealth.com'
    )
);


INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'emma.davis@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'hello@vertexcapital.com'),
    'Vertex Dividend Portfolio',
    'Dividend-focused portfolio for regular income',
    'LOW',
    280000.00,
    '2026-03-25'
WHERE NOT EXISTS (
    SELECT 1
    FROM portfolio
    WHERE investor_id = (
        SELECT investor_id
        FROM investor
        WHERE contact_email = 'hello@vertexcapital.com'
    )
);


INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'alice.johnson@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'info@auroraam.com'),
    'Aurora Dynamic Portfolio',
    'Dynamic allocation across multiple asset classes',
    'MEDIUM',
    230000.00,
    '2026-04-05'
WHERE NOT EXISTS (
    SELECT 1
    FROM portfolio
    WHERE investor_id = (
        SELECT investor_id
        FROM investor
        WHERE contact_email = 'info@auroraam.com'
    )
);


INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'brian.smith@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'support@crestlineglobal.com'),
    'Crestline Value Portfolio',
    'Value investing strategy targeting undervalued assets',
    'MEDIUM',
    265000.00,
    '2026-04-12'
WHERE NOT EXISTS (
    SELECT 1
    FROM portfolio
    WHERE investor_id = (
        SELECT investor_id
        FROM investor
        WHERE contact_email = 'support@crestlineglobal.com'
    )
);


INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT
    (SELECT employee_id FROM employee WHERE email = 'catherine.lee@companya.com'),
    (SELECT investor_id FROM investor WHERE contact_email = 'admin@horizonfunds.com'),
    'Horizon Stability Portfolio',
    'Capital preservation with stable low-risk returns',
    'LOW',
    300000.00,
    '2026-04-18'
WHERE NOT EXISTS (
    SELECT 1
    FROM portfolio
    WHERE investor_id = (
        SELECT investor_id
        FROM investor
        WHERE contact_email = 'admin@horizonfunds.com'
    )
);

/* =====================================================
   INVESTMENT
   ===================================================== */

-- BlueStone Growth Portfolio: AAPL + MSFT
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'BlueStone Growth Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL'),
    200000.00,
    215000.00,
    '2026-01-22'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'BlueStone Growth Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'BlueStone Growth Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'MSFT'),
    150000.00,
    162000.00,
    '2026-01-22'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'BlueStone Growth Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'MSFT')
);


-- NorthBridge Income Portfolio: IEF + VOO
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'NorthBridge Income Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'IEF'),
    180000.00,
    183000.00,
    '2026-02-18'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'NorthBridge Income Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'IEF')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'NorthBridge Income Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'VOO'),
    100000.00,
    104000.00,
    '2026-02-18'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'NorthBridge Income Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'VOO')
);


-- Summit Balanced Portfolio: AAPL + IEF + GLD
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Summit Balanced Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL'),
    130000.00,
    138000.00,
    '2026-03-12'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Summit Balanced Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Summit Balanced Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'IEF'),
    130000.00,
    132000.00,
    '2026-03-12'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Summit Balanced Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'IEF')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Summit Balanced Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'GLD'),
    80000.00,
    85000.00,
    '2026-03-12'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Summit Balanced Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'GLD')
);


-- Pioneer Alpha Portfolio: MSFT + AAPL
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Pioneer Alpha Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'MSFT'),
    130000.00,
    141000.00,
    '2026-03-20'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Pioneer Alpha Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'MSFT')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Pioneer Alpha Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL'),
    90000.00,
    97000.00,
    '2026-03-20'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Pioneer Alpha Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL')
);


-- Vertex Dividend Portfolio: VOO + IEF + CASH-INR
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Vertex Dividend Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'VOO'),
    120000.00,
    125000.00,
    '2026-03-28'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Vertex Dividend Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'VOO')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Vertex Dividend Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'IEF'),
    100000.00,
    102000.00,
    '2026-03-28'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Vertex Dividend Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'IEF')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Vertex Dividend Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'CASH-INR'),
    40000.00,
    40000.00,
    '2026-03-28'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Vertex Dividend Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'CASH-INR')
);


-- Aurora Dynamic Portfolio: GLD + MSFT + CASH-INR
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Aurora Dynamic Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'GLD'),
    90000.00,
    96000.00,
    '2026-04-08'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Aurora Dynamic Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'GLD')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Aurora Dynamic Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'MSFT'),
    100000.00,
    108000.00,
    '2026-04-08'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Aurora Dynamic Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'MSFT')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Aurora Dynamic Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'CASH-INR'),
    30000.00,
    30000.00,
    '2026-04-08'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Aurora Dynamic Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'CASH-INR')
);


-- Crestline Value Portfolio: AAPL + VOO
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Crestline Value Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL'),
    140000.00,
    150000.00,
    '2026-04-15'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Crestline Value Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'AAPL')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Crestline Value Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'VOO'),
    95000.00,
    99000.00,
    '2026-04-15'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Crestline Value Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'VOO')
);


-- Horizon Stability Portfolio: IEF + CASH-INR + GLD
INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Horizon Stability Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'IEF'),
    150000.00,
    153000.00,
    '2026-04-20'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Horizon Stability Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'IEF')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Horizon Stability Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'CASH-INR'),
    80000.00,
    80000.00,
    '2026-04-20'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Horizon Stability Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'CASH-INR')
);

INSERT INTO investment (portfolio_id, asset_id, amount_invested, current_value, purchase_date)
SELECT
    (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Horizon Stability Portfolio'),
    (SELECT asset_id FROM asset WHERE ticker_symbol = 'GLD'),
    60000.00,
    64000.00,
    '2026-04-20'
WHERE NOT EXISTS (
    SELECT 1 FROM investment
    WHERE portfolio_id = (SELECT portfolio_id FROM portfolio WHERE portfolio_name = 'Horizon Stability Portfolio')
    AND asset_id = (SELECT asset_id FROM asset WHERE ticker_symbol = 'GLD')
);

UPDATE investment
SET quantity = 1
WHERE quantity IS NULL OR quantity = 0;