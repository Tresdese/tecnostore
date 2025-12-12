package com.example.tecnostore.logic.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dao.DetalleVentaDAO;
import com.example.tecnostore.logic.dao.LogDAO;
import com.example.tecnostore.logic.dao.ProductoDAO;
import com.example.tecnostore.logic.dao.VentaDAO;
import com.example.tecnostore.logic.dto.DetalleVentaDTO;
import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.dto.VentaDTO;
import com.example.tecnostore.logic.dto.VentaResumenDTO;
import com.example.tecnostore.logic.utils.Sesion;

public class VentaService {

    private static final Logger LOGGER = LogManager.getLogger(VentaService.class);

    private final VentaDAO ventaDAO;
    private final LogDAO logDAO;
    private final ProductoDAO productoDAO;
    private final DetalleVentaDAO detalleVentaDAO;

    public VentaService() throws Exception {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        this.logDAO = new LogDAO();
        this.detalleVentaDAO = new DetalleVentaDAO();
    }

    /**
     * Procesa una venta completa con detalles, validando stock y registrando log.
     */
    public void procesarVenta(int usuarioId, List<DetalleVentaDTO> detalles) throws Exception {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La venta no tiene productos.");
        }

        double montoTotal = detalles.stream()
                .mapToDouble(detalle -> {
                    double subtotal = detalle.getSubtotal();
                    if (subtotal <= 0) {
                        subtotal = detalle.getPrecioVenta() * detalle.getCantidad();
                        detalle.setSubtotal(subtotal);
                    }
                    return subtotal;
                })
                .sum();

        if (montoTotal <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo.");
        }

        try (ConexionBD conexionBD = new ConexionBD()) {
            Connection conn = conexionBD.getConnection();
            try {
                conn.setAutoCommit(false);

                int ventaId = ventaDAO.insertarVenta(conn, String.valueOf(usuarioId), montoTotal);

                for (DetalleVentaDTO detalle : detalles) {
                    ProductoDTO consulta = new ProductoDTO();
                    consulta.setId(detalle.getProductoId());
                    ProductoDTO enBD = productoDAO.buscarPorId(consulta);
                    if (enBD == null) {
                        throw new Exception("Producto no encontrado: " + detalle.getProductoId());
                    }
                    if (enBD.getStock() < detalle.getCantidad()) {
                        throw new Exception("Stock insuficiente para: " + enBD.getNombre());
                    }
                    productoDAO.actualizarStock(enBD.getId(), enBD.getStock() - detalle.getCantidad());
                    detalle.setVentaId(ventaId);
                }

                detalleVentaDAO.registrarDetalles(conn, ventaId, detalles);

                logDAO.registrarLog(conn, String.valueOf(usuarioId), "REGISTRO_VENTA", "EXITO",
                        "Venta ID: " + ventaId + ", Monto: " + montoTotal);

                conn.commit();
                LOGGER.info("Venta {} procesada correctamente por usuario {}.", ventaId, usuarioId);

            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException ex) { LOGGER.error("Rollback falló: {}", ex.getMessage()); }
                LOGGER.error("Error en transacción de venta: {}", e.getMessage(), e);
                throw e;
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Versión simple: solo registra la venta (sin detalles) usando usuario en sesión si existe.
     */
    public void registrarVentaSimple(double monto) throws Exception {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo.");
        }
        String usuarioId = Sesion.getUsuarioSesion() != null
                ? String.valueOf(Sesion.getUsuarioSesion().getId())
                : null;
        try (ConexionBD conexionBD = new ConexionBD(); Connection conn = conexionBD.getConnection()) {
            ventaDAO.insertarVenta(conn, usuarioId, monto);
        }
    }

    /**
     * Adaptador para el modal de pago con detalles.
     */
    public void registrarVentaCompleta(VentaDTO venta, List<DetalleVentaDTO> detalles) throws Exception {
        if (venta == null) {
            throw new IllegalArgumentException("Venta requerida.");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La venta no tiene productos.");
        }

        double montoTotal = detalles.stream()
                .mapToDouble(detalle -> {
                    double subtotal = detalle.getSubtotal();
                    if (subtotal <= 0) {
                        subtotal = detalle.getPrecioVenta() * detalle.getCantidad();
                        detalle.setSubtotal(subtotal);
                    }
                    return subtotal;
                })
                .sum();

        venta.setDetalles(detalles);
        venta.setTotal(montoTotal);

        procesarVenta(
                Sesion.getUsuarioSesion() != null ? Sesion.getUsuarioSesion().getId() : 0,
                detalles
        );
    }

    /** Obtiene todos los detalles de ventas. */
    public List<DetalleVentaDTO> obtenerDetallesVentas() throws Exception {
        return detalleVentaDAO.obtenerDetalles();
    }

    /** Obtiene todas las ventas registradas. */
    public List<VentaResumenDTO> obtenerTodasLasVentas() throws Exception {
        return ventaDAO.obtenerVentas();
    }
}