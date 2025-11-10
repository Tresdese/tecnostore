package com.example.tecnostore.logic.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.tecnostore.data_access.ConexionBD;
import com.example.tecnostore.logic.dto.ProductoDTO;

public class ProductoDAO extends ConexionBD {

    private final static String SQL_INSERT = "INSERT INTO productos(nombre, descripcion, precio, stock, sucursal_id, activo, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE productos SET nombre=?, descripcion=?, precio=?, stock=?, sucursal_id=?, activo=? WHERE id=?";
    private final static String SQL_UPDATE_STOCK = "UPDATE productos SET stock=? WHERE id=?";
    private final static String SQL_DELETE = "DELETE FROM productos WHERE id=?";
    private final static String SQL_LOGIC_DELETE = "UPDATE productos SET activo=0 WHERE id=?";
    private final static String SQL_SELECT_BY_ID = "SELECT * FROM productos WHERE id=?";
    private final static String SQL_SELECT_BY_NAME = "SELECT * FROM productos WHERE nombre=?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM productos";
    
    public ProductoDAO() throws Exception {
        super();
    }

    public void agregar(ProductoDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getDescripcion());
            ps.setDouble(3, dto.getPrecio());
            ps.setInt(4, dto.getStock());
            ps.setInt(5, dto.getSucursal_id());
            ps.setBoolean(6, dto.isActivo()); 
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    dto.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al agregar producto: " + e.getMessage());
        }
    }

    public void actualizar(ProductoDTO dto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_UPDATE)) {
            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getDescripcion());
            ps.setDouble(3, dto.getPrecio());
            ps.setInt(4, dto.getStock());
            ps.setInt(5, dto.getSucursal_id());
            ps.setBoolean(6, dto.isActivo());
            ps.setInt(7, dto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar producto: " + e.getMessage());
        }
    }

    public void actualizarStock(int productoId, int nuevoStock) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_UPDATE_STOCK)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, productoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar stock del producto: " + e.getMessage());
        }
    }

    public void eliminar(ProductoDTO producto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_DELETE)) {
            ps.setInt(1, producto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al eliminar producto: " + e.getMessage());
        }
    }

    public boolean eliminarLogico(ProductoDTO producto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_LOGIC_DELETE)) {
            ps.setInt(1, producto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al eliminar lógicamente el producto: " + e.getMessage());
        }
        return true;
    }

    public ProductoDTO buscarPorId(ProductoDTO producto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, producto.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto.setId(rs.getInt("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setStock(rs.getInt("stock"));
                    producto.setSucursal_id(rs.getInt("sucursal_id"));
                    producto.setActivo(rs.getBoolean("activo"));
                    producto.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar producto por ID: " + e.getMessage());
        }
        return producto;
    }

    public boolean obtenerPorNombre(String nombreProducto) throws Exception {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_BY_NAME)) {
            ps.setString(1, nombreProducto);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar nombre: " + e.getMessage());
        }
    }

    public List<ProductoDTO> obtenerTodos() throws Exception {
        List<ProductoDTO> productos = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductoDTO dto = new ProductoDTO();
                dto.setId(rs.getInt("id"));
                dto.setNombre(rs.getString("nombre"));
                dto.setDescripcion(rs.getString("descripcion"));
                dto.setPrecio(rs.getDouble("precio"));
                dto.setStock(rs.getInt("stock"));
                dto.setSucursal_id(rs.getInt("sucursal_id"));
                dto.setActivo(rs.getBoolean("activo"));
                dto.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                productos.add(dto);
            }

        } catch (SQLException e) {
            throw new Exception("Error al obtener productos: " + e.getMessage());
        }

        return productos;
    }

    
    
}
