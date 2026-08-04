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

