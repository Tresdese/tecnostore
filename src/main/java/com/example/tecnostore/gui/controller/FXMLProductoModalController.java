package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class FXMLProductoModalController {
    @FXML private Label titleLabel;
    @FXML private TextField codigoField;
    @FXML private TextField descripcionField;
    @FXML private TextField marcaField;
    @FXML private TextField precioField;
    @FXML private TextField cantidadField;

    private boolean isNew = true;
    private Window owner;

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
        applyState();
    }

    public void setOwner(Window owner) { this.owner = owner; }

    @FXML
    private void initialize() {
        applyState();
    }

    private void applyState() {
        if (codigoField == null) {
            return;
        }
        if (isNew) {
            titleLabel.setText("Registrar producto");
            codigoField.clear();
            codigoField.setDisable(false);
            descripcionField.clear();
            marcaField.clear();
            precioField.clear();
            cantidadField.clear();
        } else {
            titleLabel.setText("Editar producto");
            codigoField.setDisable(true);
        }
    }
//
    @FXML private void onGuardar() { titleLabel.getScene().getWindow().hide(); }
    @FXML private void onCancelar() { titleLabel.getScene().getWindow().hide(); }
}
