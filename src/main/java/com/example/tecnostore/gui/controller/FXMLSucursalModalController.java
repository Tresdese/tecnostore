package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class FXMLSucursalModalController {
    @FXML private TextField nombreField;
    @FXML private TextField direccionField;
    @FXML private TextField telefonoField;
    @FXML private ComboBox<String> estadoCombo;

    private boolean isNew = true;
    private Window owner;

    public void setIsNew(boolean isNew) { this.isNew = isNew; }
    public void setOwner(Window owner) { this.owner = owner; }

    @FXML
    private void initialize() {
        estadoCombo.getItems().setAll("Activo", "Inactivo");
        if (isNew) estadoCombo.setValue("Activo");
    }

    @FXML private void onConfirmar() { nombreField.getScene().getWindow().hide(); }
    @FXML private void onCancelar() { nombreField.getScene().getWindow().hide(); }
}
