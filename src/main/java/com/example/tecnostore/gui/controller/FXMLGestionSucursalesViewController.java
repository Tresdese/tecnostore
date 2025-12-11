package com.example.tecnostore.gui.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLGestionSucursalesViewController {
    private static final String VIEW_BASE = "/com/example/tecnostore/gui/views/";

    @FXML private TableView<?> sucursalTable;

    @FXML
    private void onNuevaSucursal() {
        openSucursalModal("Nueva sucursal", true);
    }

    @FXML
    private void onEditarSeleccionada() {
        if (isSelectionEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar una sucursal.").showAndWait();
            return;
        }
        openSucursalModal("Editar sucursal", false);
    }

    @FXML
    private void onEliminarSucursal() {
        if (isSelectionEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar una sucursal.").showAndWait();
            return;
        }
        showEliminarModal();
    }

    private void openSucursalModal(String title, boolean isNew) {
        Stage owner = getOwnerStage();
        if (owner == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_BASE + "FXMLSucursalModal.fxml"));
            Parent root = loader.load();
            FXMLSucursalModalController controller = loader.getController();
            controller.setIsNew(isNew);
            controller.setOwner(owner);

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el modal de sucursal: " + e.getMessage()).showAndWait();
        }
    }

    private void showEliminarModal() {
        Stage owner = getOwnerStage();
        if (owner == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_BASE + "FXMLEliminarSucursalModal.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Eliminar sucursal");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana de eliminacion: " + e.getMessage()).showAndWait();
        }
    }

    private boolean isSelectionEmpty() {
        return sucursalTable == null || sucursalTable.getSelectionModel().getSelectedItem() == null;
    }

    private Stage getOwnerStage() {
        if (sucursalTable == null || sucursalTable.getScene() == null) {
            return null;
        }
        return (Stage) sucursalTable.getScene().getWindow();
    }
}
