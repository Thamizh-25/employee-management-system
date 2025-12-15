package com.sqwms.servlet;

import com.sqwms.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/deleteProduct")
public class DeleteProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("productId");
        if (idParam == null || idParam.isEmpty()) {
            req.getSession().setAttribute("flashError", "No product id provided.");
            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
            return;
        }

        int id = -1;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException ex) {
            req.getSession().setAttribute("flashError", "Invalid product id.");
            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                req.getSession().setAttribute("flashError", "DB connection failed.");
                resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
                return;
            }

            // get qr_path (so we can delete file)
            ps = conn.prepareStatement("SELECT qr_path FROM products WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            String qrPath = null;
            if (rs.next()) {
                qrPath = rs.getString("qr_path");
            }
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}

            // delete DB row
            ps = conn.prepareStatement("DELETE FROM products WHERE id = ?");
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                req.getSession().setAttribute("flashError", "Product not found or already deleted.");
                resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
                return;
            }

            // try to delete the QR file from disk (if path exists)
            if (qrPath != null && !qrPath.trim().isEmpty()) {
                // qrPath stored like "qr_codes/FILE.png"
                String webRoot = getServletContext().getRealPath("/");
                if (webRoot == null) webRoot = System.getProperty("java.io.tmpdir") + File.separator;
                File qrFile = new File(webRoot, qrPath.replace("/", File.separator));
                if (qrFile.exists()) {
                    try { qrFile.delete(); } catch (Exception ex) { /* ignore */ }
                }
            }

            req.getSession().setAttribute("flashMsg", "Product deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashError", "Failed to delete product.");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
    }
}
