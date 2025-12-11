package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class FXMLAjusteModalController {
    @FXML private ComboBox<String> productoCombo;
    @FXML private RadioButton entradaRadio;
    @FXML private RadioButton salidaRadio;
    @FXML private ComboBox<String> tipoAjusteCombo;
    @FXML private TextField cantidadField;
    @FXML private TextArea razonArea;
    @FXML private Label realizadoPor;

    private String loggedUser;

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
        updateRealizadoPor();
    }

    @FXML
    private void initialize() {
        productoCombo.getItems().setAll("Pantalla SmartTV", "Teclado alambrico");
        ToggleGroup group = new ToggleGroup();
        entradaRadio.setToggleGroup(group);
        salidaRadio.setToggleGroup(group);
        tipoAjusteCombo.getItems().setAll("Correccion por conteo", "Devolucion a proveedor", "Danio/Mermas");
        updateRealizadoPor();
    }

    private void updateRealizadoPor() {
        if (realizadoPor != null) {
            String usuario = loggedUser != null ? loggedUser : "";
            realizadoPor.setText("Ajuste realizado por: " + usuario);
        }
    }

    @FXML private void onConfirmar() { realizadoPor.getScene().getWindow().hide(); }
    @FXML private void onCancelar() { realizadoPor.getScene().getWindow().hide(); }
}
