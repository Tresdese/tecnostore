package com.example.tecnostore.gui.controller;

import java.util.List;

import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.servicios.VentaService;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

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

    // VARIABLES NUEVAS NECESARIAS
    private List<ProductoDTO> productosAVender; // Para saber qué descontar del inventario
    private boolean pagoExitoso = false;        // Para avisar al otro controlador

    public void setOwner(Window owner) { this.owner = owner; }

    // --- MÉTODO 1: Permite al controlador de ventas enviar la lista de productos ---
    public void inicializarPago(double total, List<ProductoDTO> productos) {
        // Filtrar solo productos con cantidadVenta > 0
        this.productosAVender = productos.stream()
                .filter(p -> p.getCantidadVenta() > 0)
                .toList();

        // Recalcular total en base a las cantidades filtradas
        this.totalPagar = this.productosAVender.stream()
                .mapToDouble(p -> p.getPrecio() * p.getCantidadVenta())
                .sum();

        if (totalValue != null) {
            totalValue.setText(String.format("$%,.2f", totalPagar));
        }
    }

    // --- MÉTODO 2: Permite al controlador de ventas saber si se cobró ---
    public boolean isPagoExitoso() {
        return pagoExitoso;
    }

    // Mantenemos este método por compatibilidad si lo usabas antes
    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
        if (totalValue != null) {
            totalValue.setText(String.format("$%,.2f", totalPagar));
        }
    }

    @FXML
    private void initialize() {
        metodoPago.getItems().setAll("Efectivo", "Tarjeta");
        metodoPago.setValue("Efectivo");

        // Listener para calcular el cambio automáticamente
        receivedField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    double recibido = Double.parseDouble(newValue);
                    double cambio = recibido - totalPagar;
                    changeValue.setText(String.format("$%,.2f", Math.max(0, cambio)));
                }
            } catch (NumberFormatException e) {
                changeValue.setText("$0.00");
            }
        });
    }

    @FXML
    private void onConfirmar() {
        try {
            double recibido = Double.parseDouble(receivedField.getText());

            // 1. Validar monto
            if (recibido < totalPagar) {
                WindowServices.showWarningDialog("Pago Insuficiente", "El monto recibido es menor al total.");
                return;
            }

            // 2. Obtener datos de sesión (o usar default si es nulo)
            int usuarioId = 1;
            if (Sesion.getUsuarioSesion() != null) {
                usuarioId = Sesion.getUsuarioSesion().getId();
            }

            // 3. Procesar la venta usando el servicio (Esto guarda en BD y resta Stock)
            if (productosAVender == null || productosAVender.isEmpty()) {
                WindowServices.showWarningDialog("Vacío", "Agregue productos con cantidad mayor a cero.");
                return;
            }

            VentaService servicio = new VentaService();
            // Asumimos Sucursal ID = 1
            servicio.procesarVenta(usuarioId, 1, productosAVender);

            // 4. Marcar éxito y cerrar
            WindowServices.showInformationDialog("Venta Exitosa", "La venta se ha registrado correctamente.");
            pagoExitoso = true;
            receivedField.getScene().getWindow().hide();

        } catch (NumberFormatException e) {
            WindowServices.showErrorDialog("Error", "Por favor ingrese una cantidad numérica válida.");
        } catch (Exception e) {
            e.printStackTrace();
            WindowServices.showErrorDialog("Error Crítico", "No se pudo procesar la venta: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        pagoExitoso = false;
        receivedField.getScene().getWindow().hide();
    }
}