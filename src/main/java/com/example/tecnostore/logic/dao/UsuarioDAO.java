package com.example.tecnostore.logic.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.UsuarioDTO;

public class UsuarioDAO extends ConexionBD {

    private static final Logger LOGGER = LogManager.getLogger(UsuarioDAO.class);

    // SECCIÓN CRÍTICA: manejo de datos sensibles (hash de contraseña)
    // - No loguear el hash ni la contraseña en ningún momento.
    // - Asegurarse de que el formato del hash (hex o base64) sea consistente entre la BD y la app.
    private final static String SQL_INSERT = "INSERT INTO usuarios(nombre, usuario, contraseña_hash, rol_id, activo) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE usuarios SET nombre=?, usuario=?, contraseña_hash=?, rol_id=?, activo=? WHERE id=?";
    private final static String SQL_DELETE = "DELETE FROM usuarios WHERE id=?";
    private final static String SQL_SELECT_BY_ID = "SELECT * FROM usuarios WHERE id=?";
    private final static String SQL_SELECT_ID = "SELECT id, usuario FROM usuarios WHERE id=?";
    private final static String SQL_SELECT_BY_USERNAME = "SELECT * FROM usuarios WHERE usuario=?";
    private final static String SQL_SELECT_BY_USERNAME_AND_PASSWORD = "SELECT * FROM usuarios WHERE usuario=? AND contraseña_hash=?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM usuarios";

    public UsuarioDAO() throws Exception {
        super();
    }

    public void agregar(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getUsuario());
            ps.setString(3, dto.getContrasenaHash());
            ps.setInt(4, dto.getRol_id());
            ps.setBoolean(5, dto.isActivo()); 
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    dto.setId(generatedKeys.getInt(1));
                    LOGGER.info("Usuario agregado con ID: {}", dto.getId());
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para el usuario.");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al agregar usuario: {}", e.getMessage(), e);
            throw new Exception("Error al agregar usuario: " + e.getMessage());
        }
    }

    public void actualizar(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_UPDATE)) {
            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getUsuario());
            ps.setString(3, dto.getContrasenaHash());
            ps.setInt(4, dto.getRol_id());
            ps.setBoolean(5, dto.isActivo());
            ps.setInt(6, dto.getId());
            ps.executeUpdate();
            LOGGER.info("Usuario actualizado con ID: {}", dto.getId());
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar usuario: {}", e.getMessage(), e);
            throw new Exception("Error al actualizar usuario: " + e.getMessage());
        }
    }

    public void eliminar(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_DELETE)) {
            ps.setInt(1, dto.getId());
            ps.executeUpdate();
            LOGGER.info("Usuario eliminado con ID: {}", dto.getId());
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar usuario: {}", e.getMessage(), e);
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        }
    }

    public UsuarioDTO buscarPorId(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_BY_ID)) {
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
                    LOGGER.info("Usuario encontrado con ID: {}", dto.getId());
                    return dto;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar usuario: {}", e.getMessage(), e);
            throw new Exception("Error al buscar usuario por ID: " + e.getMessage());
        }
    }

    public boolean buscarIdRepetido(UsuarioDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, dto.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                LOGGER.info("Buscando ID repetido para el usuario con ID: {}", dto.getId());
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar usuario: {}", e.getMessage(), e);
            throw new Exception("Error al buscar ID: " + e.getMessage());
        }
    }

    public UsuarioDTO buscarPorUsername(String username) throws Exception {
        UsuarioDTO usuario = null;
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_BY_USERNAME)) {
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new UsuarioDTO();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setUsuario(rs.getString("usuario"));
                    // aquí se lee el hash almacenado en la base de datos
                    // IMPORTANTE: no imprimir ni exponer este valor en logs
                    usuario.setContrasenaHash(rs.getString("contraseña_hash"));
                    usuario.setRol_id(rs.getInt("rol_id"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    
                    Timestamp fechaCreacionSql = rs.getTimestamp("fecha_creacion");
                    usuario.setFechaCreacion(fechaCreacionSql != null ? fechaCreacionSql.toLocalDateTime() : null);
                    LOGGER.info("Usuario encontrado con nombre de usuario: {}", username);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar usuario: {}", e.getMessage(), e);
            throw new Exception("Error al buscar usuario por nombre de usuario: " + e.getMessage());
        }
        return usuario;
    }

    public UsuarioDTO buscarPorUsernameYContrasena(String username, String contrasenaHash) throws Exception {
        UsuarioDTO usuario = null;
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_BY_USERNAME_AND_PASSWORD))
        {
            ps.setString(1, username);
            ps.setString(2, contrasenaHash);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new UsuarioDTO();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setUsuario(rs.getString("usuario"));
                    // leer hash desde la base de datos (verificar formato antes de comparar)
                    usuario.setContrasenaHash(rs.getString("contraseña_hash"));
                    usuario.setRol_id(rs.getInt("rol_id"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    
                    Timestamp fechaCreacionSql = rs.getTimestamp("fecha_creacion");
                    usuario.setFechaCreacion(fechaCreacionSql != null ? fechaCreacionSql.toLocalDateTime() : null);
                    LOGGER.info("Usuario encontrado con nombre de usuario: {}", username);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar usuario: {}", e.getMessage(), e);
            throw new Exception("Error al buscar usuario por nombre de usuario y contraseña: " + e.getMessage());
        }
        return usuario;
    }

    public boolean cambiarStatusActivo(UsuarioDTO dto, boolean nuevoEstado) throws Exception {
        String sql = "UPDATE usuarios SET activo=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, dto.getId());
            int filasAfectadas = ps.executeUpdate();
            LOGGER.info("Se cambió el estado activo del usuario con ID {} a {}", dto.getId(), nuevoEstado);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar usuario: {}", e.getMessage(), e);
            throw new Exception("Error al cambiar el estado activo del usuario: " + e.getMessage());
        }
    }

    public List<UsuarioDTO> buscarTodos() throws Exception {
        List<UsuarioDTO> usuarios = new java.util.ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setUsuario(rs.getString("usuario"));
                usuario.setContrasenaHash(rs.getString("contraseña_hash"));
                usuario.setRol_id(rs.getInt("rol_id"));
                usuario.setActivo(rs.getBoolean("activo"));
                Timestamp fechaCreacionSql = rs.getTimestamp("fecha_creacion");
                usuario.setFechaCreacion(fechaCreacionSql != null ? fechaCreacionSql.toLocalDateTime() : null);
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            LOGGER.error("Error al obtener todos los usuarios: {}", e.getMessage(), e);
            throw new Exception("Error al obtener todos los usuarios: " + e.getMessage());
        }
        return usuarios;
    }
}
