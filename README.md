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


