package com.example.tecnostore.logic.servicios;

import com.example.tecnostore.logic.dao.UsuarioDAO;
import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.utils.PasswordHasher;

public class ServicioDeAutenticacion {
    private final UsuarioDAO usuarioDAO;

    public ServicioDeAutenticacion() throws Exception{
        this.usuarioDAO = new UsuarioDAO();
    }

    private boolean isUserIdRepeated(UsuarioDTO nuevoUsuario) throws Exception {
        UsuarioDTO usuarioExistente = usuarioDAO.buscarPorId(nuevoUsuario);
        return usuarioExistente != null;
    }

    private boolean isUsernameTaken(UsuarioDTO nuevoUsuario) throws Exception {
        UsuarioDTO usuarioExistente = usuarioDAO.buscarPorUsername(nuevoUsuario.getUsuario());
        return usuarioExistente != null;
    }

    public boolean guardarUsuario(UsuarioDTO nuevoUsuario) throws Exception{
        boolean resultado;

        if (isUsernameTaken(nuevoUsuario)) {
            throw new Exception("El nombre de usuario ya está en uso.");
        }

        String contrasenaHasheada = PasswordHasher.hashPassword(nuevoUsuario.getContrasenaHash());
        nuevoUsuario.setContrasenaHash(contrasenaHasheada);
        usuarioDAO.agregar(nuevoUsuario);
        resultado = true;
        return resultado;
    }

    public boolean autenticarUsuario(String username, String contrasena) throws Exception{
        boolean resultado;
        String contrasenaHasheada = PasswordHasher.hashPassword(contrasena);
        UsuarioDTO usuario = usuarioDAO.buscarPorUsernameYContrasena(username, contrasenaHasheada);
        if (usuario == null) {
            resultado = false;
        }
        if (contrasenaHasheada == null) {
            resultado = false;
        }

        resultado = usuario.getContrasenaHash().equals(contrasenaHasheada);

        return resultado;
    }
}
