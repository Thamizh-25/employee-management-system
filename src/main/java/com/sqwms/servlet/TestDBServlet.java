package com.sqwms.servlet;

import com.sqwms.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

@WebServlet("/testdb")
public class TestDBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                out.println("SUCCESS: Connected to MySQL Database 🎉");
            } else {
                out.println("FAILED: Connection is null or closed.");
            }
        } catch (Exception e) {
            out.println("ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
    }
}
