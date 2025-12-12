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

    public List<VentaResumenDTO> obtenerVentas() throws Exception {
        // ... [Lógica de obtenerVentas() sin cambios] ...
        List<VentaResumenDTO> ventas = new ArrayList<>();

        String sql = "SELECT * FROM ventas";

        try (ConexionBD bd = new ConexionBD();
             Connection conn = bd.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                VentaResumenDTO dto = new VentaResumenDTO();
                String usuario = extraerCadena(rs, "usuario", "vendedor", "creado_por", "user");
                if (usuario == null) {
                    Integer id = extraerEntero(rs, "usuario_id");
                    usuario = id != null ? "ID " + id : null;
                }

                dto.setUsuario(usuario);
                dto.setMontoTotal(extraerDouble(rs, "monto_total", "total", "monto", "importe", "total_venta"));
                ventas.add(dto);
            }
        }

        return ventas;
    }

    // ... [Métodos auxiliares extraerCadena, extraerDouble, extraerEntero omitidos, ya existen] ...
    private String extraerCadena(ResultSet rs, String... posibles) { /* ... */ return null; }
    private double extraerDouble(ResultSet rs, String... posibles) { /* ... */ return 0d; }
    private Integer extraerEntero(ResultSet rs, String... posibles) { /* ... */ return null; }
}