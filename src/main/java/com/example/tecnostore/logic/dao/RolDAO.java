package com.example.tecnostore.logic.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.RolDTO;

public class RolDAO extends ConexionBD {
    private final static String SQL_SELECT_BY_ID = "SELECT id, nombre, descripcion FROM roles WHERE id=?";
    private final static String SQL_SELECT_ALL = "SELECT id, nombre, descripcion FROM roles";
    
    public RolDAO() throws Exception {
        super();
    }

    public List<RolDTO> buscarTodos() throws Exception {
        List<RolDTO> roles = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RolDTO rol = new RolDTO(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                );
                roles.add(rol);
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar todos los roles: " + e.getMessage());
        }
        return roles;
    }

    public RolDTO buscarPorId(int id)  throws Exception {
        RolDTO rol = null;
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rol = new RolDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar rol por ID: " + e.getMessage());
        }
        return rol;
    }

}
