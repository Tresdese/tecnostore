package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.example.App;
import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.servicios.ServicioDeAutenticacion;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMLIngresoController implements Initializable {
    @FXML
    private Button loginButton;
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
                UsuarioDTO usuario = authService.buscarUsuarioPorUsernameYContrasena(username, password);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Autenticación");
                alert.setHeaderText(null);
                alert.setContentText("Inicio de sesión exitoso.");
                alert.showAndWait();
                // cargar la vista principal
                // App.setRoot("/com/example/tecnostore/gui/views/FXMLPrincipal.fxml");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLPrincipal.fxml"));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                FXMLPrincipalController principalController = loader.getController();
                principalController.setUsuarioDTO(usuario);
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
