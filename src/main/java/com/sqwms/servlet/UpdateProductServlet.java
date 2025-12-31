package com.sqwms.servlet;

import com.sqwms.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/updateProduct")
public class UpdateProductServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int productId = Integer.parseInt(req.getParameter("id"));
        String sku = req.getParameter("sku");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        int newQty = Integer.parseInt(req.getParameter("quantity"));

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // 🔐 TRANSACTION START

            // 1️⃣ Fetch old quantity
            int oldQty = 0;
            PreparedStatement psOld = conn.prepareStatement(
                    "SELECT quantity FROM products WHERE id = ?");
            psOld.setInt(1, productId);
            ResultSet rs = psOld.executeQuery();

            if (rs.next()) {
                oldQty = rs.getInt("quantity");
            }
            rs.close();
            psOld.close();

            // 2️⃣ Update product
            PreparedStatement psUpdate = conn.prepareStatement(
                "UPDATE products SET sku=?, name=?, description=?, quantity=?, updated_at=NOW() WHERE id=?"
            );
            psUpdate.setString(1, sku);
            psUpdate.setString(2, name);
            psUpdate.setString(3, description);
            psUpdate.setInt(4, newQty);
            psUpdate.setInt(5, productId);
            psUpdate.executeUpdate();
            psUpdate.close();

            // 3️⃣ Log stock change ONLY if quantity changed
            if (oldQty != newQty) {
                PreparedStatement psHistory = conn.prepareStatement(
                    "INSERT INTO stock_history (product_id, sku, old_qty, new_qty, action) " +
                    "VALUES (?, ?, ?, ?, ?)"
                );
                psHistory.setInt(1, productId);
                psHistory.setString(2, sku);
                psHistory.setInt(3, oldQty);
                psHistory.setInt(4, newQty);
                psHistory.setString(5, "STOCK_UPDATED");
                psHistory.executeUpdate();
                psHistory.close();
            }

            conn.commit(); // ✅ TRANSACTION SUCCESS

            req.getSession().setAttribute("flashMsg", "Product updated successfully");
            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            throw new ServletException(e);
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
