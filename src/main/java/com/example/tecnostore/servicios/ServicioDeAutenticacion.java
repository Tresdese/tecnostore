package com.example.tecnostore.servicios;

import com.example.tecnostore.dao.UsuarioDAO;
import com.example.tecnostore.dto.UsuarioDTO;
import com.example.tecnostore.servicios.impl.ServicioDeEncriptacionImpl;

public class ServicioDeAutenticacion {
    private final UsuarioDAO usuarioDAO;
    private final ServicioDeEncriptacion encriptacionService;

    public ServicioDeAutenticacion() throws Exception{
        this.usuarioDAO = new UsuarioDAO();
        this.encriptacionService = new ServicioDeEncriptacionImpl();
    }

    public void guardarUsuario(UsuarioDTO nuevoUusario, String contrasenaOriginal) throws Exception{
        String hash = encriptacionService.encriptarContraseña(contrasenaOriginal);
        nuevoUusario.setContrasenaHash(hash);
        
        if(nuevoUusario.getRol_id() == 0){
            nuevoUusario.setRol_id(2);
        }
        nuevoUusario.setActivo(true);
        usuarioDAO.agregar(nuevoUusario);
    }
}
