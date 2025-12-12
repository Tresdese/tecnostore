package com.example.tecnostore.logic.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class VentaService {

    private static final Logger LOGGER = LogManager.getLogger(VentaService.class);

    private final VentaDAO ventaDAO;
    private final LogDAO logDAO;
    private final ProductoDAO productoDAO;
    private final DetalleVentaDAO detalleVentaDAO;

    public VentaService() throws Exception {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        this.detalleVentaDAO = new DetalleVentaDAO();
        this.logDAO = new LogDAO();
    }

    /**
     * Procesa una venta partiendo de productos (compatibilidad con el controlador).
     * Usa la cantidad indicada en "stock" como unidades a vender.
     */
    public void procesarVenta(int usuarioId, int sucursalId, List<ProductoDTO> productos) throws Exception {
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("La venta no tiene productos.");
        }

        List<DetalleVentaDTO> detalles = new ArrayList<>();
        for (ProductoDTO producto : productos) {
            int cantidad = producto.getCantidadVenta() > 0 ? producto.getCantidadVenta() : producto.getStock();
            if (cantidad <= 0) {
                continue; // no vender si la cantidad solicitada es cero
            }
            DetalleVentaDTO detalle = new DetalleVentaDTO();
            detalle.setProductoId(producto.getId());
            detalle.setCantidad(cantidad);
            detalle.setPrecioVenta(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * cantidad);
            detalles.add(detalle);
        }

        procesarVentaConDetalles(usuarioId, detalles);
    }

    /**
     * Procesa la venta con detalles calculados, descuenta stock y persiste en ventas_detalle.
     */
    public void procesarVentaConDetalles(int usuarioId, List<DetalleVentaDTO> detalles) throws Exception {
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
                    if (enBD == null || enBD.getNombre() == null) {
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

    /** Obtiene todos los detalles de ventas (ventas_detalle). */
    public List<DetalleVentaDTO> obtenerDetallesVentas() throws Exception {
        return detalleVentaDAO.obtenerDetalles();
    }
}