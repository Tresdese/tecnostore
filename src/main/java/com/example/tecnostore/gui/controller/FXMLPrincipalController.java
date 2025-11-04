package com.example.tecnostore.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.App;
import com.example.tecnostore.logic.dto.UsuarioDTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXMLPrincipalController implements Initializable {
    @FXML
    private Button logOutButton;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button employeeListButton;

    private UsuarioDTO usuarioDTO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void logOutActionButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLIngreso.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) logOutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setButtonVisibility(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    public void setUsuarioDTO(UsuarioDTO usuarioActual) {
        this.usuarioDTO = usuarioActual;
        // Actualiza la UI cuando se establece el usuario
        if (usuarioActual != null) {
            setUsername(usuarioActual);
            // pasar el id y rol al método que actualiza botones
            setUserRole(usuarioActual.getRol_id());
        }
    }

    private void setUsername(UsuarioDTO usuarioActual) {
        welcomeLabel.setText("Bienvenido, " + usuarioActual.getUsuario());
    }

    private void setUserRole(int usuarioRol) {
        
        switch (usuarioRol) {
            case 1: // Admin
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, true);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                break;
            case 2: // cajero
                setButtonVisibility(registerButton, false);
                setButtonVisibility(editButton, true);
                setButtonVisibility(deleteButton, false);
                setButtonVisibility(employeeListButton, true);
                break;
            case 3: // superadministrador
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, true);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                break;
            case 4: // gerente
                setButtonVisibility(registerButton, true);
                setButtonVisibility(editButton, false);
                setButtonVisibility(deleteButton, true);
                setButtonVisibility(employeeListButton, true);
                break;
            default:
                setButtonVisibility(registerButton, false);
                setButtonVisibility(editButton, false);
                setButtonVisibility(deleteButton, false);
                setButtonVisibility(employeeListButton, false);
                break;
        }
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        setUserRole(usuarioDTO.getRol_id());
    }

    @FXML
    private void handleEditButton(ActionEvent event) {
        setUserRole(usuarioDTO.getRol_id());
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        setUserRole(usuarioDTO.getRol_id());
    }

    @FXML
    private void handleEmployeeListButton(ActionEvent event) {
        setUserRole(usuarioDTO.getRol_id());
    }
    
}
