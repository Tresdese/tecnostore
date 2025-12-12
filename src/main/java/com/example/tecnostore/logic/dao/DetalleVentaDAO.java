package com.example.tecnostore.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.DetalleVentaDTO;

public class DetalleVentaDAO extends ConexionBD {

        private static final String SQL_INSERT_DETALLE =
            "INSERT INTO ventas_detalle (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        private static final String SQL_SELECT_DETALLES =
            "SELECT vd.id, vd.venta_id, vd.producto_id, vd.cantidad, vd.precio_unitario, vd.subtotal, p.nombre AS nombre_producto " +
                "FROM ventas_detalle vd " +
                "LEFT JOIN productos p ON vd.producto_id = p.id";

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
                ps.setDouble(5, detalle.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Obtiene todos los detalles de venta con nombre de producto.
     */
    public List<DetalleVentaDTO> obtenerDetalles() throws Exception {
        List<DetalleVentaDTO> detalles = new ArrayList<>();
        try (ConexionBD conexionBD = new ConexionBD(); Connection conn = conexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_DETALLES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DetalleVentaDTO dto = new DetalleVentaDTO();
                dto.setId(rs.getInt("id"));
                dto.setVentaId(rs.getInt("venta_id"));
                dto.setProductoId(rs.getInt("producto_id"));
                dto.setCantidad(rs.getInt("cantidad"));
                dto.setPrecioVenta(rs.getDouble("precio_unitario"));
                dto.setSubtotal(rs.getDouble("subtotal"));
                dto.setNombreProducto(rs.getString("nombre_producto"));
                detalles.add(dto);
            }
        }
        return detalles;
    }
}