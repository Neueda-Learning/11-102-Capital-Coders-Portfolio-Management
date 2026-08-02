--CREATE DATABASE if not exist portfolio_management;
USE portfolio_management;

CREATE TABLE employee (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(50)
);

CREATE TABLE portfolio (
    portfolio_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,

    portfolio_name VARCHAR(100),
    description VARCHAR(255),
    risk_level VARCHAR(20),
    created_date DATE,

    FOREIGN KEY (employee_id)
    REFERENCES employee(employee_id)
);

CREATE TABLE investor (
    investor_id INT PRIMARY KEY AUTO_INCREMENT,
    investor_name VARCHAR(100),
    contact_email VARCHAR(100)
);

CREATE TABLE fund (
    fund_id INT PRIMARY KEY AUTO_INCREMENT,

    investor_id INT,
    portfolio_id INT,

    amount_received DECIMAL(15,2),
    received_date DATE,
    status VARCHAR(20),

    FOREIGN KEY (investor_id)
    REFERENCES investor(investor_id),

    FOREIGN KEY (portfolio_id)
    REFERENCES portfolio(portfolio_id)
);

CREATE TABLE investment (
    investment_id INT PRIMARY KEY AUTO_INCREMENT,

    portfolio_id INT,

    asset_name VARCHAR(100),
    asset_type VARCHAR(50),

    amount_invested DECIMAL(15,2),
    current_value DECIMAL(15,2),

    purchase_date DATE,

    FOREIGN KEY (portfolio_id)
    REFERENCES portfolio(portfolio_id)
);

CREATE TABLE transaction_history (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,

    investment_id INT,

    transaction_type VARCHAR(20),
    amount DECIMAL(15,2),
    transaction_date DATE,

    FOREIGN KEY (investment_id)
    REFERENCES investment(investment_id)
);

