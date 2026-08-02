/* =====================================================
   EMPLOYEE
   Employees of Company A who manage portfolios
   ===================================================== */

CREATE TABLE IF NOT EXISTS employee (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(50)
);


/* =====================================================
   INVESTOR
   Companies providing funds to Company A
   Example: Company B
   ===================================================== */

CREATE TABLE IF NOT EXISTS investor (
    investor_id INT PRIMARY KEY AUTO_INCREMENT,
    investor_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100)
);


/* =====================================================
   FUND
   Money received from an investor

   One Investor -> Many Funds
   ===================================================== */

CREATE TABLE IF NOT EXISTS fund (
    fund_id INT PRIMARY KEY AUTO_INCREMENT,
    investor_id INT NOT NULL,

    fund_name VARCHAR(100),
    amount_received DECIMAL(15,2) NOT NULL,
    received_date DATE,
    status VARCHAR(20),

    FOREIGN KEY (investor_id)
        REFERENCES investor(investor_id)
);


/* =====================================================
   PORTFOLIO

   One Fund -> Many Portfolios
   One Employee -> Many Portfolios
   ===================================================== */

CREATE TABLE IF NOT EXISTS portfolio (
    portfolio_id INT PRIMARY KEY AUTO_INCREMENT,

    employee_id INT NOT NULL,
    fund_id INT NOT NULL,

    portfolio_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    risk_level VARCHAR(20),
    allocated_amount DECIMAL(15,2),
    created_date DATE,

    FOREIGN KEY (employee_id)
        REFERENCES employee(employee_id),

    FOREIGN KEY (fund_id)
        REFERENCES fund(fund_id)
);


/* =====================================================
   ASSET
   Represents something that can be invested in
   ===================================================== */

CREATE TABLE IF NOT EXISTS asset (
    asset_id INT PRIMARY KEY AUTO_INCREMENT,

    asset_name VARCHAR(100) NOT NULL,

    -- Useful for future live-price API integration
    ticker_symbol VARCHAR(30),

    asset_type VARCHAR(50) NOT NULL
);


/* =====================================================
   INVESTMENT
   Represents an asset held inside a portfolio

   One Portfolio -> Many Investments
   One Asset -> Many Investments
   ===================================================== */

CREATE TABLE IF NOT EXISTS investment (
    investment_id INT PRIMARY KEY AUTO_INCREMENT,

    portfolio_id INT NOT NULL,
    asset_id INT NOT NULL,

    amount_invested DECIMAL(15,2) NOT NULL,
    current_value DECIMAL(15,2),

    purchase_date DATE,

    FOREIGN KEY (portfolio_id)
        REFERENCES portfolio(portfolio_id),

    FOREIGN KEY (asset_id)
        REFERENCES asset(asset_id)
);