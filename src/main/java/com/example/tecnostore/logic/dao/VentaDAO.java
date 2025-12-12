package com.example.tecnostore.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Necesario para obtener ID generado
import java.util.ArrayList;
import java.util.List;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.VentaResumenDTO;

public class VentaDAO {

    private final static String SQL_INSERT = "INSERT INTO ventas (total, usuario_id) VALUES (?, ?)";

    /**
     * Inserta la cabecera de la venta. Modificado para DEVOLVER EL ID de la venta.
     */
    public int insertarVenta(Connection conn, String usuario, double monto) throws SQLException {
        Integer usuarioId = null;
        int ventaId = -1;
        try {
            usuarioId = usuario != null ? Integer.parseInt(usuario) : null;
        } catch (NumberFormatException ignored) {
            // Ignorar
        }

        // Usamos Statement.RETURN_GENERATED_KEYS para obtener el ID
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, monto);
            if (usuarioId != null) {
                stmt.setInt(2, usuarioId);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();

            // Obtener el ID generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ventaId = rs.getInt(1);
                }
            }
        }
        if (ventaId == -1) {
            throw new SQLException("Error al obtener el ID generado de la venta.");
        }
        return ventaId;
    }

        /** Inserta una venta usando una nueva conexión basada en config.properties. */
        public void registrarVenta(String usuario, double monto) throws Exception {
            try (ConexionBD bd = new ConexionBD(); Connection conn = bd.getConnection()) {
                insertarVenta(conn, usuario, monto);
            }
        }

    public List<VentaResumenDTO> obtenerVentas() throws Exception {
        List<VentaResumenDTO> ventas = new ArrayList<>();

        String sql = """
            SELECT v.total       AS total,
                   v.usuario_id  AS usuario_id,
                   COALESCE(u.usuario, u.nombre) AS usuario
              FROM ventas v
              LEFT JOIN usuarios u ON v.usuario_id = u.id
            """;

        try (ConexionBD bd = new ConexionBD();
             Connection conn = bd.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                VentaResumenDTO dto = new VentaResumenDTO();
                String usuario = extraerCadena(rs, "usuario");
                if (usuario == null || usuario.isBlank()) {
                    Integer id = extraerEntero(rs, "usuario_id");
                    usuario = id != null ? "ID " + id : "";
                }

                dto.setUsuario(usuario);
                dto.setMontoTotal(extraerDouble(rs, "total", "monto_total", "monto", "importe", "total_venta"));
                ventas.add(dto);
            }
        }

        return ventas;
    }

    private String extraerCadena(ResultSet rs, String... posibles) {
        for (String col : posibles) {
            try {
                String val = rs.getString(col);
                if (val != null) {
                    return val;
                }
            } catch (SQLException ignored) {
                // Continúa con el siguiente nombre de columna si no existe
            }
        }
        return null;
    }

    private double extraerDouble(ResultSet rs, String... posibles) {
        for (String col : posibles) {
            try {
                double val = rs.getDouble(col);
                if (!rs.wasNull()) {
                    return val;
                }
            } catch (SQLException ignored) {
                // Continúa con el siguiente nombre de columna si no existe
            }
        }
        return 0d;
    }

    private Integer extraerEntero(ResultSet rs, String... posibles) {
        for (String col : posibles) {
            try {
                int val = rs.getInt(col);
                if (!rs.wasNull()) {
                    return val;
                }
            } catch (SQLException ignored) {
                // Continúa con el siguiente nombre de columna si no existe
            }
        }
        return null;
    }
}