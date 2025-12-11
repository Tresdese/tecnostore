/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tecnostore.logic.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dao.RolDAO;
import com.example.tecnostore.logic.dto.RolDTO;
import com.example.tecnostore.logic.dto.UsuarioDTO;

public class Sesion {

    private static final Logger LOGGER = LogManager.getLogger(Sesion.class);

    private static RolDAO rolDAO;
    
    static {
        try {
            rolDAO = new RolDAO();
        } catch (Exception e) {
            LOGGER.error("Error al inicializar RolDAO: " + e.getMessage(), e);
        }
    }

    private static UsuarioDTO usuarioSesion;
    private static String rolActual; // "ADMIN", "CAJERO", "SUPERADMINISTRADOR", "GERENTE DE INVENTARIO"
    private static int idPeriodoActual;

    public static UsuarioDTO getUsuarioSesion() {
        return usuarioSesion;
    }

    public static void setUsuarioSesion(UsuarioDTO usuario) {
        usuarioSesion = usuario;

        // Si no hay tutor en sesión, limpiamos el rol y terminamos
        if (usuarioSesion == null) {
            rolActual = null;
            return;
        }

        try {
            RolDTO rolDTO = rolDAO.buscarPorId(usuario.getRol_id());
            if (rolDTO != null) {
                rolActual = rolDTO.getNombre().toUpperCase();
            } else {
                LOGGER.warn("No se encontró un rol asociado al rol_id {}", usuario.getRol_id());
                rolActual = null;
            }
        } catch (Exception ex) {
            LOGGER.error("Error inesperado al obtener el rol: " + ex.getMessage(), ex);
            rolActual = null;
        }
    }

    public static String getRolActual() {
        return rolActual;
    }
    
    public static int getIdPeriodoActual() {
        return idPeriodoActual;
    }

    public static void setIdPeriodoActual(int idPeriodo) {
        idPeriodoActual = idPeriodo;
    }
    
    // Método para limpiar al cerrar sesión
    public static void cerrarSesion() {
        usuarioSesion = null;
        rolActual = null;
        idPeriodoActual = 0;
    }
}
