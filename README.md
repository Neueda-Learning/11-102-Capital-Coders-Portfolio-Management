# 📊 Portfolio Manager

## 🚀 Capital Coders

A full-stack **Portfolio Management System** designed to help investment companies efficiently manage investor funds, employee-managed portfolios, and investment activities across different asset classes such as **Stocks, Bonds, Mutual Funds**.

The system enables employees to manage multiple investor portfolios, track fund utilization, monitor investments, view market news, analyze portfolio performance, and perform investment operations with real-time market insights.

---

# 📌 Problem Statement

An investment company collects funds from investors and assigns each investor's portfolio to an employee for management.

### Business Rules:

* Each investor's investment portfolio is managed by **only one employee**.
* An employee can manage **multiple investor portfolios**.
* Employees invest investor funds into different asset categories:

  * Stocks
  * Bonds
  * Mutual Funds

The system provides employees with a centralized platform to manage portfolios, investments, and performance analysis.

---

# ✨ Features

## 🔐 Employee Login

* Employee enters their Employee ID.
* The system loads employee-specific portfolio information.

---

# 📈 Employee Dashboard

The dashboard provides a complete overview of the employee's managed portfolios.

### Features:

* Display employee details.
* View all portfolios assigned to the employee.
* Add new portfolios.
* Edit existing portfolios.
* Delete portfolios.
* View portfolio details.
* Display real-time stock-related news using Live News API.

---

# 💼 Portfolio Management

When an employee selects a portfolio:

* Investor details related to that portfolio are displayed.
* Employee can manage investor funds and investments.

### Portfolio Features:

## 👤 Investor Details

Displays:

* Investor information
* Portfolio information
* Total allocated funds

---

## 💰 Fund Management

Employees can add additional funds when investors provide more capital for investment.

Features:

* Add funds to portfolio.
* Track available and utilized funds.
* Monitor fund allocation.

---

## 📊 Portfolio Analytics

The system provides visual analytics for better decision-making.

### Fund Distribution Pie Chart

Displays:

* Total invested funds
* Used funds
* Remaining funds
* Asset-wise distribution:

  * Stocks
  * Bonds
  * Mutual Funds

### Portfolio Performance Line Chart

Shows investment performance based on:

* Current Value
* Purchase Value

Time-based analysis:

* Monthly
* Quarterly
* Annually

---

## 📋 Portfolio Summary

Displays:

* Total Funds
* Used Funds
* Available Funds
* Profit/Loss
* Return Percentage

---

# 📈 Investment Management

Employees can manage individual investments.

Supported operations:

* View investments
* Buy new assets
* Sell investments

Each investment contains:

* Asset type
* Investment amount
* Purchase value
* Current value
* Purchase date

---

### External Integrations

```
Spring Boot Backend
        |
        |
 Live News API
        |
        |
Real-Time Market Updates
```

---

# 📂 Project Structure

```
Portfolio-Management-System

│
├── backend
│
│   ├── src/main/java
│   │
│   │   └── controller
│   │   └── service
│   │   └── repository
│   │   └── model
│   │   └── Exception
│   │
│   └── pom.xml
│
│
├── frontend
│
│   ├── login
│   │   ├── login.html
│   │   ├── login.css
│   │   └── login.js
│   │
│   ├── dashboard
│   │   ├── dashboard.html
│   │   ├── dashboard.css
│   │   └── dashboard.js
│   │
│   └── portfolio
│       ├── portfolio.html
│       ├── portfolio.css
│       └── portfolio.js
│
└── README.md
```

---

# 🛠️ Technology Stack

## Frontend

* HTML5
* CSS3
* JavaScript

## Backend

* Java
* Spring Boot
* REST APIs
* JDBC Template

## Database

* MySQL

## APIs

* Live News API
* Twelve data API to fetch live market data

## Tools

* Git & GitHub
* IntelliJ IDEA
* VS Code
* Swagger Documentation

---

# ⚙️ Installation and Setup

## Backend Setup

1. Clone the repository.

```
git clone <repository-url>
```

2. Navigate to backend.

```
cd backend
```

3. Configure MySQL database in:

```
application.properties
```

4. Run Spring Boot application.

```
mvn spring-boot:run
```

---

## Frontend Setup

Navigate to frontend folder.

Open:

```
login.html
```

Run using Live Server.

---

# 👥 Collaborators

## Capital Coders

1.Nishika Tomar

2.Mubashir Aslam

3.Jishnu Jayant Bandodkar

4.Sai Ratna Varshita Annadevara

---

# 🔮 Future Enhancements

* AI-based investment recommendations.
* Machine learning based stock price prediction.
* Risk analysis for portfolios.
* Automated portfolio rebalancing.
* Mobile application support.
* Advanced investor dashboards.

---

# 🎯 Conclusion

Portfolio Manager provides an efficient solution for investment companies to manage investor funds, employee portfolios, and investment activities in one platform.

By combining portfolio tracking, real-time financial news, visualization dashboards, and investment management features, the system helps employees make informed investment decisions while improving transparency and efficien
