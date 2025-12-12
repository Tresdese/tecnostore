package com.example.tecnostore.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.VentaResumenDTO;

public class VentaDAO {

    public void insertarVenta(Connection conn, String usuario, double monto) throws SQLException {
        String sql = "INSERT INTO ventas (total, usuario_id) VALUES (?, ?)";
        Integer usuarioId = null;
        try {
            usuarioId = usuario != null ? Integer.parseInt(usuario) : null;
        } catch (NumberFormatException ignored) {
            // Si no es numérico, se inserta null y se registrará solo el total
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, monto);
            if (usuarioId != null) {
                stmt.setInt(2, usuarioId);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();
        }
    }

        /** Inserta una venta usando una nueva conexión basada en config.properties. */
        public void registrarVenta(String usuario, double monto) throws Exception {
            try (ConexionBD bd = new ConexionBD(); Connection conn = bd.getConnection()) {
                insertarVenta(conn, usuario, monto);
            }
        }

    public List<VentaResumenDTO> obtenerVentas() throws Exception {
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

    private String extraerCadena(ResultSet rs, String... posibles) {
        for (String campo : posibles) {
            try {
                rs.findColumn(campo);
                return rs.getString(campo);
            } catch (Exception ignored) {
                // probar siguiente columna candidata
            }
        }
        return null;
    }

    private double extraerDouble(ResultSet rs, String... posibles) {
        for (String campo : posibles) {
            try {
                rs.findColumn(campo);
                return rs.getDouble(campo);
            } catch (Exception ignored) {
                // probar siguiente columna candidata
            }
        }
        return 0d;
    }

    private Integer extraerEntero(ResultSet rs, String... posibles) {
        for (String campo : posibles) {
            try {
                rs.findColumn(campo);
                int valor = rs.getInt(campo);
                return rs.wasNull() ? null : valor;
            } catch (Exception ignored) {
                // probar siguiente columna candidata
            }
        }
        return null;
    }
}