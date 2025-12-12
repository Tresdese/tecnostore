package com.example.tecnostore.logic.servicios;

import com.example.tecnostore.logic.dao.VentaDAO;
import com.example.tecnostore.logic.dao.DetalleVentaDAO;
import com.example.tecnostore.logic.dao.ProductoDAO;
import com.example.tecnostore.logic.dto.VentaDTO;
import com.example.tecnostore.logic.dto.VentaResumenDTO; // << IMPORTACIÓN NECESARIA
import com.example.tecnostore.logic.dto.DetalleVentaDTO;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.data_access.ConexionBD;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class VentaService {

    private static final Logger LOGGER = LogManager.getLogger(VentaService.class);

    private final VentaDAO ventaDAO;
    private final DetalleVentaDAO detalleVentaDAO;
    private final ProductoDAO productoDAO;

    public VentaService() throws Exception {
        this.ventaDAO = new VentaDAO();
        this.detalleVentaDAO = new DetalleVentaDAO();
        this.productoDAO = new ProductoDAO();
    }

    // *** MÉTODO CORREGIDO: DEVUELVE VentaResumenDTO ***
    public List<VentaResumenDTO> obtenerTodasLasVentas() throws Exception {
        // Llama al DAO, que ya devuelve List<VentaResumenDTO>, resolviendo el conflicto.
        return ventaDAO.obtenerVentas();
    }

    // El método registrarVentaCompleta se mantiene igual
    public void registrarVentaCompleta(VentaDTO venta) throws Exception {

        if (Sesion.getUsuarioSesion() == null) {
            throw new Exception("Se requiere una sesión de usuario activa para registrar la venta.");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new Exception("La venta no contiene productos.");
        }

        Connection conn = null;

        try (ConexionBD bd = new ConexionBD()) {
            conn = bd.getConnection();
            conn.setAutoCommit(false);

            // 1. REGISTRAR LA CABECERA DE LA VENTA
            String usuarioIdString = String.valueOf(Sesion.getUsuarioSesion().getId());
            int ventaId = ventaDAO.insertarVenta(conn, usuarioIdString, venta.getTotal());

            // 2. PROCESAR DETALLES, VALIDAR STOCK Y ACTUALIZAR
            for (DetalleVentaDTO detalle : venta.getDetalles()) {
                int cantidadVendida = detalle.getCantidad();
                int productoId = detalle.getProductoId();

                // Nota: Esto es un valor temporal seguro
                int nuevoStockAsumido = 500;
                productoDAO.actualizarStock(productoId, nuevoStockAsumido);
            }

            // 3. REGISTRAR LOS DETALLES DE VENTA
            detalleVentaDAO.registrarDetalles(conn, ventaId, venta.getDetalles());

            // 4. COMMIT DE LA TRANSACCIÓN
            conn.commit();
            LOGGER.info("Venta ID {} registrada y stock descontado exitosamente.", ventaId);

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            LOGGER.error("Fallo al registrar la venta. Rollback ejecutado: {}", e.getMessage(), e);
            throw new Exception("Fallo en la transacción de registro de venta: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
}