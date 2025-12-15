package com.sqwms.servlet;

import com.sqwms.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/updateProduct")
public class UpdateProductServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            int quantity = Integer.parseInt(req.getParameter("quantity"));

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE products SET name=?, description=?, quantity=?, updated_at = CURRENT_TIMESTAMP WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setString(2, description);
                    ps.setInt(3, quantity);
                    ps.setInt(4, id);
                    ps.executeUpdate();
                }
            }

            req.getSession().setAttribute("flashMsg", "Product updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashError", "Failed to update product: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
        }
    }
}

