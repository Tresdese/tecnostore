package com.example.tecnostore.gui.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLGestionUsuariosViewController {
    private static final String VIEW_BASE = "/com/example/tecnostore/gui/views/";

    @FXML private TextField searchField;
    @FXML private TableView<?> userTable;

    @FXML private void onBuscar() { /* TODO: search */ }

    @FXML
    private void onCrearNuevo() {
        openUsuarioModal("Crear nuevo usuario", true);
    }

    @FXML
    private void onEditarSeleccionado() {
        if (isSelectionEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un usuario en la tabla.").showAndWait();
            return;
        }
        openUsuarioModal("Editar usuario", false);
    }

    @FXML
    private void onDarDeBaja() {
        if (isSelectionEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debe seleccionar un usuario en la tabla.").showAndWait();
            return;
        }
        openBajaModal();
    }

    private void openUsuarioModal(String title, boolean isNew) {
        Stage owner = getOwnerStage();
        if (owner == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_BASE + "UsuarioModal.fxml"));
            Parent root = loader.load();
            UsuarioModalController controller = loader.getController();
            controller.setIsNew(isNew);
            controller.setOwner(owner);

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el modal de usuario: " + e.getMessage()).showAndWait();
        }
    }

    private void openBajaModal() {
        Stage owner = getOwnerStage();
        if (owner == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_BASE + "FXMLBajaUsuarioModal.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initOwner(owner);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle("Dar de baja usuario");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el modal de baja de usuario: " + e.getMessage()).showAndWait();
        }
    }

    private boolean isSelectionEmpty() {
        return userTable == null || userTable.getSelectionModel().getSelectedItem() == null;
    }

    private Stage getOwnerStage() {
        if (userTable == null || userTable.getScene() == null) {
            return null;
        }
        return (Stage) userTable.getScene().getWindow();
    }
}
