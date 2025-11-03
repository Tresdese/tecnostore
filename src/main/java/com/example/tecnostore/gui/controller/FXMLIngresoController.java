package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.example.App;
import com.example.tecnostore.logic.servicios.ServicioDeAutenticacion;

public class FXMLIngresoController implements Initializable {
    
    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField textFieldPassword;
    
    private ServicioDeAutenticacion authService;
    
    @FXML
    private void login(ActionEvent event) {
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos vacíos");
            alert.setHeaderText(null);
            alert.setContentText("Ingrese usuario y contraseña.");
            alert.showAndWait();
            return;
        }

        try {
            if (authService == null) {
                authService = new ServicioDeAutenticacion();
            }
            boolean ok = authService.autenticarUsuario(username, password);
            if (ok) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Autenticación");
                alert.setHeaderText(null);
                alert.setContentText("Inicio de sesión exitoso.");
                alert.showAndWait();
                // cargar la vista principal
                App.setRoot("/com/example/tecnostore/gui/views/FXMLPrincipal.fxml");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Autenticación");
                alert.setHeaderText(null);
                alert.setContentText("Usuario o contraseña incorrectos.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al autenticar: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    @FXML
    private void register(ActionEvent event) {
        try {
            // cargar la vista de registro
            App.setRoot("/com/example/tecnostore/gui/views/FXMLRegistro.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir el formulario de registro: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            authService = new ServicioDeAutenticacion();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de inicialización");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo inicializar el servicio de autenticación: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }    
}
