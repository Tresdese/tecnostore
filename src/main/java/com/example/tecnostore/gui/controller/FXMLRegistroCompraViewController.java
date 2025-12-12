package com.example.tecnostore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import com.example.tecnostore.logic.servicios.CompraService;
import com.example.tecnostore.logic.dto.CompraDTO; // Asumido
import com.example.tecnostore.logic.utils.WindowServices;

import java.io.IOException;
import java.util.Optional;

public class FXMLRegistroCompraViewController {
    @FXML private TextField numCompraField;
    @FXML private TableView<?> productsTable;
    @FXML private Label lblSubtotal;
    @FXML private Label lblImpuestos;
    @FXML private Label lblTotal;

    private CompraService compraService;

    @FXML
    private void initialize() {
        productsTable.setPlaceholder(new Label("No hay productos en la orden de compra"));
        try {
            compraService = new CompraService();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo inicializar el servicio de compras: " + e.getMessage());
        }
    }

    // Método auxiliar simulado para obtener datos de la UI
    private CompraDTO construirCompraDTO() {
        CompraDTO compra = new CompraDTO();
        // Lógica: setear total, proveedor, sucursal, y la lista de items.
        // compra.setTotal(obtenerValorTotalDeUI());
        return compra;
    }

    @FXML
    private void onGuardarBorrador() throws IOException {
        if (productsTable.getItems().isEmpty()) {
            WindowServices.showWarningDialog("Aviso", "No se puede guardar un borrador sin productos.");
            return;
        }

        try {
            CompraDTO compra = construirCompraDTO();
            compraService.guardarBorrador(compra);
            WindowServices.showInformationDialog("Éxito", "Borrador de compra guardado.");
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo guardar el borrador: " + e.getMessage());
        }
    }

    @FXML
    private void onRegistrarCompra() throws IOException {
        if (productsTable.getItems().isEmpty()) {
            WindowServices.showWarningDialog("Aviso", "No se puede registrar una compra sin productos.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Registro");
        confirm.setHeaderText("Registro de Compra");
        confirm.setContentText("¿Desea registrar esta compra? Esta acción afectará el inventario y no es reversible.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                CompraDTO compra = construirCompraDTO();
                compraService.registrarCompra(compra);

                WindowServices.showInformationDialog("Éxito", "Compra registrada correctamente.");

                // Cerrar la ventana de registro de compra
                Stage currentStage = (Stage) productsTable.getScene().getWindow();
                currentStage.close();

            } catch (Exception e) {
                WindowServices.showErrorDialog("Error", "Fallo al registrar la compra: " + e.getMessage());
            }
        }
    }
}
