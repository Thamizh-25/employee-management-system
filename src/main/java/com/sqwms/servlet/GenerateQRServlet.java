package com.sqwms.servlet;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;


@WebServlet("/generateQR")
public class GenerateQRServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("productId");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
            return;
        }

        int productId = Integer.parseInt(idParam);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = com.sqwms.util.DBConnection.getConnection();
            if (conn == null) {
                resp.getWriter().write("DB connection failed");
                return;
            }

            ps = conn.prepareStatement("SELECT sku, name FROM products WHERE id = ?");
            ps.setInt(1, productId);
            rs = ps.executeQuery();

            if (!rs.next()) {
                resp.getWriter().write("Product not found");
                return;
            }

            String sku = rs.getString("sku");
            String name = rs.getString("name");
            String qrText = "SKU: " + sku + " | Name: " + name;

            String qrDirPath = getServletContext().getRealPath("/qr_codes");
            if (qrDirPath == null) qrDirPath = System.getProperty("java.io.tmpdir") + File.separator + "qr_codes";

            File qrDir = new File(qrDirPath);
            if (!qrDir.exists()) qrDir.mkdirs();

            String fileName = sku + "_qr.png";
            Path qrFilePath = Paths.get(qrDirPath, fileName);

            int size = 300;
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix matrix = qrWriter.encode(qrText, BarcodeFormat.QR_CODE, size, size);
            MatrixToImageWriter.writeToPath(matrix, "PNG", qrFilePath);

            String webPath = "qr_codes/" + fileName;

            PreparedStatement ups = conn.prepareStatement(
                "UPDATE products SET qr_path = ?, qr_text = ? WHERE id = ?"
            );
            ups.setString(1, webPath);
            ups.setString(2, qrText);
            ups.setInt(3, productId);
            ups.executeUpdate();
            ups.close();

            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");

        } catch (WriterException we) {
            throw new ServletException(we);
        } catch (Exception ex) {
            throw new ServletException(ex);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
