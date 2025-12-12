package com.example.tecnostore.gui.controller;

import java.io.IOException;
import java.util.List;

import com.example.tecnostore.logic.dto.DetalleVentaDTO;
import com.example.tecnostore.logic.servicios.VentaService;
import com.example.tecnostore.logic.utils.WindowServices; // NECESARIO

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;     // NECESARIO
import javafx.scene.control.cell.PropertyValueFactory;      // NECESARIO
import javafx.scene.layout.VBox;   // NECESARIO
import javafx.stage.Modality;      // NECESARIO
import javafx.stage.Stage;

public class FXMLPuntoVentaViewController {
    private final WindowServices windowServices = new WindowServices();

    @FXML private VBox rootContainer;

    @FXML private TableView<DetalleVentaDTO> salesTable;

    @FXML private TableColumn<DetalleVentaDTO, Integer> colVenta;
    @FXML private TableColumn<DetalleVentaDTO, Integer> colProducto;
    @FXML private TableColumn<DetalleVentaDTO, String> colNombre;
    @FXML private TableColumn<DetalleVentaDTO, Integer> colCantidad;
    @FXML private TableColumn<DetalleVentaDTO, Double> colPrecio;
    @FXML private TableColumn<DetalleVentaDTO, Double> colSubtotal;

    private VentaService ventaService;

    @FXML
    private void initialize() {
        configurarColumnas();

        try {
            this.ventaService = new VentaService();
            loadSales();
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "Error al inicializar el servicio de ventas: " + e.getMessage());
        }
    }

    private void configurarColumnas() {
        if (colVenta != null) colVenta.setCellValueFactory(new PropertyValueFactory<>("ventaId"));
        if (colProducto != null) colProducto.setCellValueFactory(new PropertyValueFactory<>("productoId"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        if (colCantidad != null) colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        if (colPrecio != null) colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        if (colSubtotal != null) colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }


    private void loadSales() {
        if (salesTable == null) return;
        try {
            List<DetalleVentaDTO> ventas = ventaService.obtenerDetallesVentas();
            salesTable.getItems().setAll(ventas);
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "No se pudieron cargar las ventas: " + e.getMessage());
        }
    }

    @FXML
    private void onBuscarVentas() {
        loadSales();
    }

    @FXML
    private void onHacerVenta() {
        if (rootContainer == null || rootContainer.getScene() == null) {
            return;
        }

        Stage owner = (Stage) rootContainer.getScene().getWindow();

        try {
            // Carga manual del FXML para evitar el error de ruta con openModal
            final String FXML_PATH = "/com/example/tecnostore/gui/views/FXMLRegistroVentaModal.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Registro de venta");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            // Refresca la tabla principal después de que el modal se cierra
            loadSales();

        } catch (IOException e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el registro de venta: " + e.getMessage());
        } catch (Exception e) {
            WindowServices.showErrorDialog("Error", "Error inesperado al abrir la ventana: " + e.getMessage());
        }
    }
}