package com.example.tecnostore.logic.servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.example.tecnostore.logic.dao.LogDAO;
import com.example.tecnostore.logic.dao.VentaDAO;

public class VentaService {

    private final String DB_URL = "jdbc:mysql://localhost:3306/seguridad_ventas";
    private final String DB_USER = "root";
    private final String DB_PASS = "4422";

    private final VentaDAO ventaDAO = new VentaDAO();
    private final LogDAO logDAO;

    public VentaService() {
        try {
            this.logDAO = new LogDAO();
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar LogDAO: " + e.getMessage(), e);
        }
    }

    public void procesarVenta(String usuario, double monto) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            conn.setAutoCommit(false);

            if (monto <= 0) {
                throw new IllegalArgumentException("El monto debe ser positivo.");
            }

            ventaDAO.insertarVenta(conn, usuario, monto);
            logDAO.registrarLog(conn, usuario, "REGISTRO_VENTA", "EXITO", "Monto: " + monto);

            conn.commit();
            System.out.println("Venta procesada correctamente.");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    logDAO.registrarLog(conn, usuario, "REGISTRO_VENTA", "ERROR", e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error en transacción: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}