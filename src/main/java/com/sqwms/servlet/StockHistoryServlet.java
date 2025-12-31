package com.sqwms.servlet;

import com.sqwms.util.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/stockHistory")
public class StockHistoryServlet extends HttpServlet {

 protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

  int productId = Integer.parseInt(req.getParameter("id"));
  resp.setContentType("text/html");

  try(Connection conn = DBConnection.getConnection()){
    String sql = "SELECT * FROM stock_history WHERE product_id=? ORDER BY changed_at DESC";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, productId);
    ResultSet rs = ps.executeQuery();

    PrintWriter out = resp.getWriter();
    out.println("<h3>Stock History</h3>");
    out.println("<table width='100%' border='1' cellpadding='6'>");
    out.println("<tr><th>Date</th><th>Action</th><th>Old Qty</th><th>New Qty</th></tr>");

    while(rs.next()){
      out.println("<tr>");
      out.println("<td>"+rs.getString("changed_at")+"</td>");
      out.println("<td>"+rs.getString("action")+"</td>");
      out.println("<td>"+rs.getInt("old_qty")+"</td>");
      out.println("<td>"+rs.getInt("new_qty")+"</td>");
      out.println("</tr>");
    }
    out.println("</table>");
  }catch(Exception e){
    e.printStackTrace();
  }
 }
}
