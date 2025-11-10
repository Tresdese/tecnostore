package com.example.tecnostore.logic.servicios;

import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.dao.ProductoDAO;
import java.util.List;

public class ServicioProductos {

    private final ProductoDAO productoDAO;

    public ServicioProductos() throws Exception {
        this.productoDAO = new ProductoDAO();
    }

    public boolean productoYaExiste(ProductoDTO producto) throws Exception {
        boolean existe = productoDAO.obtenerPorNombre(producto.getNombre());
        return existe;
    }

    public boolean insertarProducto(ProductoDTO producto) throws Exception {
        if (producto == null) {
            throw new Exception("El producto no puede ser nulo.");
        } else if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            throw new Exception("El nombre del producto no puede estar vacío.");
        } else if (productoYaExiste(producto)) {
            throw new Exception("El producto ya existe.");
        }
        
        productoDAO.agregar(producto);
        return true;
    }

    public boolean actualizarProducto(ProductoDTO producto) throws Exception {
        if (producto == null) {
            throw new Exception("El producto no puede ser nulo.");
        } else if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            throw new Exception("El nombre del producto no puede estar vacío.");
        }
        
        productoDAO.actualizar(producto);
        return true;
    }

    public boolean eliminarProducto(ProductoDTO producto) throws Exception {
        if (producto == null) {
            throw new Exception("El producto no puede ser nulo.");
        }
        
        productoDAO.eliminarLogico(producto);
        return true;
    }

    public List<ProductoDTO> obtenerTodosProductos() throws Exception {
        return productoDAO.obtenerTodos();
    }



}
