package com.example.tecnostore.gui.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dao.UsuarioDAO;
import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FXMLBajaUsuarioModalController {
    private static final Logger LOGGER = LogManager.getLogger(FXMLBajaUsuarioModalController.class);

    @FXML private Label messageLabel;

    private UsuarioDAO usuarioDAO;
    private UsuarioDTO usuarioActual;

    @FXML
    private void initialize() {
        try {
            usuarioDAO = new UsuarioDAO();
        } catch (Exception e) {
            LOGGER.error("Error inicializando UsuarioDAO: {}", e.getMessage(), e);
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
        if (usuarioDAO == null) {
            cerrar();
            return;
        }
        if (usuarioActual == null) {
            try {
                WindowServices.showWarningDialog("Aviso", "No hay usuario seleccionado.");
            } catch (Exception e) {
                LOGGER.error("Error mostrando diálogo de advertencia: {}", e.getMessage(), e);
            }
            return;
        }
        try {
            usuarioDAO.cambiarStatusActivo(usuarioActual, false);
            cerrar();
        } catch (Exception e) {
            LOGGER.error("Error al dar de baja usuario: {}", e.getMessage(), e);
            WindowServices.showErrorDialog("Error", "No se pudo dar de baja al usuario: " + e.getMessage());
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private void cerrar() {
        if (messageLabel != null && messageLabel.getScene() != null) {
            messageLabel.getScene().getWindow().hide();
        }
    }
}
