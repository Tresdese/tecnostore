package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FXMLEliminarSucursalModalController {
    @FXML private Label messageLabel;

    private boolean confirmado = false;

    @FXML private void onConfirmar() {
        confirmado = true;
        messageLabel.getScene().getWindow().hide();
    }

    @FXML private void onCancelar() {
        messageLabel.getScene().getWindow().hide();
    }

    public boolean isConfirmado() {
        return confirmado;
    }//

    public void setNombreSucursal(String nombre) {
        if (messageLabel != null && nombre != null) {
            messageLabel.setText("Se eliminará la sucursal: " + nombre);
        }
    }
}
