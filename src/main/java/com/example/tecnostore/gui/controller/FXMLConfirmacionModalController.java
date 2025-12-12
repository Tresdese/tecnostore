package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Window;

public class FXMLConfirmacionModalController {
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;

    private Window owner;
    private String title;
    private String message;

    // Bandera para indicar si el usuario presionó 'Confirmar'
    private boolean confirmed = false;

    public void setOwner(Window owner) { this.owner = owner; }

    public void setTitle(String title) {
        this.title = title;
        updateTexts();
    }

    public void setMessage(String message) {
        this.message = message;
        updateTexts();
    }

    // Nuevo método para que el controlador padre obtenga el resultado
    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void initialize() {
        updateTexts();
    }

    private void updateTexts() {
        if (titleLabel != null) {
            titleLabel.setText(title != null ? title : "Confirmacion");
        }
        if (messageLabel != null) {
            messageLabel.setText(message != null ? message : "");
        }
    }

    // Al confirmar, se establece el flag y se cierra
    @FXML private void onConfirmar() {
        this.confirmed = true;
        titleLabel.getScene().getWindow().hide();
    }

    // Al cancelar, el flag permanece en false (valor inicial) y se cierra
    @FXML private void onCancelar() {
        this.confirmed = false;
        titleLabel.getScene().getWindow().hide();
    }
}