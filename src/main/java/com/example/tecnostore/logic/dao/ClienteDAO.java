package com.example.tecnostore.logic.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.App;
import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.ClienteDTO;

public class ClienteDAO extends ConexionBD {

    private static final Logger LOGGER = LogManager.getLogger(ClienteDAO.class);

    private static final String SQL_INSERT = "INSERT INTO clientes (nombre, email, telefono, activo) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE clientes SET nombre = ?, email = ?, telefono = ?, activo = ? WHERE id = ?";
    private static final String SQL_DELETE_LOGIC = "UPDATE clientes SET activo = false WHERE id = ?";
    private static final String SQL_SELECT_BY_CRITERIO = "SELECT * FROM clientes WHERE (nombre LIKE ? OR email LIKE ?) AND activo = true";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM clientes WHERE email = ?";

    public ClienteDAO() throws Exception {
        super();
    }

    public boolean registrarCliente(ClienteDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getEmail());
            ps.setString(3, dto.getTelefono());
            ps.setBoolean(4, dto.isActivo());

            int filas = ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    dto.setId(keys.getInt(1));
                }
            }

            return filas > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al registrar cliente: {}", e.getMessage(), e);
            throw new Exception("Error al registrar cliente: " + e.getMessage(), e);
        }
    }

    public boolean actualizarCliente(ClienteDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_UPDATE)) {
            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getEmail());
            ps.setString(3, dto.getTelefono());
            ps.setBoolean(4, dto.isActivo());
            ps.setInt(5, dto.getId());

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar cliente: {}", e.getMessage(), e);
            throw new Exception("Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    public boolean eliminarLogicoCliente(int id) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_DELETE_LOGIC)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al eliminar lógicamente el cliente: {}", e.getMessage(), e);
            throw new Exception("Error al eliminar lógicamente el cliente: " + e.getMessage(), e);
        }
    }

    public List<ClienteDTO> consultarClientes(String criterio) throws Exception {
        List<ClienteDTO> clientes = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_BY_CRITERIO)) {
            String criterioBusqueda = "%" + (criterio == null ? "" : criterio) + "%";
            ps.setString(1, criterioBusqueda);
            ps.setString(2, criterioBusqueda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al consultar clientes: {}", e.getMessage(), e);
            throw new Exception("Error al consultar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }

    public boolean emailYaExiste(String email) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_COUNT_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al verificar email: {}", e.getMessage(), e);
            throw new Exception("Error al verificar email: " + e.getMessage(), e);
        }
        return false;
    }

    private ClienteDTO mapearCliente(ResultSet rs) throws SQLException {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(rs.getInt("id"));
        dto.setNombre(rs.getString("nombre"));
        dto.setEmail(rs.getString("email"));
        dto.setTelefono(rs.getString("telefono"));
        dto.setActivo(rs.getBoolean("activo"));
        return dto;
    }
}