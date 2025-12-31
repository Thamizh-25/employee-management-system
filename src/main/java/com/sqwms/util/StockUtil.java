package com.sqwms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StockUtil {

    public static void logStockChange(
            int productId,
            String sku,
            int oldQty,
            int newQty,
            String action
    ) {

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO stock_history (product_id, sku, old_qty, new_qty, action) VALUES (?, ?, ?, ?, ?)"
            );

            ps.setInt(1, productId);
            ps.setString(2, sku);
            ps.setInt(3, oldQty);
            ps.setInt(4, newQty);
            ps.setString(5, action);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
