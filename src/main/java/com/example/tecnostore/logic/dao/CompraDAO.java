package com.example.tecnostore.logic.dao;

import java.sql.*;
import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.ProductoDTO;
import java.util.List;

public class CompraDAO extends ConexionBD {

    public CompraDAO() throws Exception { super(); }

    public void registrarCompraCompleta(Connection conn, String proveedor, int usuarioId, int sucursalId, double total, List<ProductoDTO> productos) throws SQLException {
        String sqlCompra = "INSERT INTO compras (proveedor, usuario_id, sucursal_id, total, fecha) VALUES (?, ?, ?, ?, NOW())";
        String sqlDetalle = "INSERT INTO compras_detalle (compra_id, producto_id, cantidad, precio_compra) VALUES (?, ?, ?, ?)";

        int compraId = -1;
        try (PreparedStatement ps = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, proveedor);
            ps.setInt(2, usuarioId);
            ps.setInt(3, sucursalId);
            ps.setDouble(4, total);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) compraId = rs.getInt(1);
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
            for (ProductoDTO p : productos) {
                ps.setInt(1, compraId);
                ps.setInt(2, p.getId());
                ps.setInt(3, p.getStock()); // Cantidad comprada
                ps.setDouble(4, p.getPrecio()); // Precio de costo
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
