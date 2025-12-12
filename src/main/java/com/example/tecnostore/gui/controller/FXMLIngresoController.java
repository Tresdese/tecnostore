package com.example.tecnostore.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.servicios.ServicioDeAutenticacion;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class FXMLIngresoController implements Initializable {
    @FXML
    private TextField userField;

    @FXML
    private PasswordField passwordField;

    private static final Logger LOGGER = LogManager.getLogger(FXMLIngresoController.class);

    private ServicioDeAutenticacion authService;
    private final WindowServices windowServices = new WindowServices();

    @FXML
    private void onLogin(ActionEvent event) {
        String username = userField.getText();
        String password = passwordField.getText();

        if (validarCampos(username, password)) {
            irLogin(username, password, event);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            authService = new ServicioDeAutenticacion();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "Ocurrió un error al inicializar: " + e.getMessage());
            LOGGER.error("Error al inicializar: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void register(ActionEvent event) {
        try {
            windowServices.openModal("FXMLRegistro.fxml", "Registro");
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el formulario de registro: " + e.getMessage());
            LOGGER.error("Error al abrir el formulario de registro: " + e.getMessage());
        }
    }

    private boolean validarCampos(String username, String password) throws IllegalArgumentException {
        boolean respuesta = true;
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            WindowServices.showErrorDialog("Error", "Campos vacíos");
            respuesta = false;
        }
        return respuesta;
    }

    private void irLogin(String username, String password, ActionEvent event) {
        try {
            if (authService == null) {
                authService = new ServicioDeAutenticacion();
            }
            boolean ok = authService.autenticarUsuario(username, password);
            if (ok) {
                UsuarioDTO usuario = authService.buscarUsuarioPorUsernameYContrasena(username, password);
                if (usuario == null) {
                    WindowServices.showErrorDialog("Autenticación", "No se pudo recuperar la información del usuario.");
                    return;
                }

                // Guardar usuario y rol en sesión para el resto de la aplicacion
                Sesion.setUsuarioSesion(usuario);

                WindowServices.showInformationDialog("Autenticación", "Inicio de sesión exitoso.");
                windowServices.goToWindow("FXMLPrincipal.fxml", event, "Principal");
            } else {
                WindowServices.showErrorDialog("Autenticación", "Usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "Ocurrió un error al autenticar: " + e.getMessage());
            LOGGER.error("Error al autenticar: {}", e.getMessage(), e);
        }
    }
}
