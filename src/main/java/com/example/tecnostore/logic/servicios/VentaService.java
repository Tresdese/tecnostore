package com.example.tecnostore.logic.servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.example.tecnostore.logic.dao.LogDAO;
import com.example.tecnostore.logic.dao.VentaDAO;
import com.example.tecnostore.logic.dao.DetalleVentaDAO;
import com.example.tecnostore.logic.dao.ProductoDAO;
import com.example.tecnostore.logic.dto.VentaDTO;
import com.example.tecnostore.logic.dto.VentaResumenDTO;
import com.example.tecnostore.logic.dto.DetalleVentaDTO;
import com.example.tecnostore.logic.utils.Sesion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VentaService {

    private static final Logger LOGGER = LogManager.getLogger(VentaService.class);

    private final String DB_URL = "jdbc:mysql://localhost:3306/seguridad_ventas";
    private final String DB_USER = "root";
    private final String DB_PASS = "4422";

    private VentaDAO ventaDAO;
    private LogDAO logDAO;
    private DetalleVentaDAO detalleVentaDAO;
    private ProductoDAO productoDAO;

    public VentaService() {
        try {
            this.ventaDAO = new VentaDAO();
            this.logDAO = new LogDAO();
            this.detalleVentaDAO = new DetalleVentaDAO();
            this.productoDAO = new ProductoDAO();
        } catch (Exception e) {
            LOGGER.error("Error al inicializar DAOs: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar DAOs de Venta: " + e.getMessage(), e);
        }
    }

    public List<VentaResumenDTO> obtenerTodasLasVentas() throws Exception {
        return ventaDAO.obtenerVentas();
    }

    public void registrarVentaCompleta(VentaDTO venta) throws Exception {

        String usuarioActual = Sesion.getUsuarioSesion() != null ? Sesion.getUsuarioSesion().getUsuario() : "Sistema";

        if (venta.getTotal() <= 0 || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El monto debe ser positivo y la venta no debe estar vacía.");
        }

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            conn.setAutoCommit(false);

            int ventaId = ventaDAO.insertarVenta(conn, usuarioActual, venta.getTotal());

            for (DetalleVentaDTO detalle : venta.getDetalles()) {
                int cantidadVendida = detalle.getCantidad();
                int productoId = detalle.getProductoId();

                productoDAO.actualizarStock(productoId, 500 - cantidadVendida);
            }

            detalleVentaDAO.registrarDetalles(conn, ventaId, venta.getDetalles());

            logDAO.registrarLog(conn, usuarioActual, "REGISTRO_VENTA", "EXITO", "Venta ID: " + ventaId + ", Total: " + venta.getTotal());
            conn.commit();

            LOGGER.info("Venta ID {} procesada correctamente por {}.", ventaId, usuarioActual);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logDAO.registrarLog(conn, usuarioActual, "REGISTRO_VENTA", "ERROR", e.getMessage());
                } catch (SQLException ex) {
                    LOGGER.error("Error durante el rollback: {}", ex.getMessage());
                }
            }
            LOGGER.error("Error en transacción de venta: {}", e.getMessage(), e);
            throw new Exception("Error al registrar la venta: " + e.getMessage());

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Error al cerrar conexión: {}", e.getMessage());
                }
            }
        }
    }
}