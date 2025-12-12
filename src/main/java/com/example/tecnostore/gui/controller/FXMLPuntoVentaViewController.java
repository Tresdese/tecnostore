package com.example.tecnostore.gui.controller;

import com.example.tecnostore.logic.utils.WindowServices;
import com.example.tecnostore.logic.dto.VentaResumenDTO;
import com.example.tecnostore.logic.servicios.VentaService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // NECESARIO
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;     // NECESARIO
import javafx.scene.Scene;      // NECESARIO
import javafx.stage.Modality;   // NECESARIO
import javafx.stage.Stage;      // NECESARIO

import java.io.IOException;
import java.util.List;

public class FXMLPuntoVentaViewController {
    private final WindowServices windowServices = new WindowServices();

    @FXML private VBox rootContainer;

    @FXML private TableView<VentaResumenDTO> salesTable;

    @FXML private TableColumn<VentaResumenDTO, String> colUsuario;
    @FXML private TableColumn<VentaResumenDTO, Double> colMonto;

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
        if (colUsuario != null) {
            colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        }
        if (colMonto != null) {
            colMonto.setCellValueFactory(new PropertyValueFactory<>("montoTotal"));
        }
    }


    private void loadSales() {
        if (salesTable == null) return;
        try {
            List<VentaResumenDTO> ventas = ventaService.obtenerTodasLasVentas();
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