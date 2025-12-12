package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

    @FXML private void initialize(URL location, ResourceBundle resources) {
        productTable.setItems(ventas);

        try {
            ventaDAO = new VentaDAO();
        } catch (Exception e) {
            e.printStackTrace();
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

    @FXML private void onCancelarVenta() {
        searchField.getScene().getWindow().hide();

    }

    @FXML
    private void onHacerPago() {
        double total = obtenerTotal();
        if (total <= 0) {
            try {
                WindowServices.showWarningDialog("Pago", "No hay ventas cargadas o el total es 0. Agrega una venta antes de pagar.");
            } catch (NullPointerException | IOException e) {
                // TODO Auto-generated catch block
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
//
            Stage modalStage = new Stage();
            if (owner != null) {
                modalStage.initOwner(owner);
            }
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Pago");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de pago: " + e.getMessage());
        }
    }

    @FXML
    private void onAgregarVenta() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar venta");
        dialog.setHeaderText("Ingrese el monto de la venta");
        dialog.setContentText("Monto:");

        dialog.showAndWait().ifPresent(valor -> {
            double monto;
            try {
                monto = Double.parseDouble(valor.replace(",", "").trim());
            } catch (NumberFormatException e) {
                WindowServices.showErrorDialog("Venta", "Monto inválido.");
                return;
            }

            if (monto <= 0) {
                try {
                    WindowServices.showWarningDialog("Venta", "El monto debe ser mayor a 0.");
                } catch (NullPointerException | IOException e) {
                    // TODO Auto-generated catch block
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
        });
    }

    private void cargarVentas() {
        cargarVentas(null);
    }

    private void cargarVentas(String filtro) {
        ventas.clear();
        try {
            if (ventaDAO == null) {
                ventaDAO = new VentaDAO();
            }
            var data = ventaDAO.obtenerVentas();
            if (filtro != null && !filtro.isBlank()) {
                String term = filtro.trim().toLowerCase();
                data = data.stream()
                        .filter(v -> (v.getUsuario() != null && v.getUsuario().toLowerCase().contains(term))
                                || String.valueOf(v.getMontoTotal()).toLowerCase().contains(term))
                        .toList();
            }
            ventas.addAll(data);
            actualizarEtiquetas();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Ventas", "No se pudieron cargar las ventas: " + e.getMessage());
        }
    }

    private double obtenerTotal() {
        return ventas.stream().mapToDouble(VentaResumenDTO::getMontoTotal).sum();
    }

    //actualizar información de las etiquetas de ventas
    private void actualizarEtiquetas() {
        double total = obtenerTotal();
        double subtotal = total / 1.16;
        double iva = total - subtotal;

        if (totalLabel != null) totalLabel.setText(String.format("$%,.2f", total));
        if (subtotalLabel != null) subtotalLabel.setText(String.format("$%,.2f", subtotal));
        if (ivaLabel != null) ivaLabel.setText(String.format("$%,.2f", iva));
    }
}
