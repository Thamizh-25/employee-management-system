<%@ page contentType="text/html; charset=UTF-8" %>

<%
int qty = (Integer) request.getAttribute("quantity");

String status = "In Stock";
String color = "#28a745";

if (qty < 10) {
    status = "Critical Stock";
    color = "#dc3545";
} else if (qty < 20) {
    status = "Low Stock";
    color = "#ffc107";
}
%>

<!DOCTYPE html>
<html>
<head>
<title>Product Details</title>

<style>
body {
    font-family: Arial;
    background: #f5f5f5;
    padding: 20px;
}
.card {
    max-width: 400px;
    margin: auto;
    background: #fff;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
h2 {
    text-align: center;
}
.label {
    font-weight: bold;
    margin-top: 12px;
}
.value {
    margin-top: 4px;
}
.status {
    padding: 6px 12px;
    border-radius: 6px;
    color: #fff;
    display: inline-block;
    margin-top: 8px;
}
.footer {
    text-align: center;
    margin-top: 20px;
    font-size: 12px;
    color: #777;
}
</style>
</head>

<body>

<div class="card">
    <h2>📦 Product Details</h2>

    <div class="label">SKU</div>
    <div class="value"><%= request.getAttribute("sku") %></div>

    <div class="label">Name</div>
    <div class="value"><%= request.getAttribute("name") %></div>

    <div class="label">Quantity</div>
    <div class="value"><%= qty %></div>

    <div class="label">Stock Status</div>
    <div class="status" style="background:<%= color %>"><%= status %></div>

    <div class="label">Last Updated</div>
    <div class="value"><%= request.getAttribute("updatedAt") %></div>

    <div class="footer">
        SmartQR Warehouse Management System
    </div>
</div>

</body>
</html>
