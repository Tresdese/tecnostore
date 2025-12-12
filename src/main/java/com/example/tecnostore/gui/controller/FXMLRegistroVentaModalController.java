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
import javafx.fxml.Initializable; // <--- IMPORTANTE: Importar esta interfaz
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

// 1. AGREGAMOS "implements Initializable" PARA QUE SE EJECUTE LA CONFIGURACIÓN DE TABLAS
public class FXMLRegistroVentaModalController implements Initializable {

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

    // Este método ahora SÍ se ejecutará al abrir la ventana porque implementamos la interfaz
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productTable.setItems(FXCollections.observableArrayList());

        try {
            servicioProductos = new ServicioProductos();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuración de columnas (Sin esto, la tabla se ve vacía)
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
            boolean encontrado = false;
            for (ProductoDTO p : servicioProductos.obtenerTodosProductos()) {
                if (String.valueOf(p.getId()).equals(texto) || p.getNombre().toLowerCase().contains(texto.toLowerCase())) {

                    ProductoDTO item = new ProductoDTO();
                    item.setId(p.getId());
                    item.setNombre(p.getNombre());
                    item.setPrecio(p.getPrecio());
                    item.setStock(1);

                    productTable.getItems().add(item);
                    actualizarEtiquetas(); // Recalcula totales
                    searchField.clear();
                    encontrado = true;
                    return; // Salimos al encontrar uno
                }
            }
            if (!encontrado) {
                WindowServices.showWarningDialog("Aviso", "Producto no encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelarVenta() {
        searchField.getScene().getWindow().hide();
    }

    @FXML
    private void onHacerPago() throws IOException {
        if (productTable.getItems().isEmpty()) {
            WindowServices.showWarningDialog("Vacío", "Agregue productos antes de pagar.");
            return;
        }

        Window owner = searchField != null ? searchField.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLPagoModal.fxml"));
            Parent root = loader.load();

            FXMLPagoModalController controller = loader.getController();

            // 2. CORREGIDO: Usamos el método que recibe TOTAL + PRODUCTOS (para descontar inventario)
            controller.inicializarPago(obtenerTotal(), productTable.getItems());

            controller.setOwner(owner);

            Stage modalStage = new Stage();
            if (owner != null) {
                modalStage.initOwner(owner);
            }
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Pago");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            // Si el pago fue exitoso, limpiamos la tabla
            if (controller.isPagoExitoso()) {
                productTable.getItems().clear();
                actualizarEtiquetas();
            }

        } catch (IOException e) {
            e.printStackTrace();
            WindowServices.showErrorDialog("Error", "No se pudo abrir el modal de pago: " + e.getMessage());
        }
    }

    // Método para calcular el total sumando los items de la tabla
    private double obtenerTotal() {
        double suma = 0;
        if (productTable != null) {
            for (ProductoDTO p : productTable.getItems()) {
                suma += p.getPrecio() * p.getStock();
            }
        }
        return suma;
    }

    private void actualizarEtiquetas() {
        double total = obtenerTotal(); // Obtiene la suma real
        double subtotal = total / 1.16; // Desglosa IVA
        double iva = total - subtotal;

        if (totalLabel != null) totalLabel.setText(String.format("$%,.2f", total));
        if (subtotalLabel != null) subtotalLabel.setText(String.format("$%,.2f", subtotal));
        if (ivaLabel != null) ivaLabel.setText(String.format("$%,.2f", iva));
    }
}