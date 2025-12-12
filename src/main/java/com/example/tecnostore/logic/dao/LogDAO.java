package com.example.tecnostore.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.LogAuditoriaDTO;

public class LogDAO extends ConexionBD {

        private static final String SQL_SELECT_LOGS = "SELECT l.id, l.usuario_id, u.usuario AS usuario_nombre, l.accion, l.descripcion, l.fecha "
            + "FROM logs_auditoria l LEFT JOIN usuarios u ON l.usuario_id = u.id";
    private static final String SQL_INSERT_LOG = "INSERT INTO logs_auditoria (usuario_id, accion, descripcion) VALUES (?, ?, ?)";

    public LogDAO() throws Exception {
        super();
    }

    public boolean registrarLog(Connection conn, Integer usuarioId, String accion, String descripcion) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_LOG)) {
            if (usuarioId != null) {
                stmt.setInt(1, usuarioId);
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, accion);
            stmt.setString(3, descripcion);
            stmt.executeUpdate();
            return true;
        }
    }

    // Compatibilidad con el uso previo (usuario y resultado/detalles en texto)
    public boolean registrarLog(Connection conn, String usuario, String accion, String resultado, String detalles) throws SQLException {
        String descripcion = (resultado != null ? resultado : "") + (detalles != null ? " | " + detalles : "");
        return registrarLog(conn, null, accion, descripcion);
    }

    public boolean registrar(LogAuditoriaDTO dto) throws Exception {
        try (Connection conn = getConnection()) {
            return registrarLog(conn, dto.getUsuarioId(), dto.getAccion(), dto.getDescripcion());
        }
    }

    public List<LogAuditoriaDTO> obtenerTodos() throws Exception {
        List<LogAuditoriaDTO> logs = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_LOGS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LogAuditoriaDTO dto = new LogAuditoriaDTO();
                dto.setId(rs.getInt("id"));
                int usuarioId = rs.getInt("usuario_id");
                dto.setUsuarioId(rs.wasNull() ? null : usuarioId);
                dto.setNombreUsuario(rs.getString("usuario_nombre"));
                dto.setAccion(rs.getString("accion"));
                dto.setDescripcion(rs.getString("descripcion"));
                Timestamp ts = rs.getTimestamp("fecha");
                dto.setFecha(ts != null ? ts.toLocalDateTime() : null);
                logs.add(dto);
            }
        }
        return logs;
    }
}