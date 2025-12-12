package com.example.tecnostore.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;

import java.io.IOException;

import com.example.tecnostore.logic.utils.WindowServices;
// import com.example.tecnostore.logic.dto.ProductoDTO; // Para uso en Edición/Baja

public class FXMLInventarioViewController {

    private final WindowServices windowServices = new WindowServices();

    @FXML private TableView<Object> inventoryTable;
    @FXML private TextField searchField;
    private ObservableList<Object> masterInventoryItems;


    @FXML
    private void onBuscar() {
        // ... [Lógica de búsqueda omitida] ...
    }

    // *** LÓGICA MANUAL: ALTA DE PRODUCTO ***
    @FXML private void onRegistrarProducto() {
        Stage owner = (Stage) inventoryTable.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLProductoModal.fxml"));
            Parent root = loader.load();

            FXMLProductoModalController controller = loader.getController();
            // controller.setIsNew(true);

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Registrar Producto");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            // cargarInventario();

        } catch (IOException e) {
            WindowServices.showErrorDialog("Error", "No se pudo abrir el formulario de registro: " + e.getMessage());
        }
    }

    // *** LÓGICA MANUAL: EDICIÓN DE PRODUCTO (Transferencia de Datos) ***
    @FXML private void onEditar() {
        Object selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Stage owner = (Stage) inventoryTable.getScene().getWindow();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLProductoModal.fxml"));
                Parent root = loader.load();

                FXMLProductoModalController controller = loader.getController();
                // ** Transferencia de Datos: controller.setProductoActual((ProductoDTO) selectedItem); **

                Stage modalStage = new Stage();
                modalStage.initOwner(owner);
                modalStage.initModality(Modality.WINDOW_MODAL);
                modalStage.setTitle("Editar Producto");
                modalStage.setScene(new Scene(root));
                modalStage.showAndWait();

                // cargarInventario();
            } catch (IOException e) {
                WindowServices.showErrorDialog("Error", "No se pudo abrir el formulario de edición: " + e.getMessage());
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un producto para editar.").showAndWait();
        }
    }

    // *** LÓGICA MANUAL: BAJA LÓGICA (Sin Retorno de Confirmación) ***
    @FXML private void onEliminar() {
        Object selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Stage owner = (Stage) inventoryTable.getScene().getWindow();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tecnostore/gui/views/FXMLConfirmacionModal.fxml"));
                Parent root = loader.load();

                FXMLConfirmacionModalController controller = loader.getController();

                // ** Transferencia de Datos: Usando setTitle/setMessage (Necesario) **
                String itemNombre = selectedItem.toString();
                controller.setTitle("Confirmar Baja de Producto");
                controller.setMessage("¿Está seguro de dar de baja el artículo: " + itemNombre + "?");

                Stage modalStage = new Stage();
                modalStage.initOwner(owner);
                modalStage.initModality(Modality.WINDOW_MODAL);
                modalStage.setTitle("Confirmación");
                modalStage.setScene(new Scene(root));
                modalStage.showAndWait();

                // Nota: La baja debe estar implementada en FXMLConfirmacionModalController.onConfirmar()
                // cargarInventario();

            } catch (Exception e) {
                WindowServices.showErrorDialog("Error", "Error al procesar la baja: " + e.getMessage());
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un producto para eliminar.").showAndWait();
        }
    }
}