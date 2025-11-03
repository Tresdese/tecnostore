package com.example.tecnostore.servicios;

public interface ServicioDeEncriptacion {

    String encriptarContraseña(String contrasena);
    boolean verificarContraseña(String contrasenaOriginal, String contrasenaHash);

}
