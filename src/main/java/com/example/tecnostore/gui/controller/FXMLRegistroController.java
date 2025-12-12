package com.example.tecnostore.gui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.servicios.ServicioDeAutenticacion;
import com.example.tecnostore.logic.servicios.ServicioRoles;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLRegistroController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLRegistroController.class);
    
    @FXML
    private Button returnBackButton;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldUsername;
    
    @FXML
    private TextField textFieldPassword;

    @FXML
    private ComboBox<String> comboBoxRole;

    private ServicioRoles servicioRoles;
    // Si el registro es exitoso, se almacena aquí el usuario creado para que el invocador modal
    // pueda leerlo después de cerrar el diálogo.
    private UsuarioDTO createdUser;

    public UsuarioDTO getCreatedUser() {
        return createdUser;
    }
//
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadRoles();
    }

    private void register() {

        UsuarioDTO usuario = new UsuarioDTO();
        String role = (String) comboBoxRole.getValue();

        try {
            servicioRoles = new ServicioRoles();

            int idRol = servicioRoles.obtenerIdPorNombre(role);
            usuario.setNombre(textFieldName.getText());
            usuario.setUsuario(textFieldUsername.getText());
            usuario.setContrasenaHash(textFieldPassword.getText());
            usuario.setRol_id(idRol);

            ServicioDeAutenticacion servicio = new ServicioDeAutenticacion();
            boolean ok = servicio.guardarUsuario(usuario);
            if (ok) {
                // indicar éxito y cerrar el diálogo modal
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registro");
                alert.setHeaderText(null);
                alert.setContentText("Registro exitoso.");
                alert.showAndWait();

                // guardar el usuario creado para quien abrió el modal
                this.createdUser = usuario;

                // cerrar solo la ventana actual (el diálogo modal)
                Stage currentStage = (Stage) returnBackButton.getScene().getWindow();
                currentStage.close();
            }
            
        } catch (Exception e) {
            LOGGER.error("Error al cargar roles: {}", e.getMessage(), e);
        }
    }

    private void loadRoles() {
        try {
            ServicioRoles rolesService = new ServicioRoles();
            List<String> roles = rolesService.obtenerNombresRoles();
            comboBoxRole.getItems().addAll(roles);
        } catch (Exception e) {
            LOGGER.error("Error al cargar roles: {}", e.getMessage(), e);
        }
    }  

    @FXML
    private void registerUserButton(ActionEvent event) {
        if (!textFieldName.getText().isBlank() || !textFieldUsername.getText().isBlank() || !textFieldPassword.getText().isBlank()) {
            register();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos vacíos");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, complete todos los campos.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleReturnBackButton(ActionEvent event) {
        try {
            Stage stage = (Stage) returnBackButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            LOGGER.error("Error al cerrar ventana: {}", e.getMessage(), e);
        }
    }
}
