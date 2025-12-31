package com.sqwms.servlet;

import com.sqwms.util.DBConnection;
import com.sqwms.util.QRGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/addProduct")
public class AddProductServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 🔹 Read inputs
            String sku = req.getParameter("sku");
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            int quantity = Integer.parseInt(req.getParameter("quantity"));

            // 🔹 QR content
            String qrText = "SKU: " + sku + " | Name: " + name;
            String fileName = sku + "_qr.png";

            String appPath = getServletContext().getRealPath("");
            String qrFolder = appPath + java.io.File.separator + "qr_codes";
            String qrPath = QRGenerator.generateQR(qrText, fileName, qrFolder);

            Connection conn = DBConnection.getConnection();

            // 🔹 INSERT PRODUCT
            String insertProduct =
                    "INSERT INTO products (sku, name, description, quantity, qr_text, qr_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps =
                    conn.prepareStatement(insertProduct, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, sku);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setInt(4, quantity);
            ps.setString(5, qrText);
            ps.setString(6, qrPath);

            ps.executeUpdate();

            // 🔹 GET PRODUCT ID
            ResultSet rs = ps.getGeneratedKeys();
            int productId = -1;
            if (rs.next()) {
                productId = rs.getInt(1);
            }

            System.out.println("✅ Product inserted with ID = " + productId);

            // 🔥 INSERT INTO STOCK HISTORY (MATCHING TABLE)
            String insertHistory =
                    "INSERT INTO stock_history (product_id, sku, old_qty, new_qty, action) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement hs = conn.prepareStatement(insertHistory);
            hs.setInt(1, productId);
            hs.setString(2, sku);
            hs.setInt(3, 0);                 // old_qty
            hs.setInt(4, quantity);          // new_qty
            hs.setString(5, "PRODUCT_CREATED");

            int rows = hs.executeUpdate();
            System.out.println("🔥 Stock history rows inserted = " + rows);

            conn.close();

            req.getSession().setAttribute("flashMsg", "Product added successfully!");
            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashError", "Error adding product");
            resp.sendRedirect(req.getContextPath() + "/addProduct.jsp");
        }
    }
}
