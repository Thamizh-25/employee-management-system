<%@ page import="java.sql.*" %>
<%@ page import="com.sqwms.util.DBConnection" %>

<!DOCTYPE html>
<html>
<head>
<title>Stock History</title>

<link rel="stylesheet"
 href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<style>
body{
 font-family:Arial;
 margin:0;
 padding:16px;
 background:#fff;
}

/* HEADER */
.header{
 display:flex;
 align-items:center;
 justify-content:space-between;
 margin-bottom:12px;
}
.header h3{
 margin:0;
 font-size:18px;
 display:flex;
 align-items:center;
 gap:8px;
}
.close{
 cursor:pointer;
 font-size:18px;
 color:#333;
}

/* TIMELINE */
.timeline{
 position:relative;
 padding-left:28px;
}
.timeline::before{
 content:'';
 position:absolute;
 left:8px;
 top:0;
 width:3px;
 height:100%;
 background:#0d6efd;
 border-radius:2px;
}

.event{
 position:relative;
 margin-bottom:16px;
}
.dot{
 position:absolute;
 left:-28px;
 top:4px;
 width:14px;
 height:14px;
 background:#0d6efd;
 border-radius:50%;
}

.time{
 font-size:13px;
 color:#666;
 margin-bottom:2px;
}
.action{
 font-weight:bold;
 color:#ff9800;
 font-size:14px;
}
.qty{
 font-size:14px;
 color:#222;
}
</style>

<script>
function closeHistory(){
 parent.document.getElementById("historyModal").style.display="none";
}
</script>
</head>

<body>

<div class="header">
 <h3><i class="fa-solid fa-clock-rotate-left"></i> Stock History</h3>
 <span class="close" onclick="closeHistory()">
  <i class="fa-solid fa-xmark"></i>
 </span>
</div>

<div class="timeline">
<%
int productId = Integer.parseInt(request.getParameter("productId"));
Connection conn = DBConnection.getConnection();

PreparedStatement ps = conn.prepareStatement(
 "SELECT * FROM stock_history WHERE product_id=? ORDER BY changed_at DESC"
);
ps.setInt(1, productId);
ResultSet rs = ps.executeQuery();

while(rs.next()){
%>

<div class="event">
 <div class="dot"></div>

 <div class="time">
  <i class="fa-regular fa-clock"></i>
  <%= rs.getTimestamp("changed_at") %>
 </div>

 <div class="action">
  <i class="fa-solid fa-arrows-rotate"></i>
  <%= rs.getString("action") %>
 </div>

 <div class="qty">
  Qty: <%= rs.getInt("old_qty") %> &rarr;
  <b><%= rs.getInt("new_qty") %></b>
 </div>
</div>

<% }
conn.close();
%>
</div>

</body>
</html>
