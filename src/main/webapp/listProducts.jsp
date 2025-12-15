<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*, java.util.*, java.net.URLEncoder" %>
<%@ page import="com.sqwms.util.DBConnection" %>

<%
/* --- Server side: flash messages & search --- */
String flashMsg = (String) session.getAttribute("flashMsg");
String flashError = (String) session.getAttribute("flashError");
if (flashMsg != null) session.removeAttribute("flashMsg");
if (flashError != null) session.removeAttribute("flashError");

/* search */
String q = request.getParameter("q");
boolean hasFilter = (q != null && !q.trim().isEmpty());
String likeParam = hasFilter ? ("%" + q.trim().toLowerCase() + "%") : null;

/* load all matching rows (no pagination) */
List<Map<String,Object>> rows = new ArrayList<>();
Connection conn = null;
PreparedStatement ps = null;
ResultSet rs = null;
try {
    conn = DBConnection.getConnection();
    if (conn != null) {
        String sql = "SELECT id, sku, name, quantity, qr_path, qr_text, created_at, updated_at FROM products"
                   + (hasFilter ? " WHERE LOWER(sku) LIKE ? OR LOWER(name) LIKE ? " : "")
                   + " ORDER BY id DESC";
        ps = conn.prepareStatement(sql);
        int idx = 1;
        if (hasFilter) { ps.setString(idx++, likeParam); ps.setString(idx++, likeParam); }
        rs = ps.executeQuery();

        while (rs.next()) {
            Map<String,Object> r = new HashMap<>();
            r.put("id", rs.getInt("id"));
            r.put("sku", rs.getString("sku"));
            r.put("name", rs.getString("name"));
            r.put("quantity", rs.getInt("quantity"));
            r.put("qr_path", rs.getString("qr_path"));
            r.put("created_at", rs.getString("created_at"));
            r.put("updated_at", rs.getString("updated_at"));
            rows.add(r);
        }
    } else {
        flashError = "Unable to connect to database.";
    }
} catch (Exception e) {
    flashError = "Error loading products: " + e.getMessage();
    e.printStackTrace();
} finally {
    try { if (rs != null) rs.close(); } catch (Exception _e) {}
    try { if (ps != null) ps.close(); } catch (Exception _e) {}
    try { if (conn != null) conn.close(); } catch (Exception _e) {}
}
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Product List</title>
  <style>
    body{font-family:Arial;background:#f5f5f5;margin:0;padding:20px}
    .container{max-width:1200px;margin:0 auto}
    table{width:100%;border-collapse:collapse;background:#fff}
    th,td{padding:10px;border-bottom:1px solid #e6e6e6;text-align:left;vertical-align:middle}
    th{background:#007bff;color:#fff}
    img.qr{width:80px;height:80px;object-fit:cover;border:1px solid #ddd;padding:4px;background:#fff}
    .topbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:12px}
    .btn{display:inline-block;padding:8px 12px;background:#007bff;color:#fff;text-decoration:none;border-radius:6px}
    .muted{color:#666;font-size:0.9rem}
    .searchbar{display:flex;align-items:center;gap:8px}
    .smallbtn{padding:8px 12px;border-radius:6px;background:#28a745;color:#fff;border:none;cursor:pointer}
    .action-btn{padding:8px 12px;border-radius:6px;border:none;cursor:pointer;margin-right:8px;text-decoration:none;display:inline-block}
    .edit-btn{background:#17a2b8;color:#fff}
    .delete-btn{background:#dc3545;color:#fff}
    .download-btn{background:#6c757d;color:#fff}
    .regenerate-btn{background:#ffc107;color:#000}
    .pager a{margin:0 6px;text-decoration:none;color:#007bff}
    .pager strong{margin:0 6px}
    .flash-success{background:#d4edda;color:#155724;padding:10px;border-radius:6px;margin-bottom:12px}
    .flash-error{background:#f8d7da;color:#721c24;padding:10px;border-radius:6px;margin-bottom:12px}
  </style>
</head>
<body>
<div class="container">
  <div class="topbar">
    <h2>Product Inventory</h2>
    <div style="display:flex;gap:10px;align-items:center">
      <a href="<%= request.getContextPath() %>/addProduct.jsp" class="btn">+ Add Product</a>
      <a href="<%= request.getContextPath() %>/testdb" class="muted">(Test DB)</a>
    </div>
  </div>

  <% if (flashMsg != null) { %>
    <div class="flash-success"><%= flashMsg %></div>
  <% } %>
  <% if (flashError != null) { %>
    <div class="flash-error"><%= flashError %></div>
  <% } %>

  <div style="margin-bottom:12px">
    <form method="get" action="listProducts.jsp" class="searchbar">
      <input type="text" name="q" placeholder="Search SKU or name"
             value="<%= (q==null) ? "" : q %>"
             style="padding:8px;border-radius:6px;border:1px solid #ccc;width:320px"/>
      <button type="submit" class="smallbtn">Search</button>
      <a href="listProducts.jsp" style="margin-left:8px;color:#666;text-decoration:underline">Clear</a>
    </form>
  </div>

  <table>
    <tr>
      <th>SKU</th>
      <th>Name</th>
      <th>Qty</th>
      <th>QR Code</th>
      <th>Actions</th>
      <th>Created</th>
      <th>Updated</th>
    </tr>

  <% if (rows.isEmpty()) { %>
    <tr><td colspan="7">No products found.</td></tr>
  <% } else {
       for (Map<String,Object> r : rows) {
           int id = (Integer) r.get("id");
           String sku = (String) r.get("sku");
           String name = (String) r.get("name");
           int qty = (Integer) r.get("quantity");
           String qrPath = (String) r.get("qr_path");
           String created = (String) r.get("created_at");
           String updated = (String) r.get("updated_at");
           String webPath = "";
           if (qrPath != null && !qrPath.trim().isEmpty()) {
               webPath = qrPath.startsWith("/") ? (request.getContextPath() + qrPath) : (request.getContextPath() + "/" + qrPath);
           }
  %>
    <tr>
      <td><%= sku %></td>
      <td><%= name %></td>
      <td><%= qty %></td>
      <td>
        <% if (!webPath.isEmpty()) { %>
          <a href="<%= webPath %>" target="_blank"><img class="qr" src="<%= webPath %>" alt="QR for <%= sku %>"/></a>
        <% } else { %>
          <span class="muted">No QR</span>
        <% } %>
      </td>
      <td>
        <% if (!webPath.isEmpty()) { %>
          <a class="action-btn download-btn" href="<%= webPath %>" download>Download</a>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/generateQR" style="display:inline">
          <input type="hidden" name="productId" value="<%= id %>"/>
          <button class="action-btn regenerate-btn" type="submit"><%= (webPath.isEmpty()) ? "Generate QR" : "Regenerate" %></button>
        </form>

        <a class="action-btn edit-btn" href="<%= request.getContextPath() %>/editProduct.jsp?id=<%= id %>">Edit</a>

        <form method="post" action="<%= request.getContextPath() %>/deleteProduct" style="display:inline" onsubmit="return confirm('Delete this product?');">
          <input type="hidden" name="productId" value="<%= id %>"/>
          <button class="action-btn delete-btn" type="submit">Delete</button>
        </form>
      </td>
      <td><%= created %></td>
      <td><%= updated %></td>
    </tr>
  <%   } // end for
     } // end else
  %>

  </table>
</div>
</body>
</html>
