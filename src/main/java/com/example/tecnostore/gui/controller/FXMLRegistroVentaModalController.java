package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.util.Optional;

import com.example.tecnostore.logic.dao.VentaDAO;
import com.example.tecnostore.logic.dto.VentaResumenDTO;
import com.example.tecnostore.logic.utils.Sesion;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXMLRegistroVentaModalController {
    @FXML private TextField searchField;
    @FXML private TableView<VentaResumenDTO> productTable;
    @FXML private Label subtotalLabel;
    @FXML private Label ivaLabel;
    @FXML private Label totalLabel;
    @FXML private TableColumn<VentaResumenDTO, String> colCodigo;
    @FXML private TableColumn<VentaResumenDTO, String> colDescripcion;

    private VentaDAO ventaDAO;
    private final ObservableList<VentaResumenDTO> ventas = FXCollections.observableArrayList();

    @FXML private void initialize() {
        productTable.setItems(ventas);

        try {
            ventaDAO = new VentaDAO();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Ventas", "No se pudo inicializar el acceso a ventas: " + e.getMessage());
        }

        colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getUsuario() != null ? cell.getValue().getUsuario() : ""));
        colDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(
                String.format("$%,.2f", cell.getValue().getMontoTotal())));

        cargarVentas();
    }

    @FXML
    private void onBuscar() {
        String texto = searchField.getText();
        cargarVentas(texto);
    }

    private void calcularTotal() {
        double total = obtenerTotal();
        double subtotal = total / 1.16;
        double iva = total - subtotal;

        if (totalLabel != null) totalLabel.setText(String.format("$%,.2f", total));
        if (subtotalLabel != null) subtotalLabel.setText(String.format("$%,.2f", subtotal));
        if (ivaLabel != null) ivaLabel.setText(String.format("$%,.2f", iva));
    }

    @FXML
    private void onHacerPago() {
        double total = obtenerTotal();
        if (total <= 0) {
            try {
                WindowServices.showWarningDialog("Pago", "No hay ventas cargadas o el total es 0. Agrega una venta antes de pagar.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        Window owner = searchField != null ? searchField.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLPagoModal.fxml"));
            Parent root = loader.load();
            FXMLPagoModalController controller = loader.getController();
            controller.setTotalPagar(total);
            controller.setOwner(owner);

            Stage modalStage = new Stage();
            if (owner != null) {
                modalStage.initOwner(owner);
            }
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Pago");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de pago: " + e.getMessage());
        }
    }

    @FXML
    private void onAgregarVenta() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar venta");
        dialog.setHeaderText("Ingrese el monto de la venta");
        dialog.setContentText("Monto:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(result.get().replace(",", "").trim());
        } catch (NumberFormatException e) {
            WindowServices.showErrorDialog("Venta", "Monto inválido.");
            return;
        }

        if (monto <= 0) {
            try {
                WindowServices.showWarningDialog("Venta", "El monto debe ser mayor a 0.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String usuario = Sesion.getUsuarioSesion() != null
                ? String.valueOf(Sesion.getUsuarioSesion().getId())
                : null;

        try {
            if (ventaDAO == null) {
                ventaDAO = new VentaDAO();
            }
            ventaDAO.registrarVenta(usuario, monto);
            cargarVentas();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Venta", "No se pudo registrar la venta: " + e.getMessage());
        }
    }

    private double obtenerTotal() {
        return ventas.stream().mapToDouble(VentaResumenDTO::getMontoTotal).sum();
    }

    @FXML private void onCancelarVenta() {
        Stage stage = searchField != null && searchField.getScene() != null ? (Stage) searchField.getScene().getWindow() : null;
        if (stage != null) {
            stage.close();
        }
    }

    private void cargarVentas() { cargarVentas(null); }

    private void cargarVentas(String filtro) {
        ventas.clear();
        try {
            if (ventaDAO == null) {
                ventaDAO = new VentaDAO();
            }
            var data = ventaDAO.obtenerVentas();
            if (filtro != null && !filtro.isBlank()) {
                String term = filtro.trim().toLowerCase();
                data = data.stream().filter(v ->
                        (v.getUsuario() != null && v.getUsuario().toLowerCase().contains(term))
                                || String.valueOf(v.getMontoTotal()).toLowerCase().contains(term)
                ).toList();
            }
            ventas.addAll(data);
            calcularTotal();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Ventas", "No se pudieron cargar las ventas: " + e.getMessage());
        }
    }
}
