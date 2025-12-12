package com.example.tecnostore.gui.controller;

import com.example.tecnostore.logic.dto.DetalleVentaDTO;
import com.example.tecnostore.logic.dto.VentaDTO;
import com.example.tecnostore.logic.servicios.VentaService;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class FXMLPagoModalController {
    @FXML private ComboBox<String> metodoPago;
    @FXML private Label totalValue;
    @FXML private TextField receivedField;
    @FXML private Label changeValue;

    private double totalPagar;
    private Window owner; // La ventana del Registro de Venta
    private List<DetalleVentaDTO> detallesVenta; // <--- NUEVO ATRIBUTO
    private VentaService ventaService; // <--- NUEVO ATRIBUTO

    // *** MÉTODO AGREGADO PARA TRANSFERIR DETALLES ***
    public void setDetallesVenta(List<DetalleVentaDTO> detalles) {
        this.detallesVenta = detalles;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
        updateTotals();
    }

    public void setOwner(Window owner) { this.owner = owner; }

    @FXML
    private void initialize() {
        metodoPago.getItems().setAll("Efectivo", "Tarjeta");
        metodoPago.setValue("Efectivo");
        try {
            this.ventaService = new VentaService();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo inicializar el servicio de ventas: " + e.getMessage());
        }
        updateTotals();
    }

    private void updateTotals() {
        if (totalValue != null) {
            totalValue.setText(String.format("$%,.2f", totalPagar));
        }

        // Lógica de cálculo de cambio (ajustada para el evento onConfirmar)
        if (changeValue != null) {
            double recibido = 0;
            // Solo intentamos leer el campo recibido si el campo no es nulo
            if (receivedField != null && receivedField.getText() != null && !receivedField.getText().isEmpty()) {
                try {
                    recibido = Double.parseDouble(receivedField.getText());
                } catch (NumberFormatException e) {
                    // Si falla la conversión, recibido sigue siendo 0
                }
            }

            double cambio = Math.max(0, recibido - totalPagar);
            changeValue.setText(String.format("$%,.2f", cambio));
        }
    }

    // *** LÓGICA DE NEGOCIO Y CIERRE IMPLEMENTADA ***
    @FXML
    private void onConfirmar() throws IOException {
        updateTotals();
        if (ventaService == null || detallesVenta == null || detallesVenta.isEmpty()) {
            WindowServices.showWarningDialog("Error", "No hay servicio de venta o productos para registrar.");
            return;
        }

        try {
            // 1. Construir el DTO final de Venta
            VentaDTO venta = new VentaDTO();
            venta.setTotal(totalPagar);
            venta.setMetodoPago(metodoPago.getValue());
            venta.setDetalles(detallesVenta);

            // 2. GUARDAR TRANSACCIÓN CRÍTICA
            ventaService.registrarVentaCompleta(venta);

            WindowServices.showInformationDialog("Éxito", "Venta registrada y stock descontado.");

            // 3. CERRAR MODAL ACTUAL
            changeValue.getScene().getWindow().hide();

            // 4. CERRAR VENTANA PADRE (Registro de Venta) para forzar el refresco de la tabla principal
            if (owner != null) {
                owner.hide(); // Cierra FXMLRegistroVentaModal
            }

        } catch (Exception e) {
            WindowServices.showErrorDialog("Error de Transacción", "Fallo al registrar la venta: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        changeValue.getScene().getWindow().hide();
    }
}