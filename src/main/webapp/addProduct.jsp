<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Add Product</title>
  <style>
    body{font-family:Arial;background:#f5f5f5;margin:0;padding:20px}
    .wrap{max-width:700px;margin:20px auto;background:#fff;padding:20px;border-radius:6px}
    label{display:block;margin-top:10px}
    input[type="text"], textarea, input[type="number"]{width:100%;padding:8px;border:1px solid #ccc;border-radius:6px}
    button{margin-top:12px;padding:8px 12px;border-radius:6px;background:#007bff;color:#fff;border:none}
    .topbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:12px}
    .btn{display:inline-block;padding:8px 12px;background:#007bff;color:#fff;text-decoration:none;border-radius:6px}
  </style>
</head>
<body>
<div class="container" style="max-width:1100px;margin:0 auto">

  <!-- TOPBAR: same as listProducts -->
  <div class="topbar">
    <h2>Add Product</h2>
    <div style="display:flex;gap:10px;align-items:center">
      <a href="<%= request.getContextPath() %>/listProducts.jsp" class="btn">Product List</a>
      <%
        String admin = (String) session.getAttribute("adminUsername");
        String fullname = (String) session.getAttribute("adminFullname");
        if (admin != null) {
      %>
        <span style="color:#333">Welcome, <b><%= (fullname!=null?fullname:admin) %></b></span>
        <a href="<%= request.getContextPath() %>/logout" class="btn" style="background:#6c757d">Logout</a>
      <% } else { %>
        <a href="<%= request.getContextPath() %>/login.jsp" class="btn">Admin Login</a>
      <% } %>
    </div>
  </div>

  <div class="wrap">
    <form method="post" action="<%= request.getContextPath() %>/addProduct">
      <label>SKU</label>
      <input type="text" name="sku" required />
      <label>Name</label>
      <input type="text" name="name" required />
      <label>Description</label>
      <textarea name="description" rows="3"></textarea>
      <label>Quantity</label>
      <input type="number" name="quantity" value="0" min="0" required />
      <button type="submit">Save & Generate QR</button>
    </form>
  </div>
</div>
</body>
</html>
