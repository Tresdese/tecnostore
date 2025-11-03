package com.example.tecnostore.logic.dto;

import java.time.LocalDateTime;

public class UsuarioDTO {
    private int id;
    private String nombre;
    private String usuario;
    private String contrasenaHash;
    private int rol_id;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    public UsuarioDTO(String nombre, String usuario, String contrasenaHash, int rol_id, boolean activo) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenaHash = contrasenaHash;
        this.rol_id = rol_id;
        this.activo = activo;
    }

    public UsuarioDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public int getRol_id() {
        return rol_id;
    }

    public void setRol_id(int rol_id) {
        this.rol_id = rol_id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    



}
