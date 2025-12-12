package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.tecnostore.logic.dto.ProductoDTO;
import com.example.tecnostore.logic.servicios.ServicioProductos;
import com.example.tecnostore.logic.utils.WindowServices;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXMLRegistroVentaModalController {
    @FXML private TextField searchField;
    @FXML private TableView<ProductoDTO> productTable;
    @FXML private Label subtotalLabel;
    @FXML private Label ivaLabel;
    @FXML private Label totalLabel;
    @FXML private TableColumn<ProductoDTO, String> colCodigo;
    @FXML private TableColumn<ProductoDTO, String> colDescripcion;
    @FXML private TableColumn<ProductoDTO, Double> colPrecio;
    @FXML private TableColumn<ProductoDTO, Integer> colCantidad;
    @FXML private TableColumn<ProductoDTO, Double> colImporte;

    private ServicioProductos servicioProductos;

    @FXML private void initialize(URL location, ResourceBundle resources) {
        productTable.setItems(FXCollections.observableArrayList());

        try {
            servicioProductos = new ServicioProductos();
        } catch (Exception e) {
            e.printStackTrace();
        }

        colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));
        colDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colPrecio.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());
        colCantidad.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getStock()).asObject());
        colImporte.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getPrecio() * cell.getValue().getStock()).asObject());
    }

    @FXML
    private void onBuscar() {
        String texto = searchField.getText();
        if (texto == null || texto.isBlank()) return;

        try {
            for (ProductoDTO p : servicioProductos.obtenerTodosProductos()) {
                if (String.valueOf(p.getId()).equals(texto) || p.getNombre().toLowerCase().contains(texto.toLowerCase())) {


                    ProductoDTO item = new ProductoDTO();
                    item.setId(p.getId());
                    item.setNombre(p.getNombre());
                    item.setPrecio(p.getPrecio());
                    item.setStock(1);

                    productTable.getItems().add(item);
                    actualizarEtiquetas();
                    searchField.clear();
                    return;
                }
            }
            WindowServices.showWarningDialog("Aviso", "Producto no encontrado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onCancelarVenta() {
        searchField.getScene().getWindow().hide();

    }

    @FXML
    private void onHacerPago() {
        Window owner = searchField != null ? searchField.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLPagoModal.fxml"));
            Parent root = loader.load();
            FXMLPagoModalController controller = loader.getController();
            controller.setTotalPagar(obtenerTotal());
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

    private double obtenerTotal() {
        if (totalLabel == null || totalLabel.getText() == null) {
            return 0;
        }
        String texto = totalLabel.getText().replaceAll("[^0-9,.-]", "").replace(",", "");
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
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
