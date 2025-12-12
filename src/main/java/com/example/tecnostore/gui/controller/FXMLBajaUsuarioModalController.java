package com.example.tecnostore.gui.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.servicios.ServicioDeAutenticacion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FXMLBajaUsuarioModalController {
    private static final Logger LOGGER = LogManager.getLogger(FXMLBajaUsuarioModalController.class);

    @FXML private Label messageLabel;

    private ServicioDeAutenticacion authService;
    private UsuarioDTO usuarioActual;

    @FXML
    private void initialize() {
        try {
            authService = new ServicioDeAutenticacion();
        } catch (Exception e) {
            LOGGER.error("Error inicializando ServicioDeAutenticacion: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo inicializar el módulo de baja: " + e.getMessage());
        }
    }

    public void setUsuarioActual(UsuarioDTO usuario) {
        this.usuarioActual = usuario;
        if (usuario != null && messageLabel != null) {
            messageLabel.setText("Se desactivará al usuario: " + usuario.getUsuario());
        }
    }

    @FXML
    private void onConfirmar() {
        if (authService == null || usuarioActual == null) {
            cerrar();
            return;
        }

        try {
            // Ejecutar la LÓGICA DE NEGOCIO aquí (Baja Lógica)
            authService.cambiarStatusActivo(usuarioActual, false);

            WindowServices.showInformationDialog("Éxito", "Usuario dado de baja exitosamente.");
            cerrar();

        } catch (Exception e) {
            LOGGER.error("Error al dar de baja usuario: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo dar de baja al usuario: " + e.getMessage());
        } finally {
            cerrar();
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private void cerrar() {
        if (messageLabel != null && messageLabel.getScene() != null) {
            messageLabel.getScene().getWindow().hide();
        }
    }
}
