# 📦 SmartQR Warehouse Management System (WMS)

SmartQR WMS is a **Java JDBC–based Warehouse Management System** built using **Servlets, JSP, MySQL, and Apache Tomcat**.  
It provides **QR-based product identification**, **real-time inventory tracking**, and **complete stock history auditing** with a timeline view.

This project was developed and managed fully using **Eclipse IDE** and version-controlled using **Git & GitHub**.

---

## 🚀 Project Overview

The system helps manage warehouse products efficiently by:
- Assigning a **unique QR code** to every product
- Tracking **stock updates automatically**
- Maintaining a **full audit trail** of stock changes
- Highlighting **Low Stock** and **Critical Stock** visually

Every product action (creation or quantity update) is logged permanently in the `stock_history` table.

---

## 🛠️ Tech Stack

- **Language:** Java  
- **Backend:** Servlets, JDBC  
- **Frontend:** JSP, HTML, CSS  
- **Database:** MySQL  
- **Server:** Apache Tomcat 10  
- **QR Code Library:** ZXing  
- **IDE:** Eclipse  
- **Version Control:** Git & GitHub  

---

## ✨ Features

### 📋 Product Management
- Add new products
- Edit existing products
- Delete products
- Automatic QR code generation

### 📊 Inventory Dashboard
- Total products count
- Low stock indication
- Critical stock indication
- Color-coded inventory rows

### 🧾 QR Code System
- Unique QR per product
- Downloadable QR images
- Stored inside `/qr_codes`

### 🕒 Stock History (Audit Trail)
- Logs product creation
- Logs every stock update
- Timeline-style history view
- Displays:
  - Date & Time
  - Action type
  - Old quantity → New quantity

### 🔍 Search
- Search products by SKU or Name

---

## 🗂️ Project Structure

Smart QR Warehouse Management System
│
├── src/main/java
│ └── com.sqwms.servlet
│ ├── AddProductServlet.java
│ ├── UpdateProductServlet.java
│ ├── DeleteProductServlet.java
│ ├── ViewProductServlet.java
│ ├── GenerateQRServlet.java
│ ├── HistoryServlet.java
│ ├── StockHistoryServlet.java
│ └── TestDBServlet.java
│
├── src/main/java/com/sqwms/util
│ ├── DBConnection.java
│ ├── QRGenerator.java
│ └── StockUtil.java
│
├── src/main/webapp
│ ├── WEB-INF
│ ├── qr_codes
│ ├── listproducts.jsp
│ ├── addProduct.jsp
│ ├── editProduct.jsp
│ └── index.jsp
│
└── README.md

pgsql
Copy code

---

## 🧮 Database Schema

### products
```
CREATE TABLE products (
  id INT PRIMARY KEY AUTO_INCREMENT,
  sku VARCHAR(50) UNIQUE,
  name VARCHAR(100),
  quantity INT,
  qr_path VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
);
```

stock_history
```
CREATE TABLE stock_history (
  id INT PRIMARY KEY AUTO_INCREMENT,
  product_id INT,
  sku VARCHAR(50),
  old_qty INT,
  new_qty INT,
  action VARCHAR(50),
  changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
▶️ How to Run the Project

Clone the repository
```
git clone https://github.com/Thamizh-25/employee-management-system.git
```
Import the project into Eclipse

Configure Apache Tomcat 10

Update database credentials in DBConnection.java

Create database tables in MySQL

Run the project on the server

Application URL
```
http://10.227.174.133:8080/Smart_QR_Warehouse_Management_System/listProducts.jsp?q=
```

👤 Author
Thamizh Selvan C
3rd Year B.Tech CSE
SRM Institute of Science and Technology

GitHub: https://github.com/Thamizh-25


