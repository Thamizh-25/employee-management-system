<%@ page import="java.sql.*" %>
<%@ page import="com.sqwms.util.DBConnection" %>

<!DOCTYPE html>
<html>
<head>
<title>SmartQR WMS - Product Inventory</title>

<!-- Font Awesome -->
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"/>

<style>
/* ---------- BASE ---------- */
body{
  font-family:Arial, sans-serif;
  background:#f5f7fb;
  margin:0;
  padding:20px;
}

/* ---------- HEADER ---------- */
.header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:20px;
}
.brand{
  display:flex;
  align-items:center;
  gap:10px;
  font-size:20px;
  font-weight:bold;
}
.logo{
  width:42px;
  height:42px;
  border-radius:50%;
  background:#0d6efd;
  color:#fff;
  display:flex;
  align-items:center;
  justify-content:center;
}
.add-btn{
  background:#0dcaf0;
  color:#fff;
  padding:10px 16px;
  border-radius:8px;
  text-decoration:none;
  font-weight:bold;
  white-space:nowrap;
}

/* ---------- DASHBOARD ---------- */
.cards{
  display:flex;
  gap:20px;
  margin-bottom:20px;
}
.card{
  padding:16px;
  border-radius:10px;
  min-width:160px;
}
.total{background:#e7f3ff}
.low{background:#fff3cd}
.critical{background:#f8d7da}
.card span{
  font-size:26px;
  font-weight:bold;
}

/* ---------- SEARCH ---------- */
.search-box{
  margin-bottom:20px;
}
.search-box input{
  padding:10px;
  width:260px;
}
.search-box button{
  padding:10px 14px;
}
.search-box a{
  margin-left:10px;
}

/* ---------- TABLE ---------- */
table{
  width:100%;
  border-collapse:collapse;
  background:#fff;
}
th,td{
  padding:12px;
  border:1px solid #ddd;
  text-align:center;
}
th{
  background:#0d6efd;
  color:#fff;
}
tr.low-stock{background:#fff3cd}
tr.critical-stock{background:#f8d7da}

/* ---------- BUTTONS ---------- */
.btn{
  padding:8px 14px;
  border-radius:6px;
  color:#fff;
  text-decoration:none;
  font-weight:bold;
  display:inline-block;
  min-width:90px;
  text-align:center;
}
.download{background:#6c757d}
.edit{background:#17a2b8}
.history{background:#343a40}
.delete{
  background:#dc3545;
  border:none;
  cursor:pointer;
}

/* ---------- MODAL ---------- */
.modal{
  display:none;
  position:fixed;
  inset:0;
  background:rgba(0,0,0,0.4);
  justify-content:center;
  align-items:center;
  z-index:1000;
}
.modal-content{
  background:#fff;
  width:420px;
  border-radius:12px;
  padding:16px;
}
.modal-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:10px;
}
.close-btn{
  cursor:pointer;
  font-size:18px;
}

/* ---------- TIMELINE ---------- */
.timeline{
  position:relative;
  margin-left:20px;
}
.timeline::before{
  content:'';
  position:absolute;
  left:6px;
  top:0;
  bottom:0;
  width:2px;
  background:#0d6efd;
}
.event{
  display:flex;
  gap:12px;
  margin-bottom:14px;
}
.dot{
  width:12px;
  height:12px;
  background:#0d6efd;
  border-radius:50%;
  margin-top:6px;
}
.content{
  background:#f8f9fa;
  padding:10px;
  border-radius:8px;
  width:100%;
}
.time{font-size:12px;color:#666}
.action{color:#ff9800;font-weight:bold}
.qty{font-size:14px}
</style>
</head>

<body>

<!-- HEADER -->
<div class="header">
  <div class="brand">
    <div class="logo"><i class="fa-solid fa-box"></i></div>
    SmartQR WMS
  </div>
  <a href="addProduct.jsp" class="add-btn">+ Add Product</a>
</div>

<h2 style="text-align:center">PRODUCT INVENTORY</h2>

<%
Connection conn = DBConnection.getConnection();
Statement st = conn.createStatement();

ResultSet rsAll = st.executeQuery("SELECT COUNT(*) FROM products");
rsAll.next();
int total = rsAll.getInt(1);

ResultSet rsLow = st.executeQuery("SELECT COUNT(*) FROM products WHERE quantity BETWEEN 6 AND 15");
rsLow.next();
int low = rsLow.getInt(1);

ResultSet rsCritical = st.executeQuery("SELECT COUNT(*) FROM products WHERE quantity <=5");
rsCritical.next();
int critical = rsCritical.getInt(1);
%>

<!-- DASHBOARD -->
<div class="cards">
  <div class="card total">Total Products<br><span><%=total%></span></div>
  <div class="card low">Low Stock<br><span><%=low%></span></div>
  <div class="card critical">Critical Stock<br><span><%=critical%></span></div>
</div>

<!-- SEARCH -->
<div class="search-box">
<form method="get">
  <input type="text" name="q" placeholder="Search SKU or name"
         value="<%= request.getParameter("q")==null?"":request.getParameter("q") %>">
  <button type="submit">Search</button>
  <a href="listProducts.jsp">Clear</a>
</form>
</div>

<table>
<tr>
  <th>SKU</th>
  <th>Name</th>
  <th>Qty</th>
  <th>QR</th>
  <th>Actions</th>
  <th>Created</th>
  <th>Updated</th>
</tr>

<%
String q = request.getParameter("q");
PreparedStatement ps;
if(q!=null && !q.isEmpty()){
  ps = conn.prepareStatement(
    "SELECT * FROM products WHERE sku LIKE ? OR name LIKE ? ORDER BY updated_at DESC");
  ps.setString(1,"%"+q+"%");
  ps.setString(2,"%"+q+"%");
}else{
  ps = conn.prepareStatement("SELECT * FROM products ORDER BY updated_at DESC");
}

ResultSet rs = ps.executeQuery();

while(rs.next()){
  int qty = rs.getInt("quantity");
  String rowClass = qty<=5 ? "critical-stock" : (qty<=15?"low-stock":"");
%>

<tr class="<%=rowClass%>">
  <td><%=rs.getString("sku")%></td>
  <td><%=rs.getString("name")%></td>
  <td><%=qty%></td>
  <td><img src="<%=request.getContextPath()+"/"+rs.getString("qr_path")%>" width="70"></td>
  <td>
    <a class="btn download" href="<%=request.getContextPath()+"/"+rs.getString("qr_path")%>" download>Download</a>
    <a class="btn edit" href="editProduct.jsp?id=<%=rs.getInt("id")%>">Edit</a>
    <button class="btn history" onclick="openHistory(<%=rs.getInt("id")%>)">History</button>
    <form action="<%=request.getContextPath()%>/deleteProduct" method="post" style="display:inline"
          onsubmit="return confirm('Delete product?');">
      <input type="hidden" name="productId" value="<%=rs.getInt("id")%>">
      <button class="btn delete">Delete</button>
    </form>
  </td>
  <td><%=rs.getTimestamp("created_at")%></td>
  <td><%=rs.getTimestamp("updated_at")%></td>
</tr>

<% } %>
</table>

<!-- HISTORY MODAL -->
<div class="modal" id="historyModal">
  <div class="modal-content">
    <div class="modal-header">
      <b><i class="fa-solid fa-clock-rotate-left"></i> Stock History</b>
      <span class="close-btn" onclick="closeHistory()">
        <i class="fa-solid fa-arrow-left"></i>
      </span>
    </div>
    <div id="historyBody"></div>
  </div>
</div>

<script>
function openHistory(id){
  fetch('<%=request.getContextPath()%>/history?productId='+id)
    .then(res=>res.text())
    .then(html=>{
      document.getElementById('historyBody').innerHTML = html;
      document.getElementById('historyModal').style.display='flex';
    });
}
function closeHistory(){
  document.getElementById('historyModal').style.display='none';
}
</script>

</body>
</html>
