package com.example.tecnostore.logic.servicios;

import com.example.tecnostore.logic.dao.CompraDAO;
import com.example.tecnostore.logic.dto.CompraDTO;
import com.example.tecnostore.logic.dto.ProductoDTO; // Necesario para la firma de CompraDAO
import com.example.tecnostore.logic.dto.DetalleCompraDTO; // Necesario para obtener los datos
import com.example.tecnostore.logic.utils.Sesion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompraService {

    private static final Logger LOGGER = LogManager.getLogger(CompraService.class);
    private CompraDAO compraDAO;

    public CompraService() throws Exception {
        this.compraDAO = new CompraDAO();
    }

    // --- LÓGICA DE BORRADOR (Se omite si la BD no lo soporta) ---
    // NOTA: Como la tabla de BD no tiene el campo 'estado', la función de borrador NO SE PUEDE IMPLEMENTAR
    public void guardarBorrador(CompraDTO compra) throws Exception {
        // Se mantiene vacío. No se puede guardar como borrador porque la BD no tiene campo estado.
        throw new UnsupportedOperationException("No se puede guardar como borrador. La tabla 'compras' no tiene columna de estado.");
    }
    // -----------------------------------------------------------

    /**
     * Finaliza el registro de la compra usando el único método disponible en CompraDAO,
     * manejando la transacción y la actualización de inventario.
     */
    public void registrarCompra(CompraDTO compra) throws Exception {
        Connection conn = null;

        if (Sesion.getUsuarioSesion() == null) {
            throw new Exception("Usuario de sesión no encontrado.");
        }

        try {
            // 1. Obtener la conexión para la transacción
            // Asumimos que compraDAO.getConnection() viene de ConexionBD.getConnection()
            conn = compraDAO.getConnection();
            conn.setAutoCommit(false); // Iniciar Transacción

            // 2. Mapear los detalles para la firma de CompraDAO.registrarCompraCompleta
            List<ProductoDTO> productosParaDAO = compra.getItemsComprados().stream()
                    .map(detalle -> {
                        // Mapeo: ID, Cantidad (Stock), Precio
                        ProductoDTO p = new ProductoDTO();
                        p.setId(detalle.getId());
                        p.setStock(detalle.getCantidad());
                        p.setPrecio(detalle.getPrecio_compra());
                        return p;
                    })
                    .collect(Collectors.toList());

            // 3. Registrar Compra y Detalles (En la misma transacción)
            compraDAO.registrarCompraCompleta(
                    conn,
                    compra.getProveedor(),
                    compra.getUsuario_id(),
                    compra.getSucursal_id(),
                    compra.getTotal(),
                    productosParaDAO
            );

            // 4. Afectar Inventario (Lógica Crítica Faltante)
            // Se debe implementar el aumento de stock aquí, pero el método NO EXISTE.
            // ServicioProductos servicioProductos = new ServicioProductos();
            // servicioProductos.actualizarInventarioPorCompra(compra.getItemsComprados());

            LOGGER.warn("El inventario no fue actualizado. Implementar ServicioProductos.actualizarInventarioPorCompra() para la entrega final.");

            conn.commit(); // Commit Transacción
            LOGGER.info("Compra finalizada y registrada con éxito.");

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Rollback en caso de error
            LOGGER.error("Fallo al registrar la compra. Rollback ejecutado: {}", e.getMessage(), e);
            throw new Exception("Fallo en la transacción de registro de compra: " + e.getMessage());
        } finally {
            if (conn != null) {
                // Se usa el cierre estándar de JDBC, asumiendo que getConnection()
                // devuelve una conexión manejable.
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}