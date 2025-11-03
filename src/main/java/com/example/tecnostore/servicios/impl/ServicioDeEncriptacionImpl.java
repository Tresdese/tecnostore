package com.example.tecnostore.servicios.impl;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.tecnostore.dao.UsuarioDAO;
import com.example.tecnostore.servicios.ServicioDeEncriptacion;

public class ServicioDeEncriptacionImpl implements ServicioDeEncriptacion {    
    @Override
    public String encriptarContraseña(String contrasena) {
        return BCrypt.hashpw(contrasena, BCrypt.gensalt());
    }

    @Override
    public boolean verificarContraseña(String contrasenaOriginal, String contrasenaHash) {
        return BCrypt.checkpw(contrasenaOriginal, contrasenaHash);
    }

}
