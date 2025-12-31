package com.sqwms.servlet;

import com.sqwms.util.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String productId = req.getParameter("productId");
        if (productId == null) {
            out.print("<p>No product selected</p>");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                "SELECT action, old_qty, new_qty, changed_at " +
                "FROM stock_history WHERE product_id=? ORDER BY changed_at DESC"
            );
            ps.setInt(1, Integer.parseInt(productId));
            ResultSet rs = ps.executeQuery();

            out.println("<div class='timeline'>");

            if (!rs.isBeforeFirst()) {
                out.println("<div class='empty'>No history available</div>");
            }

            while (rs.next()) {
                out.println(
                    "<div class='event'>" +
                        "<div class='dot'></div>" +

                        "<div class='content'>" +
                            "<div class='time'>" +
                                "<i class='fa-regular fa-clock'></i> " +
                                rs.getTimestamp("changed_at") +
                            "</div>" +

                            "<div class='action'>" +
                                "<i class='fa-solid fa-rotate'></i> " +
                                rs.getString("action") +
                            "</div>" +

                            "<div class='qty'>" +
                                "Qty: " + rs.getInt("old_qty") +
                                " <span>→</span> " +
                                "<b>" + rs.getInt("new_qty") + "</b>" +
                            "</div>" +
                        "</div>" +
                    "</div>"
                );
            }

            out.println("</div>");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("<p class='error'>Error loading history</p>");
        }
    }
}
