CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    First_Name VARCHAR(255) NOT NULL,
    Last_Name VARCHAR(255) NOT NULL,
    ROLE_ID INT NOT NULL,
    Email VARCHAR(255) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Address VARCHAR(255),
    Phone VARCHAR(50),
    Nationality VARCHAR(100),
    Gender VARCHAR(10),
    Registration_Date DATETIME,
    Birthday DATETIME
);

CREATE TABLE role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ROLE_DESC VARCHAR(255) NOT NULL
);

CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    Product_Name VARCHAR(255) NOT NULL,
    Price DOUBLE NOT NULL,
    Stock_Quantity INT,
    Color VARCHAR(50),
    Description VARCHAR(255),
    Creation_Date DATETIME
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Order_Number VARCHAR(255) NOT NULL,
    Transaction_Date DATETIME NOT NULL,
    FOREIGN KEY (User_ID) REFERENCES user(id)
);

-- Create Order Details Table
CREATE TABLE order_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ORDER_ID INT NOT NULL,
    PRODUCT_ID INT NOT NULL,
    Quantity INT NOT NULL,
    FOREIGN KEY (ORDER_ID) REFERENCES orders(id),
    FOREIGN KEY (PRODUCT_ID) REFERENCES product(id)
);

CREATE VIEW VW_ORDER_DETAILS AS
SELECT
    od.id AS id,
    o.id AS Order_ID,
    o.User_ID AS USER_ID,
    od.quantity AS PRODUCT_QUANTITY,
    (od.Quantity * p.Price) AS TOTAL_PRICE,
    p.id AS PRODUCT_ID,
    u.First_Name AS CUSTOMER_NAME,
    u.Address AS CUSTOMER_ADDRESS,
    u.Phone AS CUSTOMER_PHONE,
    o.Order_Number AS ORDER_NUMBER,
    p.Stock_Quantity AS STOCK_QUANTITY,
    p.Color AS PRODUCT_COLOR,
    p.Description AS PRODUCT_DESCRIPTION,
    p.Product_Name AS PRODUCT_NAME,
    o.Transaction_Date AS TRANSACTION_DATE
FROM 
    orders o
JOIN 
    order_details od ON o.id = od.ORDER_ID
JOIN 
    user u ON o.User_ID = u.id
JOIN 
    product p ON od.PRODUCT_ID = p.id;