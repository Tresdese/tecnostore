package com.example.tecnostore.gui.controller;

import java.io.IOException;

import com.example.tecnostore.logic.utils.WindowServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLLoginViewController {
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;

    @FXML
    private void onLogin() {
        try {
            Parent root = WindowServices.loadFXML("FXMLPuntoVentaView.fxml");
            Stage nuevoStage = new Stage();
            nuevoStage.setTitle("Punto de venta");
            nuevoStage.setScene(new Scene(root));
            nuevoStage.show();
            userField.getScene().getWindow().hide();
        } catch (IOException e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir la vista de punto de venta: " + e.getMessage());
        }
    }
}
