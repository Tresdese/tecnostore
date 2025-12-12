package com.example.tecnostore.gui.controller;

import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class FXMLPagoModalController {
    @FXML private ComboBox<String> metodoPago;
    @FXML private Label totalValue;
    @FXML private TextField receivedField;
    @FXML private Label changeValue;
    @FXML private Button confirmButton;

    private double totalPagar;
    private Window owner;
    private boolean confirmado;
    private double montoRecibido;
    private double cambio;
    private String metodoSeleccionado;

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
        updateTotals();
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    @FXML
    private void initialize() {
        if (metodoPago != null) {
            metodoPago.getItems().setAll("Efectivo", "Tarjeta");
            metodoPago.setValue("Efectivo");
        }

        if (confirmButton != null) {
            confirmButton.setDisable(true);
        }

        if (receivedField != null) {
            receivedField.textProperty().addListener((obs, oldVal, newVal) -> recalcularCambio());
        }

        updateTotals();
    }

    @FXML
    private void onConfirmar() {
        if (!entradaValida()) {
            WindowServices.showErrorDialog("Pago", "Ingrese un monto válido y suficiente.");
            return;
        }

        confirmado = true;
        metodoSeleccionado = metodoPago != null ? metodoPago.getValue() : null;
        montoRecibido = parseMontoRecibido();
        cambio = montoRecibido - totalPagar;

        cerrarModal();
        if (owner != null) {
            owner.hide();
        }
    }

    @FXML
    private void onCancelar() {
        confirmado = false;
        cerrarModal();
    }

    private void updateTotals() {
        if (totalValue != null) {
            totalValue.setText(String.format("$%,.2f", totalPagar));
        }
        recalcularCambio();
    }

    private void recalcularCambio() {
        double recibido = parseMontoRecibido();
        boolean valido = recibido >= totalPagar && recibido > 0;
        double diff = valido ? recibido - totalPagar : 0;

        if (changeValue != null) {
            changeValue.setText(String.format("$%,.2f", diff));
        }
        if (confirmButton != null) {
            confirmButton.setDisable(!valido);
        }
    }

    private boolean entradaValida() {
        return totalPagar > 0 && parseMontoRecibido() >= totalPagar;
    }

    private double parseMontoRecibido() {
        if (receivedField == null || receivedField.getText() == null) {
            return 0d;
        }
        String texto = receivedField.getText().trim().replace(",", "");
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    private void cerrarModal() {
        if (changeValue != null && changeValue.getScene() != null) {
            changeValue.getScene().getWindow().hide();
        }
    }

    public boolean isConfirmado() { return confirmado; }
    public double getMontoRecibido() { return montoRecibido; }
    public double getCambio() { return cambio; }
    public String getMetodoSeleccionado() { return metodoSeleccionado; }
}