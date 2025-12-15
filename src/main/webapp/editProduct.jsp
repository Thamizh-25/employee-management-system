<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Edit Product</title>
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
  <!-- TOPBAR -->
  <div class="topbar">
    <h2>Edit Product</h2>
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
    <%
      String idParam = request.getParameter("id");
      if (idParam == null) {
        out.println("<div style='color:red'>Missing product id.</div>");
      } else {
        int pid = 0;
        try { pid = Integer.parseInt(idParam); } catch (Exception _e) { pid = 0; }
        if (pid <= 0) {
          out.println("<div style='color:red'>Invalid id.</div>");
        } else {
          Connection conn = null;
          PreparedStatement ps = null;
          ResultSet rs = null;
          try {
            conn = com.sqwms.util.DBConnection.getConnection();
            ps = conn.prepareStatement("SELECT id, sku, name, description, quantity FROM products WHERE id = ?");
            ps.setInt(1, pid);
            rs = ps.executeQuery();
            if (rs.next()) {
              String sku = rs.getString("sku");
              String name = rs.getString("name");
              String description = rs.getString("description");
              int qty = rs.getInt("quantity");
    %>

      <form method="post" action="<%= request.getContextPath() %>/updateProduct">
        <input type="hidden" name="id" value="<%= pid %>"/>
        <label>SKU</label>
        <input type="text" name="sku" value="<%= sku %>" required />
        <label>Name</label>
        <input type="text" name="name" value="<%= name %>" required />
        <label>Description</label>
        <textarea name="description" rows="3"><%= (description==null?"":description) %></textarea>
        <label>Quantity</label>
        <input type="number" name="quantity" value="<%= qty %>" min="0" required />
        <div style="margin-top:12px">
          <button type="submit">Update Product</button>
        </div>
      </form>

    <%
            } else {
              out.println("<div style='color:orange'>Product not found.</div>");
            }
          } catch (Exception e) {
            out.println("<div style='color:red'>Error: " + e.getMessage() + "</div>");
          } finally {
            try { if (rs != null) rs.close(); } catch (Exception _e) {}
            try { if (ps != null) ps.close(); } catch (Exception _e) {}
            try { if (conn != null) conn.close(); } catch (Exception _e) {}
          }
        }
      }
    %>
  </div>
</div>
</body>
</html>

