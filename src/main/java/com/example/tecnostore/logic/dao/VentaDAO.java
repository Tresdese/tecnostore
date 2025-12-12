package com.example.tecnostore.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VentaDAO {

    public void insertarVenta(Connection conn, String usuario, double monto) throws SQLException {
        String sql = "INSERT INTO ventas (usuario, monto_total) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setDouble(2, monto);
            stmt.executeUpdate();
        }
    }
}