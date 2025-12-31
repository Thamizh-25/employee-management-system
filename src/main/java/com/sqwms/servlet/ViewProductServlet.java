package com.sqwms.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sqwms.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/viewProduct")
public class ViewProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID missing");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT sku, name, quantity, created_at, updated_at FROM products WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idParam));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                request.setAttribute("sku", rs.getString("sku"));
                request.setAttribute("name", rs.getString("name"));
                request.setAttribute("quantity", rs.getInt("quantity"));
                request.setAttribute("createdAt", rs.getString("created_at"));
                request.setAttribute("updatedAt", rs.getString("updated_at"));

                request.getRequestDispatcher("viewProduct.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
