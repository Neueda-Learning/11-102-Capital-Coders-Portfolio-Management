INSERT INTO employee (employee_name, email, department)
SELECT 'Alice Johnson', 'alice.johnson@companya.com', 'Portfolio Operations'
WHERE NOT EXISTS (
    SELECT 1 FROM employee WHERE email = 'alice.johnson@companya.com'
);

INSERT INTO employee (employee_name, email, department)
SELECT 'Brian Smith', 'brian.smith@companya.com', 'Risk Management'
WHERE NOT EXISTS (
    SELECT 1 FROM employee WHERE email = 'brian.smith@companya.com'
);

INSERT INTO employee (employee_name, email, department)
SELECT 'Catherine Lee', 'catherine.lee@companya.com', 'Client Services'
WHERE NOT EXISTS (
    SELECT 1 FROM employee WHERE email = 'catherine.lee@companya.com'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'BlueStone Capital', 'contact@bluestonecapital.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'contact@bluestonecapital.com'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'NorthBridge Holdings', 'info@northbridgeholdings.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'info@northbridgeholdings.com'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'Summit Equity Partners', 'hello@summitequity.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'hello@summitequity.com'
);

INSERT INTO asset (asset_name, ticker_symbol, asset_type)
SELECT 'Apple Inc.', 'AAPL', 'stocks'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol = 'AAPL'
);

INSERT INTO asset (asset_name, ticker_symbol, asset_type)
SELECT 'US Treasury 10Y', 'UST10Y', 'bonds'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol = 'UST10Y'
);

INSERT INTO asset (asset_name, ticker_symbol, asset_type)
SELECT 'Vanguard S&P 500 Index Fund', 'VFIAX', 'mutual funds'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol = 'VFIAX'
);

INSERT INTO asset (asset_name, ticker_symbol, asset_type)
SELECT 'INR Cash Reserve', 'CASH-INR', 'cash'
WHERE NOT EXISTS (
    SELECT 1 FROM asset WHERE ticker_symbol = 'CASH-INR'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'Pioneer Wealth Partners', 'contact@pioneerwealth.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'contact@pioneerwealth.com'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'Vertex Capital Group', 'hello@vertexcapital.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'hello@vertexcapital.com'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'Aurora Asset Management', 'info@auroraam.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'info@auroraam.com'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'Crestline Global', 'support@crestlineglobal.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'support@crestlineglobal.com'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'Horizon Strategic Funds', 'admin@horizonfunds.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'admin@horizonfunds.com'
);

INSERT INTO investor (investor_name, contact_email)
SELECT 'MapleRock Investments', 'team@maplerock.com'
WHERE NOT EXISTS (
    SELECT 1 FROM investor WHERE contact_email = 'team@maplerock.com'
);

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'BlueStone Growth Fund', 500000.00, '2026-01-15', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'contact@bluestonecapital.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'BlueStone Growth Fund');

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'NorthBridge Income Fund', 350000.00, '2026-02-10', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'info@northbridgeholdings.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'NorthBridge Income Fund');

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'Summit Balanced Fund', 420000.00, '2026-03-05', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'hello@summitequity.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'Summit Balanced Fund');

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'Pioneer Alpha Fund', 280000.00, '2026-03-12', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'contact@pioneerwealth.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'Pioneer Alpha Fund');

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'Vertex Dividend Fund', 310000.00, '2026-03-20', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'hello@vertexcapital.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'Vertex Dividend Fund');

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'Aurora Dynamic Fund', 260000.00, '2026-04-02', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'info@auroraam.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'Aurora Dynamic Fund');

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'Crestline Value Fund', 295000.00, '2026-04-08', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'support@crestlineglobal.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'Crestline Value Fund');

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'Horizon Stability Fund', 330000.00, '2026-04-15', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'admin@horizonfunds.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'Horizon Stability Fund');

INSERT INTO fund (investor_id, fund_name, amount_received, received_date, status)
SELECT i.investor_id, 'MapleRock Growth Plus', 305000.00, '2026-04-20', 'ACTIVE'
FROM investor i
WHERE i.contact_email = 'team@maplerock.com'
  AND NOT EXISTS (SELECT 1 FROM fund WHERE fund_name = 'MapleRock Growth Plus');


-- Employee: Alice
INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Alice Growth Portfolio', 'High growth strategy', 'HIGH', 220000.00, '2026-01-20'
FROM employee e JOIN investor i ON i.contact_email = 'contact@bluestonecapital.com'
WHERE e.email = 'alice.johnson@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Alice Income Portfolio', 'Income-focused allocation', 'LOW', 180000.00, '2026-02-14'
FROM employee e JOIN investor i ON i.contact_email = 'info@northbridgeholdings.com'
WHERE e.email = 'alice.johnson@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Alice Balanced Portfolio', 'Balanced equity and debt', 'MEDIUM', 200000.00, '2026-03-10'
FROM employee e JOIN investor i ON i.contact_email = 'hello@summitequity.com'
WHERE e.email = 'alice.johnson@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

-- Employee: Brian
INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Brian Alpha Portfolio', 'Aggressive alpha strategy', 'HIGH', 210000.00, '2026-03-16'
FROM employee e JOIN investor i ON i.contact_email = 'contact@pioneerwealth.com'
WHERE e.email = 'brian.smith@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Brian Dividend Portfolio', 'Dividend yield focus', 'LOW', 170000.00, '2026-03-25'
FROM employee e JOIN investor i ON i.contact_email = 'hello@vertexcapital.com'
WHERE e.email = 'brian.smith@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Brian Dynamic Portfolio', 'Tactical rebalancing strategy', 'MEDIUM', 195000.00, '2026-04-05'
FROM employee e JOIN investor i ON i.contact_email = 'info@auroraam.com'
WHERE e.email = 'brian.smith@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

-- Employee: Catherine
INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Catherine Value Portfolio', 'Long-term value investing', 'MEDIUM', 185000.00, '2026-04-09'
FROM employee e JOIN investor i ON i.contact_email = 'support@crestlineglobal.com'
WHERE e.email = 'catherine.lee@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Catherine Stability Portfolio', 'Low-volatility allocation', 'LOW', 175000.00, '2026-04-16'
FROM employee e JOIN investor i ON i.contact_email = 'admin@horizonfunds.com'
WHERE e.email = 'catherine.lee@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

INSERT INTO portfolio (employee_id, investor_id, portfolio_name, description, risk_level, allocated_amount, created_date)
SELECT e.employee_id, i.investor_id, 'Catherine Growth Plus', 'Growth with controlled risk', 'HIGH', 205000.00, '2026-04-22'
FROM employee e JOIN investor i ON i.contact_email = 'team@maplerock.com'
WHERE e.email = 'catherine.lee@companya.com'
  AND NOT EXISTS (SELECT 1 FROM portfolio WHERE investor_id = i.investor_id);

