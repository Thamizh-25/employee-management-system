package com.sqwms.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class QRGenerator {

    /**
     * Generate a PNG QR code with given text and save to (folderPath/fileName).
     * @param text the qr content
     * @param fileName the file name e.g. "SKU001_qr.png"
     * @param folderPath absolute folder path on disk (must be writable)
     * @return relative web path to use in <img src> (e.g. "qr_codes/..") or null on error
     */
    public static String generateQR(String text, String fileName, String folderPath) {
        try {
            File directory = new File(folderPath);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    System.err.println("QRGenerator: failed to create directory " + folderPath);
                    return null;
                }
            }

            Path filePath = Paths.get(folderPath, fileName);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);

            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);

            // return a web-relative path (assuming folderPath maps to webapp/qr_codes)
            return "qr_codes/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
