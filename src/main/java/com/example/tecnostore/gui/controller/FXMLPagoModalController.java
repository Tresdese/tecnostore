package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class FXMLPagoModalController {
    @FXML private ComboBox<String> metodoPago;
    @FXML private Label totalValue;
    @FXML private TextField receivedField;
    @FXML private Label changeValue;

    private double totalPagar;
    private Window owner;

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
        updateTotals();
    }

    public void setOwner(Window owner) { this.owner = owner; }

    @FXML
    private void initialize() {
        metodoPago.getItems().setAll("Efectivo", "Tarjeta");
        metodoPago.setValue("Efectivo");
        updateTotals();
    }
//
    private void updateTotals() {
        if (totalValue != null) {
            totalValue.setText(String.format("$%,.2f", totalPagar));
        }
        if (changeValue != null) {
            changeValue.setText("$0.00");
        }
    }
//
    @FXML
    private void onConfirmar() {
        changeValue.getScene().getWindow().hide();
        if (owner != null) {
            owner.hide();
        }
    }

    @FXML
    private void onCancelar() {
        changeValue.getScene().getWindow().hide();
    }
}
