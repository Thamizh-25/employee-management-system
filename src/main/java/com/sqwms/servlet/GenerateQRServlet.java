package com.sqwms.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sqwms.util.DBConnection;

@WebServlet("/generateQR")
public class GenerateQRServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("productId");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");
            return;
        }

        int productId = Integer.parseInt(idParam);

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                "SELECT sku FROM products WHERE id = ?"
            );
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
                return;
            }

            String sku = rs.getString("sku");

            /* ✅ VERY IMPORTANT PART */
            String serverIP = req.getLocalAddr(); // auto-detect IP
            String qrText =
                "http://" + serverIP + ":8080" +
                req.getContextPath() +
                "/viewProduct?id=" + productId;

            String qrDirPath = getServletContext().getRealPath("/qr_codes");
            File qrDir = new File(qrDirPath);
            if (!qrDir.exists()) qrDir.mkdirs();

            String fileName = sku + "_qr.png";
            Path qrFilePath = Paths.get(qrDirPath, fileName);

            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix matrix = qrWriter.encode(
                qrText,
                BarcodeFormat.QR_CODE,
                300,
                300
            );

            MatrixToImageWriter.writeToPath(matrix, "PNG", qrFilePath);

            String webPath = "qr_codes/" + fileName;

            PreparedStatement update = conn.prepareStatement(
                "UPDATE products SET qr_path = ?, qr_text = ? WHERE id = ?"
            );
            update.setString(1, webPath);
            update.setString(2, qrText);
            update.setInt(3, productId);
            update.executeUpdate();

            resp.sendRedirect(req.getContextPath() + "/listProducts.jsp");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}
