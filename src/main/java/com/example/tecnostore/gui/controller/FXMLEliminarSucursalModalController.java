package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FXMLEliminarSucursalModalController {
    @FXML private Label messageLabel;

    @FXML private void onConfirmar() {
        messageLabel.getScene().getWindow().hide();
    }

    @FXML private void onCancelar() {
        messageLabel.getScene().getWindow().hide();
    }
}
