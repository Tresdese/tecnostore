package com.example.tecnostore.logic.dao;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.DetalleVentaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DetalleVentaDAO extends ConexionBD {

    private static final String SQL_INSERT_DETALLE =
            "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_venta) VALUES (?, ?, ?, ?)";

    public DetalleVentaDAO() throws Exception {
        super();
    }

    /**
     * Registra todos los detalles de una venta en la base de datos.
     * Debe ejecutarse dentro de la misma transacción que la venta principal.
     */
    public void registrarDetalles(Connection conn, int ventaId, List<DetalleVentaDTO> detalles) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_DETALLE)) {
            for (DetalleVentaDTO detalle : detalles) {
                ps.setInt(1, ventaId);
                ps.setInt(2, detalle.getProductoId());
                ps.setInt(3, detalle.getCantidad());
                ps.setDouble(4, detalle.getPrecioVenta());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}