package com.example.tecnostore.logic.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dao.LogDAO;
import com.example.tecnostore.logic.dao.ProductoDAO;
import com.example.tecnostore.logic.dao.VentaDAO;
import com.example.tecnostore.logic.dto.ProductoDTO;

public class VentaService {

    private final VentaDAO ventaDAO;
    private final LogDAO logDAO;
    private final ProductoDAO productoDAO;

    public VentaService() throws Exception {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        try {
            this.logDAO = new LogDAO();
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar LogDAO: " + e.getMessage(), e);
        }
    }

    // Adaptamos el método para que reciba la lista del controlador,
    // pero use tu DAO específico internamente.
    public void procesarVenta(int usuarioId, int sucursalId, List<ProductoDTO> productos) throws Exception {
        Connection conn = null;

        // 1. Calcular el monto total sumando la lista (Tu DAO pide el total ya calculado)
        double montoTotal = 0;
        for (ProductoDTO p : productos) {
            montoTotal += p.getPrecio() * p.getStock();
        }

        try {
            // Usamos tu conexión centralizada
            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false); // Transacción para seguridad

            if (montoTotal <= 0) {
                throw new IllegalArgumentException("El monto debe ser positivo.");
            }

            // 2. Descontar Inventario (Esto se mantiene igual para que el stock baje)
            for (ProductoDTO item : productos) {
                ProductoDTO enBD = productoDAO.buscarPorId(item);
                if (enBD.getStock() < item.getStock()) {
                    throw new Exception("Stock insuficiente para: " + item.getNombre());
                }
                productoDAO.actualizarStock(item.getId(), enBD.getStock() - item.getStock());
            }

            // 3. Registrar la Venta usando TU ESTRUCTURA ACTUAL DE VentaDAO
            // Tu DAO pide (Connection, String usuario, double monto).
            // Convertimos el int usuarioId a String para cumplir con tu código.
            ventaDAO.insertarVenta(conn, String.valueOf(usuarioId), montoTotal);

            // 4. Auditoría
            logDAO.registrarLog(conn, String.valueOf(usuarioId), "REGISTRO_VENTA", "EXITO", "Monto: " + montoTotal);

            conn.commit();
            System.out.println("Venta procesada correctamente.");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    logDAO.registrarLog(conn, String.valueOf(usuarioId), "REGISTRO_VENTA", "ERROR", e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}