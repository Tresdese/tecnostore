package com.example.tecnostore.logic.servicios;

import java.util.List;
import java.util.ArrayList;

import com.example.tecnostore.logic.dao.RolDAO;
import com.example.tecnostore.logic.dto.RolDTO;

public class ServicioRoles {
    private final RolDAO rolDAO;

    public ServicioRoles() throws Exception {
        this.rolDAO = new RolDAO();
    }

    public List<String> obtenerNombresRoles() throws Exception {
        List<String> nombresRoles = new ArrayList<>();
        List<RolDTO> roles = rolDAO.buscarTodos();

        for (RolDTO rol : roles) {
            nombresRoles.add(rol.getNombre());
        }

        return nombresRoles;
    }

    public int obtenerIdPorNombre(String nombreRol) throws Exception {
        List<RolDTO> roles = rolDAO.buscarTodos();

        for (RolDTO rol : roles) {
            if (rol.getNombre().equalsIgnoreCase(nombreRol)) {
                return rol.getId();
            }
        }

        throw new Exception("Rol no encontrado: " + nombreRol);
    }

}
