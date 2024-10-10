CREATE TABLE IF NOT EXISTS ECO_USER (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    FIRST_NAME VARCHAR(255) NOT NULL,
    LAST_NAME VARCHAR(255) NOT NULL,
    ROLE_ID INT NOT NULL,
    EMAIL VARCHAR(255) NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    ADDRESS VARCHAR(255),
    PHONE VARCHAR(50),
    NATIONALITY VARCHAR(100),
    GENDER VARCHAR(10),
    REGISTRATION_DATE DATETIME,
    BIRTHDAY DATETIME
);

CREATE TABLE IF NOT EXISTS ECO_ROLE (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ROLE_DESC VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ECO_PRODUCT (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    PRODUCT_NAME VARCHAR(255) NOT NULL,
    PRICE DOUBLE NOT NULL,
    STOCK_QUANTITY INT,
    COLOR VARCHAR(50),
    DESCRIPTION VARCHAR(255),
    CREATION_DATE DATETIME
);

CREATE TABLE IF NOT EXISTS ECO_ORDERS (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    USER_ID INT NOT NULL,
    ORDER_NUMBER VARCHAR(255) NOT NULL,
    TRANSACTION_DATE DATETIME NOT NULL,
    FOREIGN KEY (USER_ID) REFERENCES ECO_USER(ID)
);

CREATE TABLE IF NOT EXISTS ECO_ORDER_DETAILS (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ORDER_ID INT NOT NULL,
    PRODUCT_ID INT NOT NULL,
    QUANTITY INT NOT NULL,
    FOREIGN KEY (ORDER_ID) REFERENCES ECO_ORDERS(ID),
    FOREIGN KEY (PRODUCT_ID) REFERENCES ECO_PRODUCT(ID)
);

CREATE VIEW IF NOT EXISTS  ECO_VW_ORDER_DETAILS AS
SELECT
    OD.ID AS ID,
    O.ID AS ORDER_ID,
    O.USER_ID AS USER_ID,
    OD.QUANTITY AS PRODUCT_QUANTITY,
    (OD.QUANTITY * P.PRICE) AS TOTAL_PRICE,
    P.ID AS PRODUCT_ID,
    U.FIRST_NAME AS CUSTOMER_NAME,
    U.ADDRESS AS CUSTOMER_ADDRESS,
    U.PHONE AS CUSTOMER_PHONE,
    O.ORDER_NUMBER AS ORDER_NUMBER,
    P.STOCK_QUANTITY AS STOCK_QUANTITY,
    P.COLOR AS PRODUCT_COLOR,
    P.DESCRIPTION AS PRODUCT_DESCRIPTION,
    P.PRODUCT_NAME AS PRODUCT_NAME,
    O.TRANSACTION_DATE AS TRANSACTION_DATE
FROM 
    ECO_ORDERS O
JOIN 
    ECO_ORDER_DETAILS OD ON O.ID = OD.ORDER_ID
JOIN 
    ECO_USER U ON O.USER_ID = U.ID
JOIN 
    ECO_PRODUCT P ON OD.PRODUCT_ID = P.ID;


CREATE TABLE IF NOT EXISTS ECO_WF_ACTIONS (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ACTION_NAME VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS ECO_WF_PROCESSES_GROUPS (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ECO_WF_PRODUCT (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    PRODUCT_ID INT,
    WF_INSTANCE_ID INT NOT NULL,
    PRODUCT_NAME VARCHAR(255) ,
    PRICE DOUBLE,
    STOCK_QUANTITY INT,
    COLOR VARCHAR(50),
    DESCRIPTION VARCHAR(255),
    STATUS INT
);


CREATE TABLE IF NOT EXISTS ECO_PRODUCT_TRNS_HISTORY (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    PRODUCT_ID INT,
    PRODUCT_NAME VARCHAR(255) NOT NULL,
    PRICE DOUBLE NOT NULL,
    STOCK_QUANTITY INT,
    COLOR VARCHAR(50),
    DESCRIPTION VARCHAR(255),
    STATUS INT,
    CREATION_DATE DATETIME
    
);

CREATE TABLE IF NOT EXISTS ECO_WF_PROCESSES (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL,
    GROUP_ID INT,
    FOREIGN KEY (GROUP_ID) REFERENCES ECO_WF_PROCESSES_GROUPS(ID)
);

CREATE TABLE IF NOT EXISTS ECO_WF_INSTANCES (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    PROCESS_ID INT NOT NULL,
    REQUESTER_ID INT NOT NULL,
    REQUEST_DATE DATETIME NOT NULL,
    STATUS INT NOT NULL,
    LAST_TASK_ACTION INT,
    FOREIGN KEY (PROCESS_ID) REFERENCES ECO_WF_PROCESSES(ID),
    FOREIGN KEY (REQUESTER_ID) REFERENCES ECO_USER(ID),
    FOREIGN KEY (LAST_TASK_ACTION) REFERENCES ECO_WF_ACTIONS(ID)
);

CREATE TABLE IF NOT EXISTS ECO_WF_TASKS (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    INSTANCE_ID INT NOT NULL,
    ASSIGNEE_ID INT NOT NULL,
    ASSIGN_DATE DATETIME NOT NULL,
    ASSIGNEE_ROLE VARCHAR(225),
    ACTION_ID INT,
    ACTION_DATE DATETIME,
    NOTES VARCHAR(255),
    REFUSE_REASONS VARCHAR(255),
    FOREIGN KEY (INSTANCE_ID) REFERENCES ECO_WF_INSTANCES(ID),
    FOREIGN KEY (ASSIGNEE_ID) REFERENCES ECO_USER(ID),
    FOREIGN KEY (ACTION_ID) REFERENCES ECO_WF_ACTIONS(ID)
);

    
CREATE SEQUENCE IF NOT EXISTS ECO_INSTANCES_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 1 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_ORDERS_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 21 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_ORDER_DETAILS_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 1 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_PROCESSES_GROUPS_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 1 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_PROCESSES_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 1 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_PRODUCT_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 21 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_PRODUCT_TRANSACTION_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 1 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_ROLE_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 1 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_TASKS_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 1 
    CACHE 20 
    NOCYCLE;

CREATE SEQUENCE IF NOT EXISTS ECO_USER_SEQ 
    MINVALUE 1 
    MAXVALUE 999999999 
    INCREMENT BY 1 
    START WITH 22 
    CACHE 20 
    NOCYCLE;
    
CREATE SEQUENCE  IF NOT EXISTS ECO_WF_PRODUCT_SEQ  MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 1 CACHE 20   NOCYCLE;
CREATE SEQUENCE  IF NOT EXISTS ECO_WF_INSTANCES_SEQ  MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 1 CACHE 20   NOCYCLE;
CREATE SEQUENCE  IF NOT EXISTS ECO_WF_ACTIONS_SEQ  MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 1 CACHE 20   NOCYCLE;
CREATE SEQUENCE  IF NOT EXISTS ECO_PRODUCT_TRNS_HISTORY_SEQ  MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 1 CACHE 20   NOCYCLE;

