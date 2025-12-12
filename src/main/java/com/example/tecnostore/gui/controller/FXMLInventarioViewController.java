package com.example.tecnostore.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLInventarioViewController {
    @FXML private TextField searchField;
    @FXML private TableView<Object> inventoryTable;
    private ObservableList<Object> masterInventoryItems;

    @FXML
    private void onBuscar() {
        if (inventoryTable == null) {
            return;
        }

        ObservableList<?> currentItems = inventoryTable.getItems();
        if (masterInventoryItems == null && currentItems != null) {
            masterInventoryItems = FXCollections.observableArrayList(currentItems);
        }

        if (masterInventoryItems == null) {
            return;
        }

        String query = searchField != null ? searchField.getText() : null;
        if (query == null || query.isBlank()) {
            inventoryTable.setItems(FXCollections.observableArrayList(masterInventoryItems));
            return;
        }

        String normalizedQuery = query.trim().toLowerCase();
        FilteredList<Object> filteredItems = new FilteredList<>(masterInventoryItems,
                item -> item != null && item.toString().toLowerCase().contains(normalizedQuery));
        inventoryTable.setItems(filteredItems);
    }

    @FXML private void onRegistrarProducto() {
        Stage owner = new Stage();
        FXMLProductoModalController modal = new FXMLProductoModalController();
        // modal.showAndWait();
    }

    @FXML private void onEditar() {
        if (inventoryTable.getSelectionModel().getSelectedItem() != null) {
            Stage owner = new Stage();
            FXMLProductoModalController modal = new FXMLProductoModalController();
            // modal.showAndWait();
        } else {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un producto para editar.").showAndWait();
        }
    }

    @FXML private void onEliminar() {
        if (inventoryTable.getSelectionModel().getSelectedItem() != null) {
            Stage owner = new Stage();
            FXMLConfirmacionModalController modal = new FXMLConfirmacionModalController();
            // modal.showAndWait();
        } else {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un producto para eliminar.").showAndWait();
        }
    }

    @FXML
    private void initialize() {
        
    }
}
