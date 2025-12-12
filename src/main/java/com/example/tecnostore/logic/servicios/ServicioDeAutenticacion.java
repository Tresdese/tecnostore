package com.example.tecnostore.logic.servicios;

import com.example.tecnostore.logic.dao.UsuarioDAO;
import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.utils.PasswordHasher;

public class ServicioDeAutenticacion {
    /*
     * SECCIÓN CRÍTICA DE SEGURIDAD:
     * - Aquí se decide cómo se guardan y comparan las contraseñas.
     * - Riesgos: comparar hashes en texto plano o usar un hasher débil puede permitir accesos no autorizados.
     * - Recomendación (no técnica): usar un método probado (BCrypt/Argon2) para guardar y verificar
     *   contraseñas; nunca comparar contraseñas en texto plano ni mostrar detalles en mensajes al usuario.
     */
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

        // Aquí se transforma la contraseña del usuario antes de guardarla.
        // Nota: el hasher actual es rápido; recomendable usar BCrypt/Argon2 para mayor seguridad.
        String contrasenaHasheada = PasswordHasher.hashPassword(nuevoUsuario.getContrasenaHash());
        nuevoUsuario.setContrasenaHash(contrasenaHasheada);
        usuarioDAO.agregar(nuevoUsuario);
        resultado = true;
        return resultado;
    }

    public UsuarioDTO buscarUsuarioPorUsernameYContrasena(String username, String contrasena) throws Exception {
        String contrasenaHasheada = PasswordHasher.hashPassword(contrasena);
        return usuarioDAO.buscarPorUsernameYContrasena(username, contrasenaHasheada);
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

        // Comparación final: asegurarse de no causar NullPointerException.
        if (usuario == null || usuario.getContrasenaHash() == null) {
            return false;
        }
        resultado = usuario.getContrasenaHash().equals(contrasenaHasheada);

        return resultado;
    }

    public boolean cambiarStatusActivo(UsuarioDTO usuario, boolean nuevoEstado) throws Exception {
        // Ejecutamos la acción directamente en el DAO
        // Se asume que UsuarioDAO tiene un método con esta firma, aunque no fue proporcionado.
        return usuarioDAO.cambiarStatusActivo(usuario, nuevoEstado);
    }
}
