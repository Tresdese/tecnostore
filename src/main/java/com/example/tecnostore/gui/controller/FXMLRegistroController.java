package com.example.tecnostore.gui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.example.tecnostore.logic.dao.RolDAO;
import com.example.tecnostore.logic.dto.RolDTO;
import com.example.tecnostore.logic.dto.UsuarioDTO;
import com.example.tecnostore.logic.servicios.ServicioDeAutenticacion;
import com.example.tecnostore.logic.servicios.ServicioRoles;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FXMLRegistroController implements Initializable {
    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldUsername;
    
    @FXML
    private TextField textFieldPassword;

    @FXML
    private ComboBox comboBoxRole;

    private ServicioRoles servicioRoles;

    @FXML
    private void registerUser(ActionEvent event) {
        if (!textFieldName.getText().isBlank() || !textFieldUsername.getText().isBlank() || !textFieldPassword.getText().isBlank()) {
            register();
        }
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
            servicio.guardarUsuario(usuario);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoles() {
        try {
            ServicioRoles servicioRoles = new ServicioRoles();
            List<String> roles = servicioRoles.obtenerNombresRoles();
            comboBoxRole.getItems().addAll(roles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadRoles();
    }    
}
