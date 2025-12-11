package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class UsuarioModalController {

    @FXML private TextField nombreField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> rolCombo;
    @FXML private CheckBox activoCheckbox;
    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;

    private boolean isNew = true;
    private Window owner;

    @FXML
    private void initialize() {
        rolCombo.getItems().setAll("Administrador", "Gerente de Inventario", "Vendedor");
        applyState();
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
        applyState();
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    private void applyState() {
        if (rolCombo == null || activoCheckbox == null) {
            return;
        }
        if (isNew) {
            rolCombo.setValue("Vendedor");
            activoCheckbox.setSelected(true);
        }
    }

    @FXML
    private void onGuardar() {
        cancelarButton.getScene().getWindow().hide();
    }

    @FXML
    private void onCancelar() {
        cancelarButton.getScene().getWindow().hide();
    }
}
