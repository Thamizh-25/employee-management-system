package com.sqwms.servlet;

import com.sqwms.util.DBConnection;
import com.sqwms.util.QRGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/addProduct")
public class AddProductServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {

	    resp.setContentType("text/plain");
	    // Don't use out.println for final UX — we'll redirect with a flash message
	    try {
	        // Read form inputs
	        String sku = req.getParameter("sku");
	        String name = req.getParameter("name");
	        String description = req.getParameter("description");
	        int quantity = Integer.parseInt(req.getParameter("quantity"));

	        // Create QR text content
	        String qrText = "SKU: " + sku + " | Name: " + name;

	        // Generate QR image file name
	        String fileName = sku + "_qr.png";

	        // Compute deployed webapp folder and qr folder
	        String appPath = getServletContext().getRealPath("");
	        String qrFolder = appPath + java.io.File.separator + "qr_codes";

	        // Generate QR and get relative web path
	        String qrPath = com.sqwms.util.QRGenerator.generateQR(qrText, fileName, qrFolder);
	        if (qrPath == null) {
	            // generation failed — set error and redirect back to add page
	            req.getSession().setAttribute("flashError", "Failed to generate QR for product.");
	            resp.sendRedirect(req.getContextPath() + "/addProduct.jsp");
	            return;
	        }

	        // Save product + qr info to DB
	        try (java.sql.Connection conn = com.sqwms.util.DBConnection.getConnection()) {
	            String sql = "INSERT INTO products (sku, name, description, quantity, qr_text, qr_path) "
	                       + "VALUES (?, ?, ?, ?, ?, ?)";

	            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
	            stmt.setString(1, sku);
	            stmt.setString(2, name);
	            stmt.setString(3, description);
	            stmt.setInt(4, quantity);
	            stmt.setString(5, qrText);
	            stmt.setString(6, qrPath);

	            stmt.executeUpdate();
	        }

	        // Set success flash and redirect to listing
	        req.getSession().setAttribute("flashMsg", "Product added successfully!");
	        resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
	        return;

	    } catch (Exception e) {
	        // Log server-side and redirect with error
	        e.printStackTrace();
	        req.getSession().setAttribute("flashError", "Error adding product: " + e.getMessage());
	        resp.sendRedirect(req.getContextPath() + "/addProduct.jsp");
	        return;
	    }
	}
}
