package com.example.tecnostore.logic.dao;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.SucursalDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SucursalDAO extends ConexionBD {

    private static final String SQL_INSERT = "INSERT INTO sucursales (nombre, direccion, telefono, activo) VALUES (?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT id, nombre, direccion, telefono, activo FROM sucursales";
    private static final String SQL_SELECT_BY_ID = "SELECT id, nombre, direccion, telefono, activo FROM sucursales WHERE id = ?";
    private static final String SQL_UPDATE = "UPDATE sucursales SET nombre = ?, direccion = ?, telefono = ?, activo = ? WHERE id = ?";
    private static final String SQL_DELETE_LOGIC = "UPDATE sucursales SET activo = 0 WHERE id = ?";

    public SucursalDAO() throws Exception {
        super();
    }


    // Convierte una fila de ResultSet a un objeto SucursalDTO
    private SucursalDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        SucursalDTO sucursal = new SucursalDTO();
        sucursal.setId(rs.getInt("id"));
        sucursal.setNombre(rs.getString("nombre"));
        sucursal.setDireccion(rs.getString("direccion"));
        sucursal.setTelefono(rs.getString("telefono"));
        sucursal.setActivo(rs.getBoolean("activo"));
        return sucursal;
    }

    //Inserta una nueva sucursal.

    public int insertar(SucursalDTO sucursal) throws SQLException {
        Connection conn = null;
        int rows = 0;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
                stmt.setString(1, sucursal.getNombre());
                stmt.setString(2, sucursal.getDireccion());
                stmt.setString(3, sucursal.getTelefono());
                stmt.setBoolean(4, sucursal.isActivo());
                rows = stmt.executeUpdate();
            }
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
        return rows;
    }

    // Recupera todas las sucursales.
    public List<SucursalDTO> seleccionarTodos() throws SQLException {
        Connection conn = null;
        List<SucursalDTO> sucursales = new ArrayList<>();
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sucursales.add(mapResultSetToDTO(rs));
                }
            }
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
        return sucursales;
    }

    //Actualiza los datos de una sucursal existente.
    public int actualizar(SucursalDTO sucursal) throws SQLException {
        Connection conn = null;
        int rows = 0;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
                stmt.setString(1, sucursal.getNombre());
                stmt.setString(2, sucursal.getDireccion());
                stmt.setString(3, sucursal.getTelefono());
                stmt.setBoolean(4, sucursal.isActivo());
                stmt.setInt(5, sucursal.getId());
                rows = stmt.executeUpdate();
            }
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
        return rows;
    }

    // Aplica la baja lógica a una sucursal.
    public int darBajaLogica(SucursalDTO sucursal) throws SQLException {
        Connection conn = null;
        int rows = 0;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_LOGIC)) {
                stmt.setInt(1, sucursal.getId());
                rows = stmt.executeUpdate();
            }
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
        return rows;
    }
}