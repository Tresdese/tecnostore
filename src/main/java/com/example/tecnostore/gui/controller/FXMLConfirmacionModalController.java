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

    public void setOwner(Window owner) { this.owner = owner; }

    public void setTitle(String title) {
        this.title = title;
        updateTexts();
    }

    public void setMessage(String message) {
        this.message = message;
        updateTexts();
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

    @FXML private void onConfirmar() { titleLabel.getScene().getWindow().hide(); }
    @FXML private void onCancelar() { titleLabel.getScene().getWindow().hide(); }
}
