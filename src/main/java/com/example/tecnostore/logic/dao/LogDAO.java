package com.example.tecnostore.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.tecnostore.data_access.ConexionBD;

public class LogDAO extends ConexionBD {

    private static final String SQL_SELECT_LOGS = "SELECT * FROM logs_auditoria";
    private static final String SQL_INSERT_LOG = "INSERT INTO logs_auditoria (usuario, accion, resultado, detalles) VALUES (?, ?, ?, ?)";

    public LogDAO() throws Exception {
        super();
    }

    

    public boolean registrarLog(Connection conn, String usuario, String accion, String resultado, String detalles) throws SQLException {
        boolean respuesta = false;
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_LOG)) {
            stmt.setString(1, usuario);
            stmt.setString(2, accion);
            stmt.setString(3, resultado);
            stmt.setString(4, detalles);
            stmt.executeUpdate();
            respuesta = true;
        }
        return respuesta;
    }
}