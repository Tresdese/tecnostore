package com.example.tecnostore.dao;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import com.example.tecnostore.dto.UsuarioDTO;

public class UsuarioDAO extends ConexionBD {
    private final static String SQL_INSERT = "INSERT INTO usuarios(nombre, usuario, contraseña_hash, rol_id, activo) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE usuarios SET nombre=?, usuario=?, contraseña_hash=?, rol_id=?, activo=? WHERE id=?";
    private final static String SQL_DELETE = "DELETE FROM usuarios WHERE id=?";
    private final static String SQL_SELECT = "SELECT id, nombre, usuario, contraseña_hash, rol_id, activo, fecha_creacion FROM usuarios WHERE id=?";
    private final static String SQL_SELECT_BY_ID = "SELECT id, nombre, usuario, contraseña_hash, rol_id, activo, fecha_creacion FROM usuarios WHERE id=?";
    private final static String SQL_SELECT_BY_USERNAME = "SELECT id, nombre, usuario, contraseña_hash, rol_id, activo, fecha_creacion FROM usuarios WHERE usuario=?";
    private final static String SQL_SELECTALL = "SELECT id, nombre, usuario, contraseña_hash, rol_id, activo, fecha_creacion FROM usuarios";

    public UsuarioDAO() throws Exception {
        super();
    }

    public void agregar(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getUsuario());
            ps.setString(3, dto.getContrasenaHash());
            ps.setInt(4, dto.getRol_id());
            ps.setBoolean(5, dto.isActivo()); 
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    dto.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al agregar usuario: " + e.getMessage());
        }
    }

    public void actualizar(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getUsuario());
            ps.setString(3, dto.getContrasenaHash());
            ps.setInt(4, dto.getRol_id());
            ps.setBoolean(5, dto.isActivo());
            ps.setInt(6, dto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar usuario: " + e.getMessage());
        }
    }

    public void eliminar(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, dto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        }
    }

    public UsuarioDTO buscar(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, dto.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto.setId(rs.getInt("id"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setUsuario(rs.getString("usuario"));
                    dto.setContrasenaHash(rs.getString("contraseña_hash"));
                    dto.setRol_id(rs.getInt("rol_id"));
                    dto.setActivo(rs.getBoolean("activo"));
                    
                    Timestamp fechaCreacionSql = rs.getTimestamp("fecha_creacion");
                    dto.setFechaCreacion(fechaCreacionSql != null ? fechaCreacionSql.toLocalDateTime() : null);
                    return dto;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar usuario por ID: " + e.getMessage());
        }
    }

    public UsuarioDTO buscarPorUsername(String username) throws Exception {
        UsuarioDTO usuario = null;
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_USERNAME)) {
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new UsuarioDTO();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setUsuario(rs.getString("usuario"));
                    usuario.setContrasenaHash(rs.getString("contraseña_hash"));
                    usuario.setRol_id(rs.getInt("rol_id"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    
                    Timestamp fechaCreacionSql = rs.getTimestamp("fecha_creacion");
                    usuario.setFechaCreacion(fechaCreacionSql != null ? fechaCreacionSql.toLocalDateTime() : null);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar usuario por nombre de usuario: " + e.getMessage());
        }
        return usuario;
    }
}
